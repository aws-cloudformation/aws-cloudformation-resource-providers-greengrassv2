/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.cluster;

import com.amazonaws.util.CollectionUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceResponse;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceResponse;
import software.amazon.awssdk.services.greengrassv2.model.UpdateClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.UpdateClusterResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateHandler extends BaseHandlerStd {

    private static final String TAG_OPERATION = "TagResource";
    private static final String UNTAG_OPERATION = "UntagResource";
    private static final String UPDATE_OPERATION = "UpdateCluster";
    private static final String NOT_UPDATABLE_MESSAGE_FMT = "%s property cannot be updated.";

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;
        logger.log(String.format("Updating cluster with clusterName %s for account %s.",
                request.getDesiredResourceState().getClusterName(), request.getAwsAccountId()));


        final List<String> previousAssociations = getListOrEmpty(request.getPreviousResourceState().getAssociatedMembers());
        final List<String> desiredAssociations = getListOrEmpty(request.getDesiredResourceState().getAssociatedMembers());

        // Imagine the following case:
        //   previousAssociations [a, b, c, d]
        //   desiredAssociations [b, c, e]
        //
        // Need to get to following operations:
        //   Disassociate [a, d]
        //   Associate [e]
        //
        // Achieved through:
        //  previousAssociations removeAll desiredAssociations = [a, d] (disassociate -> [b, c])
        //  desiredAssociations removeAll previousAssociations = [e] (associate -> [b, c, e])

        List<String> toDisassociate = new ArrayList<>(previousAssociations);
        toDisassociate.removeAll(desiredAssociations);

        List<String> toAssociate = new ArrayList<>(desiredAssociations);
        toAssociate.removeAll(previousAssociations);

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateClusterNamePresent)
                .then(progress -> validateIfClusterResourceExists(proxyClient, progress))
                .then(progress -> validateCreateOnlyPropertiesNotChanged(request.getDesiredResourceState(),
                        request.getPreviousResourceState(), progress)
                )
                .then(progress -> updateTags(proxy, proxyClient, request.getDesiredResourceTags(), request.getPreviousResourceTags(), progress))
                .then(progress -> {
                    if (CollectionUtils.isNullOrEmpty(toDisassociate)) {
                        return ProgressEvent.progress(progress.getResourceModel(), callbackContext);
                    } else {
                        return proxy.initiate("AWS-GreengrassV2-Cluster::Disassociate", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(resourceModel ->
                                        Translator.translateToDisassociateRequest(resourceModel, new ArrayList<>(toDisassociate)))
                                .makeServiceCall(this::disassociateCoreDevices)
                                .progress();
                    }
                })
                .then(progress -> {
                    if (CollectionUtils.isNullOrEmpty(toAssociate)) {
                        return ProgressEvent.progress(progress.getResourceModel(), callbackContext);
                    } else {
                        return proxy.initiate("AWS-GreengrassV2-Cluster::Associate", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(resourceModel ->
                                        Translator.translateToAssociateRequest(resourceModel, new ArrayList<>(toAssociate)))
                                .makeServiceCall(this::associateCoreDevices)
                                .progress();
                    }
                })
                .then(progress ->
                        proxy.initiate("AWS-GreengrassV2-Cluster::Update", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(resourceModel ->
                                        Translator.translateToUpdateRequest(resourceModel))
                                .makeServiceCall(this::updateCluster)
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private List<String> getListOrEmpty(final List<String> associatedMembers) {
        if (CollectionUtils.isNullOrEmpty(associatedMembers)) {
            return ImmutableList.of();
        }
        return new ArrayList<>(associatedMembers);
    }

    private ProgressEvent<ResourceModel, CallbackContext> validateCreateOnlyPropertiesNotChanged(final ResourceModel desiredResourceState,
                                                                                                 final ResourceModel previousResourceState,
                                                                                                 final ProgressEvent<ResourceModel, CallbackContext> progressEvent) {
        if (!Objects.equals(desiredResourceState.getClusterName(), previousResourceState.getClusterName())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, String.format(NOT_UPDATABLE_MESSAGE_FMT, "ClusterName"));
        }

        return progressEvent;
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateTags(final AmazonWebServicesClientProxy proxy,
                                                                     final ProxyClient<GreengrassV2Client> proxyClient,
                                                                     final Map<String, String> desiredResourceTags,
                                                                     final Map<String, String> previousResourceTags,
                                                                     final ProgressEvent<ResourceModel, CallbackContext> progress) {
        final MapDifference<String, String> difference = Maps.difference(desiredResourceTags, previousResourceTags);
        final Map<String, String> tagsToUpdate = desiredResourceTags.entrySet()
                .stream()
                .filter(e -> difference.entriesDiffering()
                        .containsKey(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final Map<String, String> tagsToAdd = difference.entriesOnlyOnLeft();
        tagsToUpdate.putAll(tagsToAdd);
        final Map<String, String> tagsToRemove = difference.entriesOnlyOnRight();

        final String clusterArn = progress.getResourceModel().getClusterArn();

        return progress
                .then(progressEvent ->{
                    if (tagsToRemove.isEmpty()) {
                        return progressEvent;
                    }
                    return proxy.initiate("AWS-GreengrassV2-Cluster::Update::RemoveTags", proxyClient, progressEvent.getResourceModel(), progressEvent.getCallbackContext())
                            .translateToServiceRequest(model -> Translator.translateToUntagResourceRequest(clusterArn, tagsToRemove))
                            .makeServiceCall(this::untagResource)
                            .progress();
                })
                .then(progressEvent -> {
                    if (tagsToUpdate.isEmpty()) {
                        return progressEvent;
                    }
                    return proxy.initiate("AWS-GreengrassV2-Cluster::Update::AddTags", proxyClient, progressEvent.getResourceModel(), progressEvent.getCallbackContext())
                            .translateToServiceRequest(model -> Translator.translateToTagResourceRequest(clusterArn, tagsToUpdate))
                            .makeServiceCall(this::tagResource)
                            .progress();
                });
    }

    private TagResourceResponse tagResource(final TagResourceRequest request, final ProxyClient<GreengrassV2Client> client) {
        TagResourceResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::tagResource);
        } catch (Exception ex) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(TAG_OPERATION, request.resourceArn(), ex);
        }
        logger.log(String.format("%s successfully tagged cluster with ARN: %s", ResourceModel.TYPE_NAME,
                request.resourceArn()));
        return awsResponse;
    }

    private UntagResourceResponse untagResource(final UntagResourceRequest request, final ProxyClient<GreengrassV2Client> client) {
        UntagResourceResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::untagResource);
        } catch (Exception ex) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(UNTAG_OPERATION, request.resourceArn(), ex);
        }
        logger.log(String.format("%s successfully untagged cluster with ARN: %s", ResourceModel.TYPE_NAME,
                request.resourceArn()));
        return awsResponse;
    }

    private UpdateClusterResponse updateCluster(final UpdateClusterRequest request, final ProxyClient<GreengrassV2Client> client) {
        UpdateClusterResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::updateCluster);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(UPDATE_OPERATION, request.clusterName(), e);
        }
        logger.log(String.format("%s successfully updated cluster with clusterName: %s", ResourceModel.TYPE_NAME,
                request.clusterName()));
        return awsResponse;
    }
}

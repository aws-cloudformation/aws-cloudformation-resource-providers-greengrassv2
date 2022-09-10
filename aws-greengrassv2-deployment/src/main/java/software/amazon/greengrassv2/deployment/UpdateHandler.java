/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import software.amazon.awssdk.arns.Arn;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;
    private static final String NOT_UPDATABLE_MESSAGE_FMT = "%s property cannot be updated.";
    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;

        final String deploymentArn = getDeploymentArnFromRequest(request);

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateDeploymentIdPresent)
                .then(progressEvent -> validateIfDeploymentResourceExists(proxyClient, progressEvent))
                .then(progressEvent -> validateCreateOnlyPropertiesNotChanged(
                        request.getDesiredResourceState(), request.getPreviousResourceState(), progressEvent))
                .then(progress -> updateTags(proxy, proxyClient, deploymentArn,
                        request.getDesiredResourceTags(), request.getPreviousResourceTags(), progress))
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> validateIfDeploymentResourceExists(final ProxyClient<GreengrassV2Client> proxyClient,
                                                                                             ProgressEvent<ResourceModel, CallbackContext> progressEvent) {
        try {
            proxyClient.injectCredentialsAndInvokeV2(Translator.translateToReadRequest(progressEvent.getResourceModel()),
                    proxyClient.client()::getDeployment);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnException("UpdateDeployment", e);
        }
        return progressEvent;
    }

    private ProgressEvent<ResourceModel, CallbackContext> validateCreateOnlyPropertiesNotChanged(
            ResourceModel desiredResourceState,
            ResourceModel previousResourceState,
            ProgressEvent<ResourceModel, CallbackContext> progressEvent) {

        if (!Objects.equals(desiredResourceState.getTargetArn(), previousResourceState.getTargetArn())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, String.format(NOT_UPDATABLE_MESSAGE_FMT, "TargetArn"));
        }

        if (!Objects.equals(desiredResourceState.getComponents(), previousResourceState.getComponents())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, String.format(NOT_UPDATABLE_MESSAGE_FMT, "Component"));
        }

        if (!Objects.equals(desiredResourceState.getIotJobConfiguration(), previousResourceState.getIotJobConfiguration())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, String.format(NOT_UPDATABLE_MESSAGE_FMT, "IotJobConfiguration"));
        }

        if (!Objects.equals(desiredResourceState.getDeploymentName(), previousResourceState.getDeploymentName())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, String.format(NOT_UPDATABLE_MESSAGE_FMT, "DeploymentName"));
        }

        if (!Objects.equals(desiredResourceState.getDeploymentPolicies(), previousResourceState.getDeploymentPolicies())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, String.format(NOT_UPDATABLE_MESSAGE_FMT, "DeploymentPolicies"));
        }

        return progressEvent;
    }

    private ProgressEvent<ResourceModel, CallbackContext> updateTags(
            AmazonWebServicesClientProxy proxy,
            ProxyClient<GreengrassV2Client> proxyClient,
            String deploymentArn,
            Map<String, String> desiredResourceTags,
            Map<String, String> previousResourceTags,
            ProgressEvent<ResourceModel, CallbackContext> progress) {

        final MapDifference<String, String> difference = Maps.difference(desiredResourceTags, previousResourceTags);
        final Map<String, String> tagsToUpdate = desiredResourceTags.entrySet().stream().filter(e->difference.entriesDiffering().containsKey(e.getKey())).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final Map<String, String> tagsToAdd = difference.entriesOnlyOnLeft();
        tagsToUpdate.putAll(tagsToAdd);
        final Map<String, String> tagsToRemove = difference.entriesOnlyOnRight();

        return progress
                .then(progressEvent -> {
                    if (!tagsToUpdate.isEmpty()) {
                        return proxy.initiate("AWS-GreengrassV2-Deployment::Update::AddTags", proxyClient, progressEvent.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(model -> Translator.translateToTagResourceRequest(deploymentArn, tagsToUpdate))
                                .makeServiceCall((awsRequest, client) -> {
                                    try {
                                        return client.injectCredentialsAndInvokeV2(awsRequest, client.client()::tagResource);
                                    } catch (Exception ex) {
                                        throw ExceptionTranslator.translateToCfnExceptionForCreatedResource("TagResource", deploymentArn, ex);
                                    }
                                })
                                .progress();
                    } else {
                        return progress;
                    }
                })
                .then(progressEvent ->{
                    if (!tagsToRemove.isEmpty()) {
                        return proxy.initiate("AWS-GreengrassV2-Deployment::Update::RemoveTags", proxyClient, progressEvent.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(model -> Translator.translateToUntagResourceRequest(deploymentArn, tagsToRemove))
                                .makeServiceCall((awsRequest, client) -> {
                                    try {
                                        return client.injectCredentialsAndInvokeV2(awsRequest, client.client()::untagResource);
                                    } catch (Exception ex) {
                                        throw ExceptionTranslator.translateToCfnExceptionForCreatedResource("UntagResource", deploymentArn, ex);
                                    }
                                })
                                .progress();
                    } else {
                        return progress;
                    }
                });
    }

    private static String getDeploymentArnFromRequest(final ResourceHandlerRequest<ResourceModel> request) {
        return Arn.builder()
                .partition(request.getAwsPartition())
                .region(request.getRegion())
                .accountId(request.getAwsAccountId())
                .service("greengrass")
                .resource("deployments:" + request.getDesiredResourceState().getDeploymentId())
                .build()
                .toString();
    }
}

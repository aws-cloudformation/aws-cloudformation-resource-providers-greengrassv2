/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.cluster;

import com.amazonaws.util.CollectionUtils;
import com.google.common.collect.ImmutableList;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.AssociateCoreDevicesWithClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.AssociateCoreDevicesWithClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.DisassociateCoreDevicesFromClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.DisassociateCoreDevicesFromClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.stream.Collectors;

// Placeholder for the functionality that could be shared across Create/Read/Update/Delete/List Handlers

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {

    private static final String READ_OPERATION = "GetCluster";
    private static final String ASSOCIATE_OPERATION = "AssociateCoreDevicesWithCluster";
    private static final String DISASSOCIATE_OPERATION = "DisassociateCoreDevicesFromCluster";
    private static final String LIST_ASSOCIATED_OPERATION = "ListCoreDevicesAssociatedWithCluster";

    protected Logger logger;

    @Override
    public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {
        return handleRequest(
                proxy,
                request,
                callbackContext != null ? callbackContext : new CallbackContext(),
                proxy.newProxy(ClientBuilder::getClient),
                logger);
    }

    protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger);

    protected ProgressEvent<ResourceModel, CallbackContext> validateClusterNamePresent(ProgressEvent<ResourceModel, CallbackContext> progress) {
        final ResourceModel resourceModel = progress.getResourceModel();
        if (resourceModel.getClusterName() == null) {
            return ProgressEvent.failed(resourceModel, progress.getCallbackContext(), HandlerErrorCode.NotFound, "Provided clusterName was null");
        }
        return progress;
    }

    protected ProgressEvent<ResourceModel, CallbackContext> validateClusterArnNotPresent(ProgressEvent<ResourceModel, CallbackContext> progress) {
        final ResourceModel resourceModel = progress.getResourceModel();
        if (resourceModel.getClusterArn() != null) {
            return ProgressEvent.failed(resourceModel, progress.getCallbackContext(), HandlerErrorCode.InvalidRequest,
                    "ClusterArn should not be provided in create request");
        }
        return progress;
    }

    protected ProgressEvent<ResourceModel, CallbackContext> validateIfClusterResourceExists(final ProxyClient<GreengrassV2Client> proxyClient,
                                                                                            ProgressEvent<ResourceModel, CallbackContext> progress) {
        try {
            GetClusterResponse getClusterResponse = proxyClient.injectCredentialsAndInvokeV2(
                    Translator.translateToReadRequest(progress.getResourceModel()), proxyClient.client()::getCluster);
            progress.getResourceModel().setClusterArn(getClusterResponse.clusterArn());
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnException(READ_OPERATION, e);
        }
        return progress;
    }

    protected List<String> getAssociatedMembers(ListCoreDevicesAssociatedWithClusterResponse response) {
        if (CollectionUtils.isNullOrEmpty(response.members())) {
            return ImmutableList.of();
        }

        return response.members().stream()
                .map(member -> member.coreDeviceThingName())
                .collect(Collectors.toList());
    }

    protected AssociateCoreDevicesWithClusterResponse associateCoreDevices(final AssociateCoreDevicesWithClusterRequest request,
                                                                           final ProxyClient<GreengrassV2Client> client) {
        if (request == null || CollectionUtils.isNullOrEmpty(request.entries())) {
            return AssociateCoreDevicesWithClusterResponse.builder().build();
        }

        AssociateCoreDevicesWithClusterResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::associateCoreDevicesWithCluster);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(ASSOCIATE_OPERATION, request.clusterName(), e);
        }
        logger.log(String.format("%s successfully associated core devices with clusterName: %s", ResourceModel.TYPE_NAME,
                request.clusterName()));
        return awsResponse;
    }

    protected DisassociateCoreDevicesFromClusterResponse disassociateCoreDevices(final DisassociateCoreDevicesFromClusterRequest request,
                                                                                 final ProxyClient<GreengrassV2Client> client) {
        if (request == null || CollectionUtils.isNullOrEmpty(request.entries())) {
            return DisassociateCoreDevicesFromClusterResponse.builder().build();
        }

        DisassociateCoreDevicesFromClusterResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::disassociateCoreDevicesFromCluster);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(DISASSOCIATE_OPERATION, request.clusterName(), e);
        }
        logger.log(String.format("%s successfully disassociated core devices with clusterName: %s", ResourceModel.TYPE_NAME,
                request.clusterName()));
        return awsResponse;
    }

    protected ListCoreDevicesAssociatedWithClusterResponse listAssociated(final ListCoreDevicesAssociatedWithClusterRequest request,
                                                                          final ProxyClient<GreengrassV2Client> client) {
        ListCoreDevicesAssociatedWithClusterResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::listCoreDevicesAssociatedWithCluster);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(LIST_ASSOCIATED_OPERATION, request.clusterName(), e);
        }
        logger.log(String.format("%s successfully listed associated members for clusterName: %s", ResourceModel.TYPE_NAME,
                request.clusterName()));
        return awsResponse;
    }
}

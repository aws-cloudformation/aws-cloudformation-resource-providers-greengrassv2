/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */


package software.amazon.greengrassv2.cluster;

import com.amazonaws.util.CollectionUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.CreateClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.CreateClusterResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandlerStd {

    private static final String OPERATION = "CreateCluster";

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;

        logger.log(String.format("Creating cluster with clusterName %s for account %s.",
                request.getDesiredResourceState().getClusterName(), request.getAwsAccountId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateClusterArnNotPresent)
                .then(this::validateClusterNamePresent)
                .then(progress ->
                    proxy.initiate("AWS-GreengrassV2-Cluster::Create", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                            .translateToServiceRequest(resourceModel ->
                                    Translator.translateToCreateRequest(resourceModel, request.getDesiredResourceTags()))
                            .makeServiceCall(this::createCluster)
                            .done((describeRequest, response, proxyInvocation, model, context) -> {
                                progress.getResourceModel().setClusterArn(response.clusterArn());
                                return ProgressEvent.progress(progress.getResourceModel(), context);
                            })
                )
                .then(progress -> {
                    if (CollectionUtils.isNullOrEmpty(request.getDesiredResourceState().getAssociatedMembers())) {
                        return ProgressEvent.defaultSuccessHandler(progress.getResourceModel());
                    } else {
                        return proxy.initiate("AWS-GreengrassV2-Cluster::Associate", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(resourceModel ->
                                        Translator.translateToAssociateRequest(resourceModel, request.getDesiredResourceState().getAssociatedMembers()))
                                .makeServiceCall(this::associateCoreDevices)
                                .progress();
                    }
                })
                .then(progress -> ProgressEvent.defaultSuccessHandler(progress.getResourceModel()));
    }

    private CreateClusterResponse createCluster(final CreateClusterRequest request, final ProxyClient<GreengrassV2Client> client) {
        CreateClusterResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::createCluster);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnException(OPERATION, e);
        }
        logger.log(String.format("%s successfully created with clusterName: %s", ResourceModel.TYPE_NAME, request.clusterName()));
        return awsResponse;
    }
}

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.cluster;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {

    private static final String OPERATION = "GetCluster";

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;
        logger.log(String.format("Getting cluster with clusterName %s for account %s.",
                request.getDesiredResourceState().getClusterName(), request.getAwsAccountId()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateClusterNamePresent)
                .then(progress -> proxy.initiate("AWS-GreengrassV2-Cluster::Read", proxyClient, request.getDesiredResourceState(), callbackContext)
                        .translateToServiceRequest(Translator::translateToReadRequest)
                        .makeServiceCall(this::getCluster)
                        .done((describeRequest, response, proxyInvocation, model, context) ->
                             ProgressEvent.progress(Translator.translateFromReadResponse(response), context)
                        )
                )
                .then(progress -> proxy.initiate("AWS-GreengrassV2-Cluster::ListAssociated", proxyClient, request.getDesiredResourceState(), callbackContext)
                        .translateToServiceRequest(resourceModel ->
                                Translator.translateToListAssociatedRequest(resourceModel.getClusterName()))
                        .makeServiceCall(this::listAssociated)
                        .done((describeRequest, response, proxyInvocation, model, context) -> {
                            // ClusterName not included in GetCluster output, should consider adding this in SDK
                            progress.getResourceModel().setClusterName(request.getDesiredResourceState().getClusterName());
                            progress.getResourceModel().setAssociatedMembers(getAssociatedMembers(response));
                            return ProgressEvent.progress(progress.getResourceModel(), context);
                        })
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(progress.getResourceModel()));
    }

    private GetClusterResponse getCluster(final GetClusterRequest request, final ProxyClient<GreengrassV2Client> client) {
        GetClusterResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::getCluster);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(OPERATION, request.clusterName(), e);
        }
        logger.log(String.format("%s has successfully been read. %s", ResourceModel.TYPE_NAME, awsResponse));
        return awsResponse;
    }
}

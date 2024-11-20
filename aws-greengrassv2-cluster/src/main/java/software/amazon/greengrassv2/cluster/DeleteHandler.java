/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */


package software.amazon.greengrassv2.cluster;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.DeleteClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.DeleteClusterResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {

    private static final String OPERATION = "DeleteCluster";

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;
        logger.log(String.format("Deleting cluster with clusterName %s for account %s",
                request.getDesiredResourceState().getClusterName(), request.getAwsAccountId()));

        // Associated core devices should be disassociated before deleting
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateClusterNamePresent)
                .then(progress -> validateIfClusterResourceExists(proxyClient, progress))
                .then(progress ->
                    proxy.initiate("AWS-GreengrassV2-Cluster::ListAssociated", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                            .translateToServiceRequest(resourceModel ->
                                    Translator.translateToListAssociatedRequest(resourceModel.getClusterName()))
                            .makeServiceCall(this::listAssociated)
                            .done((describeRequest, response, proxyInvocation, model, context) -> {
                                progress.getResourceModel().setAssociatedMembers(getAssociatedMembers(response));
                                return ProgressEvent.progress(model, context);
                            })
                )
                .then(progress ->
                    proxy.initiate("AWS-GreengrassV2-Cluster::Disassociate", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                            .translateToServiceRequest(resourceModel ->
                                    Translator.translateToDisassociateRequest(resourceModel, resourceModel.getAssociatedMembers()))
                            .makeServiceCall(this::disassociateCoreDevices)
                            .progress()
                )
                .then(progress ->
                    proxy.initiate("AWS-GreengrassV2-Cluster::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                            .translateToServiceRequest(resourceModel ->
                                    Translator.translateToDeleteRequest(resourceModel))
                            .makeServiceCall(this::deleteCluster)
                            .progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }

    private DeleteClusterResponse deleteCluster(final DeleteClusterRequest request, final ProxyClient<GreengrassV2Client> client) {
        DeleteClusterResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::deleteCluster);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(OPERATION, request.clusterName(), e);
        }
        logger.log(String.format("%s successfully deleted with clusterName: %s", ResourceModel.TYPE_NAME, request.clusterName()));
        return awsResponse;
    }
}

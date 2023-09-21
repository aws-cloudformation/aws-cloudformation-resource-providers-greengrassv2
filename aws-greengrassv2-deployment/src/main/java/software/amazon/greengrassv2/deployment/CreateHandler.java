/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.CreateDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.CreateDeploymentResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;
        logger.log(String.format("Creating deployment for account %s: %s",
                request.getAwsAccountId(), request.getDesiredResourceState().toString()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateDeploymentIdNotPresent)
                .then(progress ->
                        proxy.initiate("AWS-GreengrassV2-Deployment::Create", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(resourceModel ->
                                        Translator.translateToCreateRequest(resourceModel, request.getDesiredResourceTags()))
                                .makeServiceCall(this::createDeployment)
                                .done((describeRequest, response, proxyInvocation, model, context) -> {
                                    model.setDeploymentId(response.deploymentId());
                                    return ProgressEvent.progress(model, context);
                                })
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private CreateDeploymentResponse createDeployment(final CreateDeploymentRequest request, final ProxyClient<GreengrassV2Client> client) {

        CreateDeploymentResponse awsResponse;
        try {
            awsResponse = client.injectCredentialsAndInvokeV2(request, client.client()::createDeployment);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnException("CreateDeployment", e);
        }
        logger.log(String.format("%s successfully created with deploymentId: %s.", ResourceModel.TYPE_NAME, awsResponse.deploymentId()));
        return awsResponse;
    }

    private ProgressEvent<ResourceModel, CallbackContext> validateDeploymentIdNotPresent(ProgressEvent<ResourceModel, CallbackContext> progress) {
        final ResourceModel resourceModel = progress.getResourceModel();
        if (resourceModel.getDeploymentId() != null) {
            return ProgressEvent.failed(resourceModel, progress.getCallbackContext(), HandlerErrorCode.InvalidRequest,
                    "DeploymentId should not be provided in create request");
        }
        return progress;
    }
}

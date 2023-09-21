/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.GetDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetDeploymentResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;
        logger.log(String.format("Reading deployment for account %s: %s",
                request.getAwsAccountId(), request.getDesiredResourceState().toString()));

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateDeploymentIdPresent)
                .then(event -> proxy.initiate("AWS-GreengrassV2-Deployment::Read", proxyClient, request.getDesiredResourceState(), callbackContext)
                        .translateToServiceRequest(Translator::translateToReadRequest)
                        .makeServiceCall(this::getDeployment)
                        .done(awsResponse -> ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(awsResponse)))
                );
    }

    private GetDeploymentResponse getDeployment(final GetDeploymentRequest request, final ProxyClient<GreengrassV2Client> client) {
        GetDeploymentResponse getDeploymentResponse;
        try {
            getDeploymentResponse = client.injectCredentialsAndInvokeV2(request, client.client()::getDeployment);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource("GetDeployment", request.deploymentId(), e);
        }
        logger.log(String.format("%s has successfully been read. %s", ResourceModel.TYPE_NAME, getDeploymentResponse));
        return getDeploymentResponse;
    }
}

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;

        // Deployment should be cancelled before deleted.
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-GreengrassV2-Deployment::Cancel", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(Translator::translateToCancelRequest)
                                .makeServiceCall((awsRequest, client) ->  {
                                    try {
                                        return client.injectCredentialsAndInvokeV2(awsRequest, client.client()::cancelDeployment);
                                    } catch (Exception ex) {
                                        throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(
                                                "CancelDeployment", request.getDesiredResourceState()
                                                        .getDeploymentId(), ex);
                                    }
                                })
                                .progress())
                .then(progress ->
                        proxy.initiate("AWS-GreengrassV2-Deployment::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(Translator::translateToDeleteRequest)
                                .makeServiceCall((awsRequest, client) ->  {
                                    try {
                                        return client.injectCredentialsAndInvokeV2(awsRequest, client.client()::deleteDeployment);
                                    } catch (Exception ex) {
                                        throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(
                                                "DeleteDeployment", request.getDesiredResourceState()
                                                        .getDeploymentId(), ex);
                                    }
                                })
                                .progress())
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }
}

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.ListDeploymentsRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListDeploymentsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandlerStd {
    private Logger logger;

    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;

        logger.log(String.format("Listing deployment for account %s.", request.getAwsAccountId()));

        final ListDeploymentsRequest listDeploymentsRequest =
                Translator.translateToListRequest(request.getNextToken());

        ListDeploymentsResponse listDeploymentsResponse;
        try {
            listDeploymentsResponse = proxyClient.injectCredentialsAndInvokeV2(listDeploymentsRequest, proxyClient.client()::listDeployments);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnException("ListDeployments", e);
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(Translator.translateFromListResponse(listDeploymentsResponse))
                .nextToken(listDeploymentsResponse.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
    }
}

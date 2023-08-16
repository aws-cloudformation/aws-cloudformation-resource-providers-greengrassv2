/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.cluster;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.ListClustersRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListClustersResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandlerStd {

    private static final String OPERATION = "ListClusters";

    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<GreengrassV2Client> proxyClient,
            final Logger logger) {

        this.logger = logger;
        logger.log(String.format("Listing clusters for account %s.", request.getAwsAccountId()));

        final ListClustersRequest listClustersRequest = Translator.translateToListRequest(
                request.getNextToken());

        ListClustersResponse awsResponse;
        try {
            awsResponse = proxyClient.injectCredentialsAndInvokeV2(listClustersRequest, proxyClient.client()::listClusters);
        } catch (final AwsServiceException e) {
            throw ExceptionTranslator.translateToCfnException(OPERATION, e);
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(Translator.translateFromListResponse(awsResponse))
                .nextToken(awsResponse.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
    }
}

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.services.greengrassv2.model.ListComponentVersionsRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListComponentVersionsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel desiredResourceModel = request.getDesiredResourceState();

        final ListComponentVersionsRequest listComponentVersionsRequest =
                Translator.translateToListRequest(request, request.getNextToken());

        ListComponentVersionsResponse listComponentVersionsResponse =
                proxy.injectCredentialsAndInvokeV2(listComponentVersionsRequest, ClientBuilder.getClient()::listComponentVersions);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(Translator.translateFromListResponse(listComponentVersionsResponse))
                .nextToken(listComponentVersionsResponse.nextToken())
                .status(OperationStatus.SUCCESS)
                .build();
    }
}

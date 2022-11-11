/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
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
        logger.log(String.format("Getting component version with name %s and version %s for account %s.",
                request.getDesiredResourceState().getComponentName(),
                request.getDesiredResourceState().getComponentVersion(), request.getAwsAccountId()));


        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateArnPresent)
                .then(event -> proxy.initiate("AWS-GreengrassV2-ComponentVersion::Read::DescribeComponent", proxyClient, request.getDesiredResourceState(), callbackContext)
                        .translateToServiceRequest(Translator::translateToReadRequest)
                        .makeServiceCall((awsRequest, client) -> {
                            try {
                                return client.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeComponent);
                            } catch (Exception ex) {
                                throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(
                                        "DescribeComponent", request.getDesiredResourceState().getArn(), ex);
                            }
                        }).done(describeComponentResponse -> ProgressEvent.progress(Translator.translateFromReadResponse(describeComponentResponse), callbackContext)))
                // Read Request should list out the tags for the resource. DescribeComponent API doesn't include tags in the response
                // For this we use the ListTagsForResource API to add tags to the output of DescribeComponentResponse from above.
                // Tags information is only added if it's not empty.
                .then(event -> proxy.initiate("AWS-GreengrassV2-ComponentVersion::Read::ListTagsForResource", proxyClient, request.getDesiredResourceState(), callbackContext)
                        .translateToServiceRequest(Translator::translateToListTagsRequest)
                        .makeServiceCall((awsRequest, client) -> {
                            try {
                                return client.injectCredentialsAndInvokeV2(awsRequest, client.client()::listTagsForResource);
                            } catch (Exception ex) {
                                throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(
                                        "ListTagsForResource", request.getDesiredResourceState().getArn(), ex);
                            }
                        })
                        .done(listTagsForResourceResponse -> ProgressEvent.success(Translator.translateFromListTagsResponse(event.getResourceModel(), listTagsForResourceResponse), callbackContext)));
    }

}

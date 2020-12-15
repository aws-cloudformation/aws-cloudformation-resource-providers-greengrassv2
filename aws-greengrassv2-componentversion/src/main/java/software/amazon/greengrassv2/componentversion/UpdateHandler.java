/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.componentversion;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;
import java.util.Objects;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<GreengrassV2Client> proxyClient,
        final Logger logger) {

        this.logger = logger;

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(this::validateArnPresent)
                .then(progressEvent -> validateCreateOnlyPropertiesNotChanged(
                        request.getDesiredResourceState(), request.getPreviousResourceState(), progressEvent))
                .then(progress -> updateTags(proxy, proxyClient, request.getDesiredResourceState().getArn(),
                        request.getDesiredResourceTags(), request.getPreviousResourceTags(), progress))
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> validateCreateOnlyPropertiesNotChanged(
            ResourceModel desiredResourceState,
            ResourceModel previousResourceState,
            ProgressEvent<ResourceModel, CallbackContext> progressEvent) {

        if (!Objects.equals(desiredResourceState.getArn(), previousResourceState.getArn())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, "Arn property cannot be updated.");
        }

        if (!Objects.equals(desiredResourceState.getComponentName(), previousResourceState.getComponentName())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, "ComponentName property cannot be updated.");
        }

        if (!Objects.equals(desiredResourceState.getComponentVersion(), previousResourceState.getComponentVersion())) {
            return ProgressEvent.failed(previousResourceState, progressEvent.getCallbackContext(),
                    HandlerErrorCode.NotUpdatable, "ComponentVersion property cannot be updated.");
        }

        return progressEvent;
    }


    private ProgressEvent<ResourceModel, CallbackContext> updateTags(
            AmazonWebServicesClientProxy proxy,
            ProxyClient<GreengrassV2Client> proxyClient,
            String arn,
            Map<String, String> desiredResourceTags,
            Map<String, String> previousResourceTags,
            ProgressEvent<ResourceModel, CallbackContext> progress) {

        final MapDifference<String, String> difference = Maps.difference(desiredResourceTags, previousResourceTags);
        final Map<String, String> tagsToAdd = difference.entriesOnlyOnLeft();
        final Map<String, String> tagsToRemove = difference.entriesOnlyOnRight();

        return progress
                .then(progressEvent -> {
                    if (!tagsToAdd.isEmpty()) {
                        return proxy.initiate("AWS-GreengrassV2-ComponentVersion::Update::AddTags", proxyClient, progressEvent.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(model -> Translator.translateToTagResourceRequest(model.getArn(), tagsToAdd))
                                .makeServiceCall((awsRequest, client) -> {
                                    try {
                                        return client.injectCredentialsAndInvokeV2(awsRequest, client.client()::tagResource);
                                    } catch (Exception ex) {
                                        throw ExceptionTranslator.translateToCfnExceptionForCreatedResource("TagResource", arn, ex);
                                    }
                                })
                                .progress();
                    } else {
                        return progress;
                    }
                })
                .then(progressEvent ->{
                    if (!tagsToRemove.isEmpty()) {
                        return proxy.initiate("AWS-GreengrassV2-ComponentVersion::Update::RemoveTags", proxyClient, progressEvent.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(model -> Translator.translateToUntagResourceRequest(model.getArn(), tagsToRemove))
                                .makeServiceCall((awsRequest, client) -> {
                                    try {
                                        return client.injectCredentialsAndInvokeV2(awsRequest, client.client()::untagResource);
                                    } catch (Exception ex) {
                                        throw ExceptionTranslator.translateToCfnExceptionForCreatedResource("UntagResource", arn, ex);
                                    }
                                })
                                .progress();
                    } else {
                        return progress;
                    }
                });
    }
}

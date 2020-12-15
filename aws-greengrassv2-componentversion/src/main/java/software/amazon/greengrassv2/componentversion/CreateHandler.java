/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.CloudComponentState;
import software.amazon.awssdk.services.greengrassv2.model.ConflictException;
import software.amazon.awssdk.services.greengrassv2.model.CreateComponentVersionResponse;
import software.amazon.awssdk.services.greengrassv2.model.DescribeComponentRequest;
import software.amazon.awssdk.services.greengrassv2.model.DescribeComponentResponse;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnNotStabilizedException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.CallChain;
import software.amazon.cloudformation.proxy.Delay;
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

        final ResourceModel desiredResourceState = request.getDesiredResourceState();

        return ProgressEvent.progress(desiredResourceState, callbackContext)
                .then(this::validateReadOnlyPropertiesNotPresent)
                .then(this::validateOnlyOneRecipeSourcePresent)
                .then(progress ->
                        proxy.initiate("AWS-GreengrassV2-ComponentVersion::Create", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                                .translateToServiceRequest(resourceModel ->
                                        Translator.translateToCreateRequest(resourceModel, request.getDesiredResourceTags()))
                                .makeServiceCall((awsRequest, client) -> {
                                    try {
                                        final CreateComponentVersionResponse response = client.injectCredentialsAndInvokeV2(awsRequest, client.client()::createComponentVersion);
                                        logger.log("Create successful.");
                                        return response;
                                    } catch (ConflictException ex) {
                                        throw new CfnAlreadyExistsException(ex);
                                    } catch (Exception ex) {
                                        throw ExceptionTranslator.translateToCfnException("CreateComponentVersion", ex);
                                    }
                                })
                                .stabilize((describeRequest, response, proxyInvocation, model, context) -> isStabilized(response.arn(), proxyClient))
                                .done((describeRequest, response, proxyInvocation, model, context) -> {
                                    // As soon as we successfully create a ComponentVersion in the Greengrass service,
                                    // save identifiers into the resource model & report them to CloudFormation, sucn
                                    // that CloudFormation can clean up the resource as needed
                                    if (model.getArn() == null) {
                                        model.setArn(response.arn());
                                        model.setComponentName(response.componentName());
                                        model.setComponentVersion(response.componentVersion());
                                        context.setResourceCreated(true);

                                        // Specify a callback delay here in order to cause the ProgressEvent chain
                                        // to bypass subsequent steps for now, such that the current
                                        // state is immediately reported back to CloudFormation as IN_PROGRESS with
                                        // the ARN saved. This will allow CloudFormation to delete the resource if
                                        // any problems occur after this point.
                                        return ProgressEvent.defaultInProgressHandler(context, 1, model);
                                    } else {
                                        // Since this `done` method is invoked every time this handler is called (regardless
                                        // of whether this service call chain has finished yet or not), when the ARN has
                                        // already been set, don't set a delay here, such that the ProgressEvent chain
                                        // can advance to the next step.
                                        return ProgressEvent.progress(model, context);
                                    }
                                })
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private ProgressEvent<ResourceModel, CallbackContext> validateReadOnlyPropertiesNotPresent(ProgressEvent<ResourceModel, CallbackContext> progressEvent) {
        // We only validate that read-only identifier properties are not provided on the first creation event
        // After the resource has been created, these may exist in the model, since they are set during the
        // creation process
        if (progressEvent.getCallbackContext().resourceCreated) {
            return progressEvent;
        }

        final ResourceModel model = progressEvent.getResourceModel();

        if (model.getArn() != null) {
            return ProgressEvent.failed(null, null, HandlerErrorCode.InvalidRequest, "Arn is a read-only property.");
        }

        if (model.getComponentName() != null) {
            return ProgressEvent.failed(null, null, HandlerErrorCode.InvalidRequest, "ComponentName is a read-only property.");
        }

        if (model.getComponentVersion() != null) {
            return ProgressEvent.failed(null, null, HandlerErrorCode.InvalidRequest, "ComponentVersion is a read-only property.");
        }

        return progressEvent;
    }

    private ProgressEvent<ResourceModel, CallbackContext> validateOnlyOneRecipeSourcePresent(ProgressEvent<ResourceModel, CallbackContext> progressEvent) {
        final ResourceModel model = progressEvent.getResourceModel();

        if (model.getInlineRecipe() != null && model.getLambdaFunction() != null) {
            return ProgressEvent.failed(null, null, HandlerErrorCode.InvalidRequest, "LambdaFunction and InlineRecipe should not be both provided.");
        }

        return progressEvent;
    }

    /**
     * By default, this will be called once every 5 seconds up to 20 minutes.
     * See {@link software.amazon.cloudformation.proxy.DelayFactory#CONSTANT_DEFAULT_DELAY_FACTORY}.
     *
     * Delay can be configured by calling {@link CallChain.Caller#backoffDelay(Delay)} in
     * {@link #handleRequest(AmazonWebServicesClientProxy, ResourceHandlerRequest, CallbackContext, ProxyClient, Logger)}
     * above.
     */
    private Boolean isStabilized(String arn, ProxyClient<GreengrassV2Client> proxyClient) {
        final DescribeComponentResponse describeComponentResponse;
        try {
            describeComponentResponse = proxyClient.injectCredentialsAndInvokeV2(
                    DescribeComponentRequest.builder()
                            .arn(arn)
                            .build(),
                    proxyClient.client()::describeComponent);
        } catch (Exception ex) {
            throw ExceptionTranslator.translateToCfnExceptionForCreatedResource(
                    "DescribeComponent", arn, ex);
        }

        final CloudComponentState componentState = describeComponentResponse.status().componentState();
        logger.log(String.format("Current component state: %s", componentState));
        switch (componentState) {
            case DEPLOYABLE:
                logger.log("Stabilized.");
                return true;
            case INITIATED:
            case REQUESTED:
                logger.log("Not yet stabilized.");
                return false;
            default:
                logger.log("Unexpected state; stabilization failed.");
                throw new CfnNotStabilizedException(ResourceModel.TYPE_NAME, arn);
        }
    }
}

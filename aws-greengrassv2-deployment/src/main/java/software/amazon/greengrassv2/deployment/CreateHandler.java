package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;


public class CreateHandler extends BaseHandlerStd {
    private Logger logger;

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<IotClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        logger.log("CreateHandler, the model is:" + request.getDesiredResourceState().toString());

//        return ProgressEvent.<ResourceModel, CallbackContext>builder()
//                .status(OperationStatus.SUCCESS)
//                .resourceModel(ResourceModel.builder().targetName(request.getDesiredResourceState().getTargetName()).build())
//                .build();

        // TODO: Adjust Progress Chain according to your implementation
        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        //We need to check whether the resouce exists for EG APIs
        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)

                // STEP 1 [check if resource already exists]
                // if target API does not support 'ResourceAlreadyExistsException' then following check is required
                // for more information -> https://docs.aws.amazon.com/cloudformation-cli/latest/userguide/resource-type-test-contract.html
//            .then(progress ->
//                // STEP 1.0 [initialize a proxy context]
//                // If your service API is not idempotent, meaning it does not distinguish duplicate create requests against some identifier (e.g; resource Name)
//                // and instead returns a 200 even though a resource already exists, you must first check if the resource exists here
//                // NOTE: If your service API throws 'ResourceAlreadyExistsException' for create requests this method is not necessary
//                proxy.initiate("AWS-GreengrassV2-Deployment::Create::PreExistanceCheck", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
//
//                    // STEP 1.1 [TODO: construct a body of a request]
//                    .translateToServiceRequest(Translator::translateToReadRequest)
//
//                    // STEP 1.2 [TODO: make an api call]
//                    .makeServiceCall((awsRequest, client) -> {
//                        AwsResponse awsResponse = null;
//
//                        // TODO: add custom read resource logic
//
//                        logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
//                        return awsResponse;
//                    })
//
//                    // STEP 1.3 [TODO: handle exception]
//                    .handleError((awsRequest, exception, client, model, context) -> {
//                        // TODO: uncomment when ready to implement
//                        // if (exception instanceof CfnNotFoundException)
//                        //     return ProgressEvent.progress(model, context);
//                        // throw exception;
//                        return ProgressEvent.progress(model, context);
//                    })
//                    .progress()
//            )

//                 STEP 2 [create/stabilize progress chain - required for resource creation]
                .then(progress ->
                        // If your service API throws 'ResourceAlreadyExistsException' for create requests then CreateHandler can return just proxy.initiate construction
                        // STEP 2.0 [initialize a proxy context]
                        // Implement client invocation of the create request through the proxyClient, which is already initialised with
                        // caller credentials, correct region and retry settings
                        proxy.initiate("AWS-GreengrassV2-Deployment::Create", proxyClient, progress.getResourceModel(), progress.getCallbackContext())

                                // STEP 2.1 [TODO: construct a body of a request]
                                .translateToServiceRequest(Translator::translateToCreateRequest)

                                // STEP 2.2 [TODO: make an api call]
                                .makeServiceCall((awsRequest, client) -> {
                                    CreateThingResponse awsResponse = null;
                                    try {
                                        awsResponse = client.injectCredentialsAndInvokeV2(awsRequest, client.client()::createThing);
                                    } catch (final AwsServiceException e) {
                                        ExceptionTranslator.translateToCfnException(e);
                                    }
                                    logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));
                                    return awsResponse;
                                }).progress()
                )

                // STEP 3 [TODO: describe call/chain to return the resource model]
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }
}


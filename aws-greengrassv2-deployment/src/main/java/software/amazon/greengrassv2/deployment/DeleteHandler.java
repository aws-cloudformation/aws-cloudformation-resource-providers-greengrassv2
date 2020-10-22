package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.DeleteThingResponse;
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
            final ProxyClient<IotClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        logger.log("DeleteHandler, the model is:" + request.getDesiredResourceState().toString());

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)

                // STEP 2.0 [delete/stabilize progress chain - required for resource deletion]
                .then(progress ->
                        // If your service API throws 'ResourceNotFoundException' for delete requests then DeleteHandler can return just proxy.initiate construction
                        // STEP 2.0 [initialize a proxy context]
                        // Implement client invocation of the delete request through the proxyClient, which is already initialised with
                        // caller credentials, correct region and retry settings
                        proxy.initiate("AWS-GreengrassV2-Deployment::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())

                                // STEP 2.1 [TODO: construct a body of a request]
                                .translateToServiceRequest(Translator::translateToDeleteRequest)

                                // STEP 2.2 [TODO: make an api call]
                                .makeServiceCall((awsRequest, client) -> {
                                    DeleteThingResponse deleteThingResponse = null;
                                    try {
                                        deleteThingResponse = client.injectCredentialsAndInvokeV2(awsRequest, client.client()::deleteThing);
                                    } catch (final AwsServiceException e) {
                                        ExceptionTranslator.translateToCfnException(e);
                                    }
                                    logger.log(String.format("%s successfully deleted.", ResourceModel.TYPE_NAME));
                                    return deleteThingResponse;
                                }).progress()
                )
                .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }
}

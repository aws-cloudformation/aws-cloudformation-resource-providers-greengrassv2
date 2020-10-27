package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableResponse;
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
        final ProxyClient<DynamoDbClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
            .then(progress ->
                proxy.initiate("AWS-GreengrassV2-ComponentVersion::Delete", proxyClient, progress.getResourceModel(), progress.getCallbackContext())
                        .translateToServiceRequest(Translator::translateToDeleteRequest)

                        .makeServiceCall((awsRequest, client) -> {
                            DeleteTableResponse awsResponse = null;
                            try {
                                awsResponse = client.injectCredentialsAndInvokeV2(awsRequest, client.client()::deleteTable);
                            } catch (final AwsServiceException e) {
                                ExceptionTranslator.translateToCfnException(e);
                            }
                            logger.log(String.format("%s has successfully been deleted.", ResourceModel.TYPE_NAME));
                            return awsResponse;
                        })
                    .progress()
            )
            .then(progress -> ProgressEvent.defaultSuccessHandler(null));
    }
}

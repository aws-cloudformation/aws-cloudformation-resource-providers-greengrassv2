package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
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
            final ProxyClient<DynamoDbClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        logger.log("CreateHandler, the model is:" + request.getDesiredResourceState().toString());

        return ProgressEvent.progress(request.getDesiredResourceState(), callbackContext)
                .then(progress ->
                        proxy.initiate("AWS-GreengrassV2-ComponentVersion::Create", proxyClient, progress.getResourceModel(), progress.getCallbackContext())

                                // STEP 2.1 [TODO: construct a body of a request]
                                .translateToServiceRequest(Translator::translateToCreateRequest)

                                // STEP 2.2 [TODO: make an api call]
                                .makeServiceCall((awsRequest, client) -> {
                                    CreateTableResponse awsResponse = null;
                                    try {
                                        awsResponse = client.injectCredentialsAndInvokeV2(awsRequest, client.client()::createTable);
                                    } catch (final AwsServiceException e) {
                                        ExceptionTranslator.translateToCfnException(e);
                                    }
                                    logger.log(String.format("%s successfully created.", ResourceModel.TYPE_NAME));
                                    return awsResponse;
                                })
                                .stabilize((describeRequest, response, proxyInvocation, model, context) -> isStabilized(proxyClient, model))
                                .progress()
                )
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
    }

    private Boolean isStabilized(ProxyClient<DynamoDbClient> proxyClient, ResourceModel model) {
        DescribeTableResponse describeTableResponse = proxyClient.injectCredentialsAndInvokeV2(Translator.translateToReadRequest(model), proxyClient.client()::describeTable);
        final TableStatus status = describeTableResponse.table().tableStatus();
        switch (status) {
            case ACTIVE:
                return true;
            case CREATING:
                return false;
            default:
                throw new CfnGeneralServiceException(String.format("Couldn't create %s due to invalid status: %s",
                        ResourceModel.TYPE_NAME, describeTableResponse.table().tableStatus()));
        }
    }
}

package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
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
        final ProxyClient<DynamoDbClient> proxyClient,
        final Logger logger) {

        this.logger = logger;

        return proxy.initiate("AWS-GreengrassV2-ComponentVersion::Read", proxyClient, request.getDesiredResourceState(), callbackContext)
            .translateToServiceRequest(Translator::translateToReadRequest)
                .makeServiceCall((awsRequest, client) -> {
                    DescribeTableResponse awsResponse = null;
                    try {
                        awsResponse = client.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeTable);
                    } catch (final AwsServiceException e) {
                        ExceptionTranslator.translateToCfnException(e);
                    }
                    logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
                    return awsResponse;
                })

            .done(awsResponse -> ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(awsResponse)));
    }
}

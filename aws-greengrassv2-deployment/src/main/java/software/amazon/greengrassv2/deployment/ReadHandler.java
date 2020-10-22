package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.DescribeThingResponse;
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
            final ProxyClient<IotClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        // TODO: Adjust Progress Chain according to your implementation
        // https://github.com/aws-cloudformation/cloudformation-cli-java-plugin/blob/master/src/main/java/software/amazon/cloudformation/proxy/CallChain.java

        // STEP 1 [initialize a proxy context]
        return proxy.initiate("AWS-GreengrassV2-Deployment::Read", proxyClient, request.getDesiredResourceState(), callbackContext)

                // STEP 2 [TODO: construct a body of a request]
                .translateToServiceRequest(Translator::translateToReadRequest)

                // STEP 3 [TODO: make an api call]
                // Implement client invocation of the read request through the proxyClient, which is already initialised with
                // caller credentials, correct region and retry settings
                .makeServiceCall((awsRequest, client) -> {
                    DescribeThingResponse describeThingResponse = null;
                    try {
                        describeThingResponse = client.injectCredentialsAndInvokeV2(awsRequest, client.client()::describeThing);
                    } catch (final AwsServiceException e) {
                        ExceptionTranslator.translateToCfnException(e);
                    }
                    logger.log(String.format("%s has successfully been read.", ResourceModel.TYPE_NAME));
                    return describeThingResponse;
                })

                // STEP 4 [TODO: gather all properties of the resource]
                // Implement client invocation of the read request through the proxyClient, which is already initialised with
                // caller credentials, correct region and retry settings
                .done(awsResponse -> ProgressEvent.defaultSuccessHandler(Translator.translateFromReadResponse(awsResponse)));
    }
}

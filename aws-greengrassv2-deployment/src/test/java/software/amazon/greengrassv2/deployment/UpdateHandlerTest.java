/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.GetDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetDeploymentResponse;
import software.amazon.awssdk.services.greengrassv2.model.GreengrassV2Exception;
import software.amazon.awssdk.services.greengrassv2.model.InternalServerException;
import software.amazon.awssdk.services.greengrassv2.model.ResourceNotFoundException;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceResponse;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceResponse;
import software.amazon.awssdk.services.greengrassv2.model.ValidationException;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_DEPLOYMENT_ID;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_DEPLOYMENT_NAME;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_MODEL;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_MODEL_DIFF_COMPONENTS;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_MODEL_DIFF_DEPLOYMENT_NAME;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_MODEL_DIFF_DEPLOYMENT_POLICIES;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_MODEL_DIFF_JOB_CONFIGURATION;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_MODEL_DIFF_TARGET_ARN;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_TAGS_1;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_TAGS_2;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_THING_GROUP_ARN;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_UPDATE_REQUEST;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<GreengrassV2Client> proxyClient;

    @Mock
    GreengrassV2Client greengrassV2Client;

    UpdateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        greengrassV2Client = mock(GreengrassV2Client.class);
        proxyClient = MOCK_PROXY(proxy, greengrassV2Client);
        handler = new UpdateHandler();
    }

    @AfterEach
    public void tear_down() {
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ResourceModel model = ResourceModel.builder()
                .deploymentId(TEST_DEPLOYMENT_ID)
                .deploymentName(TEST_DEPLOYMENT_NAME)
                .targetArn(TEST_THING_GROUP_ARN)
                .build();

        TagResourceResponse tagResourceResponse = TagResourceResponse.builder().build();
        UntagResourceResponse untagResourceResponse = UntagResourceResponse.builder().build();

        GetDeploymentResponse getDeploymentResponseBeforeUpdate = GetDeploymentResponse.builder()
                .deploymentId(TEST_DEPLOYMENT_ID)
                .deploymentName(TEST_DEPLOYMENT_NAME)
                .targetArn(TEST_THING_GROUP_ARN)
                .tags(TEST_TAGS_1)
                .build();
        GetDeploymentResponse getDeploymentResponseAfterUpdate = GetDeploymentResponse.builder()
                .deploymentId(TEST_DEPLOYMENT_ID)
                .deploymentName(TEST_DEPLOYMENT_NAME)
                .targetArn(TEST_THING_GROUP_ARN)
                .tags(TEST_TAGS_2)
                .build();

        doReturn(getDeploymentResponseBeforeUpdate, getDeploymentResponseAfterUpdate)
                .when(greengrassV2Client)
                .getDeployment(any(GetDeploymentRequest.class));
        doReturn(tagResourceResponse)
                .when(greengrassV2Client)
                .tagResource(any(TagResourceRequest.class));
        doReturn(untagResourceResponse)
                .when(greengrassV2Client)
                .untagResource(any(UntagResourceRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, TEST_UPDATE_REQUEST, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDeploymentId()).isEqualTo(TEST_DEPLOYMENT_ID);
        assertThat(response.getResourceModel().getDeploymentName()).isEqualTo(TEST_DEPLOYMENT_NAME);
        assertThat(response.getResourceModel().getTargetArn()).isEqualTo(TEST_THING_GROUP_ARN);
        assertThat(response.getResourceModel().getTags()).isEqualTo(TEST_TAGS_2);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getDeployment(any(GetDeploymentRequest.class));
        order.verify(greengrassV2Client, times(1)).tagResource(any(TagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).untagResource(any(UntagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).getDeployment(any(GetDeploymentRequest.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void handle_request_WhenResourceDoesNotExist_ThenThrowNotFoundException() {
        doThrow(ResourceNotFoundException.class)
                .when(greengrassV2Client)
                .getDeployment(any(GetDeploymentRequest.class));

        assertThrows(CfnNotFoundException.class,
                () -> handler.handleRequest(proxy, TEST_UPDATE_REQUEST, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", CfnNotFoundException.class.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getDeployment(any(GetDeploymentRequest.class));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("provideTaggingOperationExceptionsAndExpectedCfnExceptions")
    public void handle_request_WhenServiceClientThrowsExceptionDuringTagging_ThenTranslateToCfnException(
            Class<GreengrassV2Exception> greengrassV2Exception,
            Class<BaseHandlerException> cfnException) {
        doThrow(greengrassV2Exception)
                .when(greengrassV2Client)
                .tagResource(any(TagResourceRequest.class));

        assertThrows(cfnException,
                () -> handler.handleRequest(proxy, TEST_UPDATE_REQUEST, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", cfnException.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).tagResource(any(TagResourceRequest.class));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("provideTaggingOperationExceptionsAndExpectedCfnExceptions")
    public void handle_request_WhenServiceClientThrowsExceptionDuringUntagging_ThenTranslateToCfnException(
            Class<GreengrassV2Exception> greengrassV2Exception,
            Class<BaseHandlerException> cfnException) {

        doThrow(greengrassV2Exception)
                .when(greengrassV2Client)
                .untagResource(any(UntagResourceRequest.class));

        assertThrows(cfnException,
                () -> handler.handleRequest(proxy, TEST_UPDATE_REQUEST, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", cfnException.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).untagResource(any(UntagResourceRequest.class));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("provideModelsAndExpectedErrMsg")
    public void handleRequest_WhenGivenNotUpdatableAttribute_ThenThrowNotUpdatableException(final ResourceModel newModel,
                                                                                            final String expectedErrMsg) {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .awsPartition("aws")
                .region("us-east-1")
                .previousResourceState(TEST_MODEL)
                .previousResourceTags(TEST_TAGS_1)
                .desiredResourceState(newModel)
                .desiredResourceTags(TEST_TAGS_2)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).contains(expectedErrMsg);
        verify(greengrassV2Client, times(1)).getDeployment(any(GetDeploymentRequest.class));
        verifyNoMoreInteractions(greengrassV2Client);
    }

    private static Stream<Arguments> provideTaggingOperationExceptionsAndExpectedCfnExceptions() {
        //TagResource: https://docs.aws.amazon.com/greengrass/v2/APIReference/API_TagResource.html
        //UntagResource: https://docs.aws.amazon.com/greengrass/v2/APIReference/API_UntagResource.html
        return Stream.of(
                Arguments.of(ValidationException.class, CfnInvalidRequestException.class),
                Arguments.of(InternalServerException.class, CfnServiceInternalErrorException.class),
                Arguments.of(ResourceNotFoundException.class, CfnNotFoundException.class)
        );
    }

    private static Stream<Arguments> provideModelsAndExpectedErrMsg() {
        return Stream.of(
                Arguments.of(TEST_MODEL_DIFF_DEPLOYMENT_NAME, "DeploymentName property cannot be updated."),
                Arguments.of(TEST_MODEL_DIFF_COMPONENTS, "Component property cannot be updated."),
                Arguments.of(TEST_MODEL_DIFF_TARGET_ARN, "TargetArn property cannot be updated."),
                Arguments.of(TEST_MODEL_DIFF_DEPLOYMENT_POLICIES, "DeploymentPolicies property cannot be updated."),
                Arguments.of(TEST_MODEL_DIFF_JOB_CONFIGURATION, "IotJobConfiguration property cannot be updated.")
        );
    }
}

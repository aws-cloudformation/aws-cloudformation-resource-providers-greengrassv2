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
import software.amazon.awssdk.services.greengrassv2.model.AccessDeniedException;
import software.amazon.awssdk.services.greengrassv2.model.ConflictException;
import software.amazon.awssdk.services.greengrassv2.model.CreateDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.CreateDeploymentResponse;
import software.amazon.awssdk.services.greengrassv2.model.GetDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetDeploymentResponse;
import software.amazon.awssdk.services.greengrassv2.model.GreengrassV2Exception;
import software.amazon.awssdk.services.greengrassv2.model.InternalServerException;
import software.amazon.awssdk.services.greengrassv2.model.RequestAlreadyInProgressException;
import software.amazon.awssdk.services.greengrassv2.model.ResourceNotFoundException;
import software.amazon.awssdk.services.greengrassv2.model.ThrottlingException;
import software.amazon.awssdk.services.greengrassv2.model.ValidationException;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;
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
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_DEPLOYMENT_ID;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_DEPLOYMENT_NAME;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_THING_GROUP_ARN;
import static software.amazon.greengrassv2.deployment.TestUtils.TEST_THING_GROUP_ARN_1;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<GreengrassV2Client> proxyClient;

    @Mock
    GreengrassV2Client greengrassV2Client;

    CreateHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        greengrassV2Client = mock(GreengrassV2Client.class);
        proxyClient = MOCK_PROXY(proxy, greengrassV2Client);
        handler = new CreateHandler();
    }

    @AfterEach
    public void tear_down() {
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ResourceModel model = ResourceModel.builder()
                .deploymentName(TEST_DEPLOYMENT_NAME)
                .targetArn(TEST_THING_GROUP_ARN)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        CreateDeploymentResponse createDeploymentResponse = CreateDeploymentResponse.builder()
                .deploymentId(TEST_DEPLOYMENT_ID)
                .build();

        GetDeploymentResponse getDeploymentResponse = GetDeploymentResponse.builder()
                .deploymentId(TEST_DEPLOYMENT_ID)
                .deploymentName(TEST_DEPLOYMENT_NAME)
                .targetArn(TEST_THING_GROUP_ARN)
                .build();

        doReturn(createDeploymentResponse)
                .when(greengrassV2Client)
                .createDeployment(any(CreateDeploymentRequest.class));
        doReturn(getDeploymentResponse)
                .when(greengrassV2Client)
                .getDeployment(any(GetDeploymentRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDeploymentId()).isEqualTo(TEST_DEPLOYMENT_ID);
        assertThat(response.getResourceModel().getDeploymentName()).isEqualTo(TEST_DEPLOYMENT_NAME);
        assertThat(response.getResourceModel().getTargetArn()).isEqualTo(TEST_THING_GROUP_ARN);
        assertThat(response.getResourceModel().getParentTargetArn()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).createDeployment(any(CreateDeploymentRequest.class));
        order.verify(greengrassV2Client, times(1)).getDeployment(any(GetDeploymentRequest.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void handleRequest_with_subdeployment_SimpleSuccess() {
        final ResourceModel model = ResourceModel.builder()
                .deploymentName(TEST_DEPLOYMENT_NAME)
                .targetArn(TEST_THING_GROUP_ARN_1)
                .parentTargetArn(TEST_THING_GROUP_ARN)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        CreateDeploymentResponse createDeploymentResponse = CreateDeploymentResponse.builder()
                .deploymentId(TEST_DEPLOYMENT_ID)
                .build();

        GetDeploymentResponse getDeploymentResponse = GetDeploymentResponse.builder()
                .deploymentId(TEST_DEPLOYMENT_ID)
                .deploymentName(TEST_DEPLOYMENT_NAME)
                .targetArn(TEST_THING_GROUP_ARN_1)
                .parentTargetArn(TEST_THING_GROUP_ARN)
                .build();

        doReturn(createDeploymentResponse)
                .when(greengrassV2Client)
                .createDeployment(any(CreateDeploymentRequest.class));
        doReturn(getDeploymentResponse)
                .when(greengrassV2Client)
                .getDeployment(any(GetDeploymentRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDeploymentId()).isEqualTo(TEST_DEPLOYMENT_ID);
        assertThat(response.getResourceModel().getDeploymentName()).isEqualTo(TEST_DEPLOYMENT_NAME);
        assertThat(response.getResourceModel().getTargetArn()).isEqualTo(TEST_THING_GROUP_ARN_1);
        assertThat(response.getResourceModel().getParentTargetArn()).isEqualTo(TEST_THING_GROUP_ARN);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).createDeployment(any(CreateDeploymentRequest.class));
        order.verify(greengrassV2Client, times(1)).getDeployment(any(GetDeploymentRequest.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void handleRequest_RequestWithReadOnlyProperties_ThrowsException() {
        final ResourceModel model = ResourceModel.builder()
                .deploymentId(TEST_DEPLOYMENT_ID)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler
                .handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).contains("DeploymentId should not be provided in create request");
    }

    @ParameterizedTest
    @MethodSource("provideCreateDeploymentExceptionsAndExpectedCfnExceptions")
    public void handle_request_WhenServiceClientThrowsException_ThenTranslateToCfnException(
            Class<GreengrassV2Exception> greengrassV2Exception,
            Class<BaseHandlerException> cfnException) {
        final ResourceHandlerRequest<ResourceModel> request =
                ResourceHandlerRequest.<ResourceModel>builder().desiredResourceState(ResourceModel.builder().build()).build();

        doThrow(greengrassV2Exception)
                .when(greengrassV2Client)
                .createDeployment(any(CreateDeploymentRequest.class));

        assertThrows(cfnException,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", cfnException.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).createDeployment(any(CreateDeploymentRequest.class));
        order.verifyNoMoreInteractions();
    }

    private static Stream<Arguments> provideCreateDeploymentExceptionsAndExpectedCfnExceptions() {
        //https://docs.aws.amazon.com/greengrass/v2/APIReference/API_CreateDeployment.html#API_CreateDeployment_Errors
        return Stream.of(
                Arguments.of(ValidationException.class, CfnInvalidRequestException.class),
                Arguments.of(AccessDeniedException.class, CfnAccessDeniedException.class),
                Arguments.of(InternalServerException.class, CfnServiceInternalErrorException.class),
                Arguments.of(ThrottlingException.class, CfnServiceLimitExceededException.class),
                Arguments.of(ResourceNotFoundException.class, CfnNotFoundException.class),
                Arguments.of(RequestAlreadyInProgressException.class, CfnResourceConflictException.class),
                Arguments.of(ConflictException.class, CfnResourceConflictException.class)
        );
    }
}

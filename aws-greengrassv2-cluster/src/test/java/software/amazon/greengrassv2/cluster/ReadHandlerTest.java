/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */


package software.amazon.greengrassv2.cluster;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.GreengrassV2Exception;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterResponse;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static software.amazon.greengrassv2.cluster.TestUtils.CONFIGURATION;
import static software.amazon.greengrassv2.cluster.TestUtils.SDK_CONFIGURATION;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_ARN;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_MEMBERS;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_MEMBER_NAMES;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_NAME;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<GreengrassV2Client> proxyClient;

    @Mock
    GreengrassV2Client greengrassV2Client;

    ReadHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        greengrassV2Client = mock(GreengrassV2Client.class);
        proxyClient = MOCK_PROXY(proxy, greengrassV2Client);
        handler = new ReadHandler();
    }

    @AfterEach
    public void tear_down() {
    }

    @Test
    public void handleRequest_no_associations_SimpleSuccess() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(CONFIGURATION)
                        .build())
                .build();

        GetClusterResponse getClusterResponse = GetClusterResponse.builder()
                .clusterArn(TEST_CLUSTER_ARN)
                .clusterConfiguration(SDK_CONFIGURATION)
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder().build();

        doReturn(getClusterResponse)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        final ResourceModel responseModel = response.getResourceModel();
        assertThat(responseModel.getClusterName()).isEqualTo(TEST_CLUSTER_NAME);
        assertThat(responseModel.getClusterArn()).isEqualTo(TEST_CLUSTER_ARN);
        assertThat(responseModel.getClusterConfiguration()).isEqualTo(CONFIGURATION);
        assertThat(responseModel.getAssociatedMembers()).isEmpty();

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
    }

    @Test
    public void handleRequest_with_associations_SimpleSuccess() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(CONFIGURATION)
                        .associatedMembers(TEST_CLUSTER_MEMBER_NAMES)
                        .build())
                .build();

        GetClusterResponse getClusterResponse = GetClusterResponse.builder()
                .clusterArn(TEST_CLUSTER_ARN)
                .clusterConfiguration(SDK_CONFIGURATION)
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder()
                        .members(TEST_CLUSTER_MEMBERS)
                        .build();

        doReturn(getClusterResponse)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        final ResourceModel responseModel = response.getResourceModel();
        assertThat(responseModel.getClusterName()).isEqualTo(TEST_CLUSTER_NAME);
        assertThat(responseModel.getClusterArn()).isEqualTo(TEST_CLUSTER_ARN);
        assertThat(responseModel.getClusterConfiguration()).isEqualTo(CONFIGURATION);
        assertThat(responseModel.getAssociatedMembers()).isEqualTo(TEST_CLUSTER_MEMBER_NAMES);

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
    }

    @Test
    public void handleRequest_RequestWithoutClusterName_ThrowsException() {
        final ResourceModel model = ResourceModel.builder()
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler
                .handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).contains("Provided clusterName was null");
    }

    @ParameterizedTest
    @MethodSource("provideGeneralExceptionsAndExpectedCfnExceptions")
    public void handle_request_WhenServiceClientThrowsExceptionDuringGet_ThenTranslateToCfnException(
            Class<GreengrassV2Exception> greengrassV2Exception,
            Class<BaseHandlerException> cfnException) {
        final ResourceHandlerRequest<ResourceModel> request =
                ResourceHandlerRequest.<ResourceModel>builder()
                        .desiredResourceState(ResourceModel.builder()
                                .clusterName(TEST_CLUSTER_NAME)
                                .build())
                        .build();

        doThrow(greengrassV2Exception)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));

        assertThrows(cfnException,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", cfnException.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, never()).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("provideGeneralExceptionsAndExpectedCfnExceptions")
    public void handle_request_WhenServiceClientThrowsExceptionDuringListAssociated_ThenTranslateToCfnException(
            Class<GreengrassV2Exception> greengrassV2Exception,
            Class<BaseHandlerException> cfnException) {
        final ResourceHandlerRequest<ResourceModel> request =
                ResourceHandlerRequest.<ResourceModel>builder()
                        .desiredResourceState(ResourceModel.builder()
                                .clusterName(TEST_CLUSTER_NAME)
                                .build())
                        .build();

        GetClusterResponse getClusterResponse = GetClusterResponse.builder()
                .clusterArn(TEST_CLUSTER_ARN)
                .clusterConfiguration(SDK_CONFIGURATION)
                .build();

        doReturn(getClusterResponse)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doThrow(greengrassV2Exception)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));

        assertThrows(cfnException,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", cfnException.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verifyNoMoreInteractions();
    }
}

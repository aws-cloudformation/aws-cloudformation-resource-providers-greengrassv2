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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.AccessDeniedException;
import software.amazon.awssdk.services.greengrassv2.model.ConflictException;
import software.amazon.awssdk.services.greengrassv2.model.DeleteClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.DisassociateCoreDevicesFromClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.GreengrassV2Exception;
import software.amazon.awssdk.services.greengrassv2.model.InternalServerException;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterResponse;
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
import static software.amazon.greengrassv2.cluster.TestUtils.DELETE_CLUSTER_RESPONSE;
import static software.amazon.greengrassv2.cluster.TestUtils.DISASSOCIATE_CORE_DEVICES_FROM_CLUSTER_RESPONSE;
import static software.amazon.greengrassv2.cluster.TestUtils.GET_CLUSTER_RESPONSE_BEFORE;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_MEMBERS;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_NAME;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest extends AbstractTestBase {


    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<GreengrassV2Client> proxyClient;

    @Mock
    GreengrassV2Client greengrassV2Client;

    DeleteHandler handler;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        greengrassV2Client = mock(GreengrassV2Client.class);
        proxyClient = MOCK_PROXY(proxy, greengrassV2Client);
        handler = new DeleteHandler();
    }

    @AfterEach
    public void tear_down() {
    }

    @Test
    public void handleRequest_no_associations_SimpleSuccess() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .build())
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder().build();

        doReturn(GET_CLUSTER_RESPONSE_BEFORE)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));
        doReturn(DELETE_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .deleteCluster(any(DeleteClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).deleteCluster(any(DeleteClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void handleRequest_with_associations_SimpleSuccess() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .build())
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder()
                        .members(TEST_CLUSTER_MEMBERS)
                        .build();

        doReturn(GET_CLUSTER_RESPONSE_BEFORE)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));
        doReturn(DISASSOCIATE_CORE_DEVICES_FROM_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .disassociateCoreDevicesFromCluster(any(DisassociateCoreDevicesFromClusterRequest.class));
        doReturn(DELETE_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .deleteCluster(any(DeleteClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).disassociateCoreDevicesFromCluster(
                any(DisassociateCoreDevicesFromClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).deleteCluster(any(DeleteClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("provideDeleteClusterExceptionsAndExpectedCfnExceptions")
    public void handle_request_WhenServiceClientThrowsExceptionDuringDeletion_ThenTranslateToCfnException(
            Class<GreengrassV2Exception> greengrassV2Exception,
            Class<BaseHandlerException> cfnException) {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .build())
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder()
                        .members(TEST_CLUSTER_MEMBERS)
                        .build();

        doReturn(GET_CLUSTER_RESPONSE_BEFORE)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));
        doReturn(DISASSOCIATE_CORE_DEVICES_FROM_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .disassociateCoreDevicesFromCluster(any(DisassociateCoreDevicesFromClusterRequest.class));
        doThrow(greengrassV2Exception)
                .when(greengrassV2Client)
                .deleteCluster(any(DeleteClusterRequest.class));

        assertThrows(cfnException,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", cfnException.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).disassociateCoreDevicesFromCluster(
                any(DisassociateCoreDevicesFromClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).deleteCluster(any(DeleteClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    private static Stream<Arguments> provideDeleteClusterExceptionsAndExpectedCfnExceptions() {
        return Stream.of(
                Arguments.of(ValidationException.class, CfnInvalidRequestException.class),
                Arguments.of(AccessDeniedException.class, CfnAccessDeniedException.class),
                Arguments.of(InternalServerException.class, CfnServiceInternalErrorException.class),
                Arguments.of(ThrottlingException.class, CfnServiceLimitExceededException.class),
                Arguments.of(ResourceNotFoundException.class, CfnNotFoundException.class),
                Arguments.of(ConflictException.class, CfnResourceConflictException.class)
        );
    }
}

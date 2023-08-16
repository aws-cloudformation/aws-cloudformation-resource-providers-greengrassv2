/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */


package software.amazon.greengrassv2.cluster;

import com.google.common.collect.ImmutableList;
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
import software.amazon.awssdk.services.greengrassv2.model.AssociateCoreDevicesWithClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.DisassociateCoreDevicesFromClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.GreengrassV2Exception;
import software.amazon.awssdk.services.greengrassv2.model.InternalServerException;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.ResourceNotFoundException;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.UpdateClusterRequest;
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
import static software.amazon.greengrassv2.cluster.TestUtils.ASSOCIATE_CORE_DEVICES_WITH_CLUSTER_RESPONSE;
import static software.amazon.greengrassv2.cluster.TestUtils.CONFIGURATION;
import static software.amazon.greengrassv2.cluster.TestUtils.DISASSOCIATE_CORE_DEVICES_FROM_CLUSTER_RESPONSE;
import static software.amazon.greengrassv2.cluster.TestUtils.GET_CLUSTER_RESPONSE_AFTER;
import static software.amazon.greengrassv2.cluster.TestUtils.GET_CLUSTER_RESPONSE_BEFORE;
import static software.amazon.greengrassv2.cluster.TestUtils.OTHER_CONFIGURATION;
import static software.amazon.greengrassv2.cluster.TestUtils.OTHER_TEST_CLUSTER_MEMBERS;
import static software.amazon.greengrassv2.cluster.TestUtils.OTHER_TEST_CLUSTER_MEMBER_NAMES;
import static software.amazon.greengrassv2.cluster.TestUtils.OTHER_TEST_CLUSTER_NAME;
import static software.amazon.greengrassv2.cluster.TestUtils.TAG_RESOURCE_RESPONSE;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_ARN;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_MEMBERS;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_MEMBER_NAMES;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_CLUSTER_NAME;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_REQUEST;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_TAGS_1;
import static software.amazon.greengrassv2.cluster.TestUtils.TEST_TAGS_2;
import static software.amazon.greengrassv2.cluster.TestUtils.UNTAG_RESOURCE_RESPONSE;
import static software.amazon.greengrassv2.cluster.TestUtils.UPDATE_CLUSTER_RESPONSE;

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
    public void handleRequest_keep_associations_SimpleSuccess() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(OTHER_CONFIGURATION)
                        .associatedMembers(TEST_CLUSTER_MEMBER_NAMES)
                        .build())
                .previousResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(CONFIGURATION)
                        .associatedMembers(TEST_CLUSTER_MEMBER_NAMES)
                        .build())
                .desiredResourceTags(TEST_TAGS_2)
                .previousResourceTags(TEST_TAGS_1)
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder()
                        .members(TEST_CLUSTER_MEMBERS)
                        .build();

        doReturn(GET_CLUSTER_RESPONSE_BEFORE, GET_CLUSTER_RESPONSE_AFTER)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(UNTAG_RESOURCE_RESPONSE)
                .when(greengrassV2Client)
                .untagResource(any(UntagResourceRequest.class));
        doReturn(TAG_RESOURCE_RESPONSE)
                .when(greengrassV2Client)
                .tagResource(any(TagResourceRequest.class));
        doReturn(UPDATE_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .updateCluster(any(UpdateClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        verifyResponse(response);
        assertThat(response.getResourceModel().getAssociatedMembers()).isEqualTo(TEST_CLUSTER_MEMBER_NAMES);

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).untagResource(any(UntagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).tagResource(any(TagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).updateCluster(any(UpdateClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void handleRequest_remove_all_associations_SimpleSuccess() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(OTHER_CONFIGURATION)
                        .associatedMembers(ImmutableList.of())
                        .build())
                .previousResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(CONFIGURATION)
                        .associatedMembers(TEST_CLUSTER_MEMBER_NAMES)
                        .build())
                .desiredResourceTags(TEST_TAGS_2)
                .previousResourceTags(TEST_TAGS_1)
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder()
                        .build();

        doReturn(GET_CLUSTER_RESPONSE_BEFORE, GET_CLUSTER_RESPONSE_AFTER)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(UNTAG_RESOURCE_RESPONSE)
                .when(greengrassV2Client)
                .untagResource(any(UntagResourceRequest.class));
        doReturn(TAG_RESOURCE_RESPONSE)
                .when(greengrassV2Client)
                .tagResource(any(TagResourceRequest.class));
        doReturn(DISASSOCIATE_CORE_DEVICES_FROM_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .disassociateCoreDevicesFromCluster(any(DisassociateCoreDevicesFromClusterRequest.class));
        doReturn(UPDATE_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .updateCluster(any(UpdateClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        verifyResponse(response);
        assertThat(response.getResourceModel().getAssociatedMembers()).isEqualTo(ImmutableList.of());

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).untagResource(any(UntagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).tagResource(any(TagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).disassociateCoreDevicesFromCluster(
                any(DisassociateCoreDevicesFromClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).updateCluster(any(UpdateClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void handleRequest_bring_new_associations_SimpleSuccess() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(OTHER_CONFIGURATION)
                        .associatedMembers(TEST_CLUSTER_MEMBER_NAMES)
                        .build())
                .previousResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(CONFIGURATION)
                        .associatedMembers(ImmutableList.of())
                        .build())
                .desiredResourceTags(TEST_TAGS_2)
                .previousResourceTags(TEST_TAGS_1)
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder()
                        .members(TEST_CLUSTER_MEMBERS)
                        .build();

        doReturn(GET_CLUSTER_RESPONSE_BEFORE, GET_CLUSTER_RESPONSE_AFTER)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(UNTAG_RESOURCE_RESPONSE)
                .when(greengrassV2Client)
                .untagResource(any(UntagResourceRequest.class));
        doReturn(TAG_RESOURCE_RESPONSE)
                .when(greengrassV2Client)
                .tagResource(any(TagResourceRequest.class));
        doReturn(ASSOCIATE_CORE_DEVICES_WITH_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .associateCoreDevicesWithCluster(any(AssociateCoreDevicesWithClusterRequest.class));
        doReturn(UPDATE_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .updateCluster(any(UpdateClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        verifyResponse(response);
        assertThat(response.getResourceModel().getAssociatedMembers()).isEqualTo(TEST_CLUSTER_MEMBER_NAMES);

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).untagResource(any(UntagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).tagResource(any(TagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).associateCoreDevicesWithCluster(
                any(AssociateCoreDevicesWithClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).updateCluster(any(UpdateClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void handleRequest_disassociate_and_associate_SimpleSuccess() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(OTHER_CONFIGURATION)
                        .associatedMembers(OTHER_TEST_CLUSTER_MEMBER_NAMES)
                        .build())
                .previousResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .clusterConfiguration(CONFIGURATION)
                        .associatedMembers(TEST_CLUSTER_MEMBER_NAMES)
                        .build())
                .desiredResourceTags(TEST_TAGS_2)
                .previousResourceTags(TEST_TAGS_1)
                .build();

        ListCoreDevicesAssociatedWithClusterResponse listCoreDevicesAssociatedWithClusterResponse =
                ListCoreDevicesAssociatedWithClusterResponse.builder()
                        .members(OTHER_TEST_CLUSTER_MEMBERS)
                        .build();

        doReturn(GET_CLUSTER_RESPONSE_BEFORE, GET_CLUSTER_RESPONSE_AFTER)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doReturn(UNTAG_RESOURCE_RESPONSE)
                .when(greengrassV2Client)
                .untagResource(any(UntagResourceRequest.class));
        doReturn(TAG_RESOURCE_RESPONSE)
                .when(greengrassV2Client)
                .tagResource(any(TagResourceRequest.class));
        doReturn(DISASSOCIATE_CORE_DEVICES_FROM_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .disassociateCoreDevicesFromCluster(any(DisassociateCoreDevicesFromClusterRequest.class));
        doReturn(ASSOCIATE_CORE_DEVICES_WITH_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .associateCoreDevicesWithCluster(any(AssociateCoreDevicesWithClusterRequest.class));
        doReturn(UPDATE_CLUSTER_RESPONSE)
                .when(greengrassV2Client)
                .updateCluster(any(UpdateClusterRequest.class));
        doReturn(listCoreDevicesAssociatedWithClusterResponse)
                .when(greengrassV2Client)
                .listCoreDevicesAssociatedWithCluster(any(ListCoreDevicesAssociatedWithClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        verifyResponse(response);
        assertThat(response.getResourceModel().getAssociatedMembers()).isEqualTo(OTHER_TEST_CLUSTER_MEMBER_NAMES);

        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).untagResource(any(UntagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).tagResource(any(TagResourceRequest.class));
        order.verify(greengrassV2Client, times(1)).disassociateCoreDevicesFromCluster(
                any(DisassociateCoreDevicesFromClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).associateCoreDevicesWithCluster(
                any(AssociateCoreDevicesWithClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).updateCluster(any(UpdateClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verify(greengrassV2Client, times(1)).listCoreDevicesAssociatedWithCluster(
                any(ListCoreDevicesAssociatedWithClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    private void verifyResponse(final ProgressEvent<ResourceModel, CallbackContext> response) {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        final ResourceModel responseModel = response.getResourceModel();
        assertThat(responseModel.getClusterName()).isEqualTo(TEST_CLUSTER_NAME);
        assertThat(responseModel.getClusterArn()).isEqualTo(TEST_CLUSTER_ARN);
        assertThat(responseModel.getClusterConfiguration()).isEqualTo(OTHER_CONFIGURATION);
        assertThat(response.getResourceModel().getTags()).isEqualTo(TEST_TAGS_2);
    }

    @Test
    public void handle_request_WhenResourceDoesNotExist_ThenThrowNotFoundException() {
        doThrow(ResourceNotFoundException.class)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));

        assertThrows(CfnNotFoundException.class,
                () -> handler.handleRequest(proxy, TEST_REQUEST, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", CfnNotFoundException.class.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("provideTaggingOperationExceptionsAndExpectedCfnExceptions")
    public void handle_request_WhenServiceClientThrowsExceptionDuringTagging_ThenTranslateToCfnException(
            Class<GreengrassV2Exception> greengrassV2Exception,
            Class<BaseHandlerException> cfnException) {

        doReturn(GET_CLUSTER_RESPONSE_BEFORE)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doThrow(greengrassV2Exception)
                .when(greengrassV2Client)
                .tagResource(any(TagResourceRequest.class));

        assertThrows(cfnException,
                () -> handler.handleRequest(proxy, TEST_REQUEST, new CallbackContext(), proxyClient, logger),
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

        doReturn(GET_CLUSTER_RESPONSE_BEFORE)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));
        doThrow(greengrassV2Exception)
                .when(greengrassV2Client)
                .untagResource(any(UntagResourceRequest.class));

        assertThrows(cfnException,
                () -> handler.handleRequest(proxy, TEST_REQUEST, new CallbackContext(), proxyClient, logger),
                String.format("Expected handler throws %s, but it did not", cfnException.getSimpleName()));
        InOrder order = inOrder(greengrassV2Client);
        order.verify(greengrassV2Client, times(1)).untagResource(any(UntagResourceRequest.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void handleRequest_WhenGivenNotUpdatableAttribute_ThenThrowNotUpdatableException() {
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(ResourceModel.builder()
                        .clusterName(OTHER_TEST_CLUSTER_NAME)
                        .build())
                .previousResourceState(ResourceModel.builder()
                        .clusterName(TEST_CLUSTER_NAME)
                        .build())
                .desiredResourceTags(TEST_TAGS_2)
                .previousResourceTags(TEST_TAGS_1)
                .build();

        doReturn(GET_CLUSTER_RESPONSE_BEFORE)
                .when(greengrassV2Client)
                .getCluster(any(GetClusterRequest.class));

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getMessage()).contains("ClusterName property cannot be updated.");
        verify(greengrassV2Client, times(1)).getCluster(any(GetClusterRequest.class));
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
}

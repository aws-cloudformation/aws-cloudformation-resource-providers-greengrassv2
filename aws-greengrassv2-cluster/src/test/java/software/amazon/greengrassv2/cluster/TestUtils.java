/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */


package software.amazon.greengrassv2.cluster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.greengrassv2.model.AssociateCoreDevicesWithClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.AssociatedCoreDevice;
import software.amazon.awssdk.services.greengrassv2.model.AssociatedCoreDeviceRole;
import software.amazon.awssdk.services.greengrassv2.model.AssociatedCoreDeviceStatus;
import software.amazon.awssdk.services.greengrassv2.model.DeleteClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.DisassociateCoreDevicesFromClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceResponse;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceResponse;
import software.amazon.awssdk.services.greengrassv2.model.UpdateClusterResponse;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestUtils {

    public static final String TEST_CLUSTER_NAME = "testCluster";
    public static final String OTHER_TEST_CLUSTER_NAME = "otherTestCluster";
    public static final String TEST_CLUSTER_ARN = String.format("arn:aws:greengrass:us-east-1:123456789012:clusters:%s-12345678910",
            TEST_CLUSTER_NAME);
    public static final Map<String, String> TEST_TAGS_1 = ImmutableMap.of("key1", "value1");
    public static final Map<String, String> TEST_TAGS_2 = ImmutableMap.of("key2", "value2");

    public static final List<String> TEST_CLUSTER_MEMBER_NAMES = ImmutableList.of(
            "myThing1", "myThing2", "myThing3", "myThing4");
    public static final List<String> OTHER_TEST_CLUSTER_MEMBER_NAMES = ImmutableList.of(
                        "myThing2", "myThing3",             "myThing5");

    public static final String TEST_NEXT_TOKEN = "nextToken";

    public static final List<AssociatedCoreDevice> TEST_CLUSTER_MEMBERS =
            convertThingNamesToAssociationList(TEST_CLUSTER_MEMBER_NAMES);
    public static final List<AssociatedCoreDevice> OTHER_TEST_CLUSTER_MEMBERS =
            convertThingNamesToAssociationList(OTHER_TEST_CLUSTER_MEMBER_NAMES);

    public static final ClusterConfiguration CONFIGURATION = ClusterConfiguration.builder()
            .maximumLeaderElectionTimeout(1000)
            .minimumLeaderElectionTimeout(500)
            .clusterClientPort(8080)
            .clusterServerPort(8080)
            .minimumHealthyNodes(4)
            .build();

    public static final ClusterConfiguration OTHER_CONFIGURATION = ClusterConfiguration.builder()
            .maximumLeaderElectionTimeout(500)
            .minimumLeaderElectionTimeout(250)
            .clusterClientPort(4040)
            .clusterServerPort(4040)
            .minimumHealthyNodes(2)
            .build();

    public static final software.amazon.awssdk.services.greengrassv2.model.ClusterConfiguration SDK_CONFIGURATION =
            ModelTranslator.translateToSdkClusterConfiguration(CONFIGURATION);
    public static final software.amazon.awssdk.services.greengrassv2.model.ClusterConfiguration OTHER_SDK_CONFIGURATION =
            ModelTranslator.translateToSdkClusterConfiguration(OTHER_CONFIGURATION);

    public static final ResourceHandlerRequest<ResourceModel> TEST_REQUEST = ResourceHandlerRequest.<ResourceModel>builder()
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

    public static final GetClusterResponse GET_CLUSTER_RESPONSE_BEFORE = GetClusterResponse.builder()
            .clusterArn(TEST_CLUSTER_ARN)
                .clusterConfiguration(SDK_CONFIGURATION)
                .tags(TEST_TAGS_1)
                .build();

    public static final GetClusterResponse GET_CLUSTER_RESPONSE_AFTER = GetClusterResponse.builder()
            .clusterArn(TEST_CLUSTER_ARN)
            .clusterConfiguration(OTHER_SDK_CONFIGURATION)
            .tags(TEST_TAGS_2)
            .build();

    public static final UpdateClusterResponse UPDATE_CLUSTER_RESPONSE = UpdateClusterResponse.builder()
            .clusterArn(TEST_CLUSTER_ARN)
            .build();

    public static final TagResourceResponse TAG_RESOURCE_RESPONSE = TagResourceResponse.builder().build();
    public static final UntagResourceResponse UNTAG_RESOURCE_RESPONSE = UntagResourceResponse.builder().build();

    public static final DisassociateCoreDevicesFromClusterResponse DISASSOCIATE_CORE_DEVICES_FROM_CLUSTER_RESPONSE =
            DisassociateCoreDevicesFromClusterResponse.builder().build();

    public static final AssociateCoreDevicesWithClusterResponse ASSOCIATE_CORE_DEVICES_WITH_CLUSTER_RESPONSE =
            AssociateCoreDevicesWithClusterResponse.builder().build();

    public static final DeleteClusterResponse DELETE_CLUSTER_RESPONSE = DeleteClusterResponse.builder().build();

    private static List<AssociatedCoreDevice> convertThingNamesToAssociationList(final List<String> memberNames) {
        return memberNames.stream()
                .map(member -> AssociatedCoreDevice.builder()
                        .coreDeviceThingName(member)
                        .clusterRole(AssociatedCoreDeviceRole.PENDING)
                        .nodeStatus(AssociatedCoreDeviceStatus.PENDING_ASSOCIATION)
                        .lastStatusUpdateTimestamp(Instant.now())
                        .build())
                .collect(Collectors.toList());
    }
}
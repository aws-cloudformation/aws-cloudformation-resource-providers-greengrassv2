/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import com.google.common.collect.ImmutableMap;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;

public class TestUtils {
    public static final String TEST_THING_GROUP_ARN = "arn:aws:iot:us-east-1:123456789012:thinggroup/testThingGroup";
    public static final String TEST_THING_GROUP_ARN_1 = "arn:aws:iot:us-east-1:123456789012:thinggroup/testThingGroup1";
    public static final String TEST_DEPLOYMENT_NAME = "testDeployment";
    public static final String TEST_DEPLOYMENT_NAME_1 = "testDeployment1";
    public static final String TEST_DEPLOYMENT_ID = "0123456789";
    public static final String TEST_NEXT_TOKEN = "nextToken";
    public static final Map<String, String> TEST_TAGS_1 = ImmutableMap.of("key1", "value1");
    public static final Map<String, String> TEST_TAGS_2 = ImmutableMap.of("key2", "value2");

    private static final String TEST_COMPONENT_NAME = "testComponentName";
    private static final String TEST_COMPONENT_VERSION = "1.0.0";
    private static final Integer TEST_IN_PROGRESS_TIMEOUT = 10;
    private static final String TEST_COMPONENT_UPDATE_POLICY = "NOTIFY_COMPONENTS";

    private static final Map<String, ComponentDeploymentSpecification> TEST_COMPONENT_MAP = ImmutableMap.of(
            TEST_COMPONENT_NAME, ComponentDeploymentSpecification.builder()
                    .componentVersion(TEST_COMPONENT_VERSION)
                    .build());

    private static final DeploymentIoTJobConfiguration TEST_JOB_CONFIGURATION = DeploymentIoTJobConfiguration.builder()
            .timeoutConfig(IoTJobTimeoutConfig.builder()
                    .inProgressTimeoutInMinutes(TEST_IN_PROGRESS_TIMEOUT)
                    .build())
            .build();

    private static final DeploymentPolicies TEST_DEPLOYMENT_POLICIES = DeploymentPolicies.builder()
            .componentUpdatePolicy(DeploymentComponentUpdatePolicy.builder()
                    .action(TEST_COMPONENT_UPDATE_POLICY)
                    .build())
            .build();

    public static final ResourceModel TEST_MODEL = ResourceModel.builder()
            .deploymentId(TEST_DEPLOYMENT_ID)
            .deploymentName(TEST_DEPLOYMENT_NAME)
            .targetArn(TEST_THING_GROUP_ARN)
            .build();

    // Here are models where each model is different from TEST_VALID_MODEL with one not-updatable attribute respectively.
    public static final ResourceModel TEST_MODEL_DIFF_TARGET_ARN = TEST_MODEL.toBuilder().targetArn(TEST_THING_GROUP_ARN_1).build();
    public static final ResourceModel TEST_MODEL_DIFF_DEPLOYMENT_NAME = TEST_MODEL.toBuilder().deploymentName(TEST_DEPLOYMENT_NAME_1).build();
    public static final ResourceModel TEST_MODEL_DIFF_COMPONENTS = TEST_MODEL.toBuilder().components(TEST_COMPONENT_MAP).build();
    public static final ResourceModel TEST_MODEL_DIFF_JOB_CONFIGURATION = TEST_MODEL.toBuilder().iotJobConfiguration(TEST_JOB_CONFIGURATION).build();
    public static final ResourceModel TEST_MODEL_DIFF_DEPLOYMENT_POLICIES = TEST_MODEL.toBuilder().deploymentPolicies(TEST_DEPLOYMENT_POLICIES).build();

    public static final ResourceHandlerRequest<ResourceModel> TEST_UPDATE_REQUEST = ResourceHandlerRequest.<ResourceModel>builder()
            .awsPartition("aws")
            .region("us-east-1")
            .previousResourceState(TEST_MODEL)
            .previousResourceTags(TEST_TAGS_1)
            .desiredResourceState(TEST_MODEL)
            .desiredResourceTags(TEST_TAGS_2)
            .build();
}

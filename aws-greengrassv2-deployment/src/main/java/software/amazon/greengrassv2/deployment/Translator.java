/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.services.greengrassv2.model.CancelDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.CreateDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.DeleteDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetDeploymentResponse;
import software.amazon.awssdk.services.greengrassv2.model.ListDeploymentsRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListDeploymentsResponse;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a centralized placeholder for
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - resource model construction for read/list handlers
 */

public class Translator {

  /**
   * Request to create a deployment
   * @param model resource model
   * @return CreateDeploymentRequest GreengrassV2 CreateDeploymentRequest
   */
  static CreateDeploymentRequest translateToCreateRequest(final ResourceModel model,
                                                          final Map<String, String> desiredResourceTags) {
    return CreateDeploymentRequest.builder()
            .targetArn(model.getTargetArn())
            .deploymentPolicies(ModelTranslator.mapNullable(model.getDeploymentPolicies(),
                    ModelTranslator::translateToSdkDeploymentPolicies))
            .iotJobConfiguration(ModelTranslator.mapNullable(model.getIotJobConfiguration(),
                    ModelTranslator::translateToSdkDeploymentIoTJobConfiguration))
            .components(ModelTranslator.mapNullable(model.getComponents(),
                    ModelTranslator::translateToSdkComponentDeploymentSpecs))
            .tags(desiredResourceTags != null && !desiredResourceTags.isEmpty() ?
                    desiredResourceTags : null)
            .deploymentName(model.getDeploymentName())
            .build();
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return GetDeploymentRequest GreengrassV2 GetDeploymentRequest
   */
  static GetDeploymentRequest translateToReadRequest(final ResourceModel model) {
    return GetDeploymentRequest.builder()
            .deploymentId(model.getDeploymentId())
            .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param response Greengrass V2 GetDeploymentResponse
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final GetDeploymentResponse response) {
    return ResourceModel.builder()
            .deploymentId(response.deploymentId())
            .deploymentName(response.deploymentName())
            .targetArn(response.targetArn())
            .components(ModelTranslator.mapNullable(response.components(), ModelTranslator::translateToCfnComponentDeploymentSpecs))
            .deploymentPolicies(ModelTranslator.mapNullable(response.deploymentPolicies(), ModelTranslator::translateToCfnDeploymentPolicies))
            .iotJobConfiguration(ModelTranslator.mapNullable(response.iotJobConfiguration(), ModelTranslator::translateToCfnDeploymentIoTJobConfiguration))
            .tags(response.tags())
            .build();
  }

  /**
   * Request to cancel a resource
   * @param model resource model
   * @return awsRequest the aws service request to cancel a deployment
   */
  static CancelDeploymentRequest translateToCancelRequest(final ResourceModel model) {
    return CancelDeploymentRequest.builder()
            .deploymentId(model.getDeploymentId())
            .build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return awsRequest the aws service request to delete a deployment
   */
  static DeleteDeploymentRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteDeploymentRequest.builder()
            .deploymentId(model.getDeploymentId())
            .build();
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListDeploymentsRequest translateToListRequest(final String nextToken) {
    return ListDeploymentsRequest.builder()
            .nextToken(nextToken)
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param listDeploymentsResponse the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListResponse(final ListDeploymentsResponse listDeploymentsResponse) {
    return streamOfOrEmpty(listDeploymentsResponse.deployments())
            .map(resource -> ResourceModel.builder()
                    .deploymentId(resource.deploymentId())
                    .build())
            .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
            .map(Collection::stream)
            .orElseGet(Stream::empty);
  }

  public static TagResourceRequest translateToTagResourceRequest(String deploymentArn, Map<String, String> tagsToAdd) {
    return TagResourceRequest.builder()
            .resourceArn(deploymentArn)
            .tags(tagsToAdd)
            .build();
  }

  public static UntagResourceRequest translateToUntagResourceRequest(String deploymentArn, Map<String, String> tagsToRemove) {
    return UntagResourceRequest.builder()
            .resourceArn(deploymentArn)
            .tagKeys(tagsToRemove.keySet())
            .build();
  }
}

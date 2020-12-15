/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.arns.Arn;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.greengrassv2.model.CreateComponentVersionRequest;
import software.amazon.awssdk.services.greengrassv2.model.DeleteComponentRequest;
import software.amazon.awssdk.services.greengrassv2.model.DescribeComponentRequest;
import software.amazon.awssdk.services.greengrassv2.model.DescribeComponentResponse;
import software.amazon.awssdk.services.greengrassv2.model.ListComponentVersionsRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListComponentVersionsResponse;
import software.amazon.awssdk.services.greengrassv2.model.RecipeSource;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceRequest;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Collection;
import java.util.Collections;
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
   * Request to create a resource
   * @param model resource model
   * @param desiredResourceTags
   * @return awsRequest the createTableRequest
   */
  static CreateComponentVersionRequest translateToCreateRequest(final ResourceModel model,
                                                                final Map<String, String> desiredResourceTags) {
    return CreateComponentVersionRequest.builder()
            .inlineRecipe(CreateTranslator.mapNullable(model.getInlineRecipe(),
                            SdkBytes::fromUtf8String))
            .lambdaFunction(CreateTranslator.mapNullable(model.getLambdaFunction(),
                            CreateTranslator::translateToSDKLambdaFunctionRecipeSource))
            .tags(desiredResourceTags != null && !desiredResourceTags.isEmpty() ?
                    desiredResourceTags : null)
            .build();
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return awsRequest the aws service request to describe a resource
   */
  static DescribeComponentRequest translateToReadRequest(final ResourceModel model) {
    return DescribeComponentRequest.builder()
            .arn(model.getArn())
            .build();
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param describeComponentResponse the describeComponentResponse
   * @return model resource model
   */
  static ResourceModel translateFromReadResponse(final DescribeComponentResponse describeComponentResponse) {
    final Map<String, String> tags = describeComponentResponse.tags();

    return ResourceModel.builder()
            .arn(describeComponentResponse.arn())
            .componentVersion(describeComponentResponse.componentVersion())
            .componentName(describeComponentResponse.componentName())
            .tags(tags != null ? tags : Collections.emptyMap())
            .build();
  }

  /**
   * Request to delete a resource
   * @param model resource model
   * @return deleteTableRequest deleteTableRequest
   */
  static DeleteComponentRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteComponentRequest.builder()
            .arn(model.getArn())
            .build();
  }

  /**
   * Request to list resources
   *
   * @param request
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListComponentVersionsRequest translateToListRequest(final ResourceHandlerRequest<ResourceModel> request,
                                                             final String nextToken) {
    return ListComponentVersionsRequest.builder()
            .arn(Arn.builder()
                    .partition(request.getAwsPartition())
                    .region(request.getRegion())
                    .accountId(request.getAwsAccountId())
                    .service("greengrass")
                    .resource("components:" + request.getDesiredResourceState().getComponentName())
                    .build()
                    .toString())
            .nextToken(nextToken)
            .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param listComponentVersionsResponse the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(final ListComponentVersionsResponse listComponentVersionsResponse) {
    return streamOfOrEmpty(listComponentVersionsResponse.componentVersions())
        .map(resource -> ResourceModel.builder()
                // Only populates primaryIdentifier (arn)
                .arn(resource.arn())
                .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }

  public static TagResourceRequest translateToTagResourceRequest(String arn, Map<String, String> tagsToAdd) {
    return TagResourceRequest.builder()
            .resourceArn(arn)
            .tags(tagsToAdd)
            .build();
  }

  public static UntagResourceRequest translateToUntagResourceRequest(String arn, Map<String, String> tagsToRemove) {
    return UntagResourceRequest.builder()
            .resourceArn(arn)
            .tagKeys(tagsToRemove.keySet())
            .build();
  }
}

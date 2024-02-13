/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.cluster;

import software.amazon.awssdk.services.greengrassv2.model.AssociateCoreDeviceWithClusterEntry;
import software.amazon.awssdk.services.greengrassv2.model.AssociateCoreDevicesWithClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.CreateClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.DeleteClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.DisassociateCoreDeviceFromClusterEntry;
import software.amazon.awssdk.services.greengrassv2.model.DisassociateCoreDevicesFromClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.GetClusterResponse;
import software.amazon.awssdk.services.greengrassv2.model.ListClustersRequest;
import software.amazon.awssdk.services.greengrassv2.model.ListClustersResponse;
import software.amazon.awssdk.services.greengrassv2.model.ListCoreDevicesAssociatedWithClusterRequest;
import software.amazon.awssdk.services.greengrassv2.model.TagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.UntagResourceRequest;
import software.amazon.awssdk.services.greengrassv2.model.UpdateClusterRequest;

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
     * Request to create a cluster
     * @param model resource model
     * @param desiredResourceTags tags
     * @return CreateClusterRequest GreengrassV2 CreateClusterRequest
     */
    static CreateClusterRequest translateToCreateRequest(final ResourceModel model,
                                                         final Map<String, String> desiredResourceTags) {
        return CreateClusterRequest.builder()
                .clusterName(model.getClusterName())
                .clusterConfiguration(ModelTranslator.translateToSdkClusterConfiguration(
                        model.getClusterConfiguration()))
                .tags(desiredResourceTags != null && !desiredResourceTags.isEmpty() ?
                        desiredResourceTags : null)
                .build();
    }

    /**
     * Request to update a cluster
     * @param model resource model
     * @return UpdateClusterRequest GreengrassV2 UpdateClusterRequest
     */
    static UpdateClusterRequest translateToUpdateRequest(final ResourceModel model) {
        return UpdateClusterRequest.builder()
                .clusterName(model.getClusterName())
                .clusterConfiguration(ModelTranslator.translateToSdkClusterConfiguration(
                        model.getClusterConfiguration()))
                .build();
    }

    /**
     * Request to read a resource
     * @param model resource model
     * @return GetClusterRequest GreengrassV2 GetClusterRequest
     */
    static GetClusterRequest translateToReadRequest(final ResourceModel model) {
        return GetClusterRequest.builder()
                .clusterName(model.getClusterName())
                .build();
    }

    /**
     * Translates resource object from sdk into resource model
     * @param response resource model
     * @return model resource model
     */
    static ResourceModel translateFromReadResponse(final GetClusterResponse response) {
        return ResourceModel.builder()
                .clusterArn(response.clusterArn())
                .clusterConfiguration(ModelTranslator.translateToCfnClusterConfiguration(
                        response.clusterConfiguration()))
                .tags(response.tags())
                .build();
    }

    /**
     * Request to delete a resource
     * @param model resource model
     * @return awsRequest the aws service request to delete a cluster
     */
    static DeleteClusterRequest translateToDeleteRequest(final ResourceModel model) {
        return DeleteClusterRequest.builder()
                .clusterName(model.getClusterName())
                .build();
    }

    /**
     * Request to list resources
     * @param nextToken token passed to the aws service list resources request
     * @return awsRequest the aws service request to list resources within aws account
     */
    static ListClustersRequest translateToListRequest(final String nextToken) {
        return ListClustersRequest.builder()
                .nextToken(nextToken)
                .build();
    }

    /**
     * Translates resource objects from sdk into a resource model (primary identifier only)
     * @param response the aws service describe resource response
     * @return list of resource models
     */
    static List<ResourceModel> translateFromListResponse(final ListClustersResponse response) {
        return streamOfOrEmpty(response.clusters())
                .map(resource -> ResourceModel.builder()
                        .clusterArn(resource.clusterArn())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Request to associate members with cluster
     * @param model resource model
     * @param associateMembers members to associate
     * @return awsRequest the aws service request to associate core devices with a cluster
     */
    static AssociateCoreDevicesWithClusterRequest translateToAssociateRequest(final ResourceModel model,
                                                                              final List<String> associateMembers) {
        if (model.getAssociatedMembers() == null) {
            // No associations will cause a request build failure, so return null and handle that
            return null;
        }

        return AssociateCoreDevicesWithClusterRequest.builder()
                .clusterName(model.getClusterName())
                .entries(associateMembers.stream()
                        .map(associatedMember -> AssociateCoreDeviceWithClusterEntry.builder()
                                .coreDeviceThingName(associatedMember)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Request to disassociate members from cluster
     * @param model resource model
     * @param associatedMembers members to disassociate
     * @return awsRequest the aws service request to disassociate core devices from a cluster
     */
    static DisassociateCoreDevicesFromClusterRequest translateToDisassociateRequest(final ResourceModel model,
                                                                                    final List<String> associatedMembers) {
        if (model.getAssociatedMembers() == null) {
            // No disassociations will cause a request build failure, so return null and handle that
            return null;
        }

        return DisassociateCoreDevicesFromClusterRequest.builder()
                .clusterName(model.getClusterName())
                .entries(associatedMembers.stream()
                        .map(associatedMember -> DisassociateCoreDeviceFromClusterEntry.builder()
                                .coreDeviceThingName(associatedMember)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Request to list core devices associated with a cluster
     * @param clusterName cluster name
     * @return awsRequest the aws service request to list core devices associated with a cluster
     */
    static ListCoreDevicesAssociatedWithClusterRequest translateToListAssociatedRequest(final String clusterName) {
        return ListCoreDevicesAssociatedWithClusterRequest.builder()
                .clusterName(clusterName)
                .maxResults(10)
                .build();
    }

    /**
     * Request to tag a resource
     * @param clusterArn cluster arn
     * @param tagsToAdd tags to add
     * @return awsRequest the aws service request to tag a resource
     */
    public static TagResourceRequest translateToTagResourceRequest(String clusterArn, Map<String, String> tagsToAdd) {
        return TagResourceRequest.builder()
                .resourceArn(clusterArn)
                .tags(tagsToAdd)
                .build();
    }

    /**
     * Request to untag a resource
     * @param clusterArn cluster arn
     * @param tagsToRemove tags to remove
     * @return awsRequest the aws service request to untag a resource
     */
    public static UntagResourceRequest translateToUntagResourceRequest(String clusterArn, Map<String, String> tagsToRemove) {
        return UntagResourceRequest.builder()
                .resourceArn(clusterArn)
                .tagKeys(tagsToRemove.keySet())
                .build();
    }

    private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
        return Optional.ofNullable(collection)
                .map(Collection::stream)
                .orElseGet(Stream::empty);
    }
}

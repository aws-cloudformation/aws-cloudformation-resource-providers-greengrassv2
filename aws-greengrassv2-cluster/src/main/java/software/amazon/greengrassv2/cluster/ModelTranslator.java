/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.cluster;

public class ModelTranslator {

    static software.amazon.awssdk.services.greengrassv2.model.ClusterConfiguration translateToSdkClusterConfiguration(
            ClusterConfiguration configuration) {
        return software.amazon.awssdk.services.greengrassv2.model.ClusterConfiguration.builder()
                .minimumHealthyNodes(configuration.getMinimumHealthyNodes())
                .minimumLeaderElectionTimeout(configuration.getMinimumLeaderElectionTimeout())
                .maximumLeaderElectionTimeout(configuration.getMaximumLeaderElectionTimeout())
                .clusterClientPort(configuration.getClusterClientPort())
                .clusterServerPort(configuration.getClusterServerPort())
                .build();
    }

    static ClusterConfiguration translateToCfnClusterConfiguration(
            software.amazon.awssdk.services.greengrassv2.model.ClusterConfiguration configuration) {
        return ClusterConfiguration.builder()
                .minimumHealthyNodes(configuration.minimumHealthyNodes())
                .minimumLeaderElectionTimeout(configuration.minimumLeaderElectionTimeout())
                .maximumLeaderElectionTimeout(configuration.maximumLeaderElectionTimeout())
                .clusterClientPort(configuration.clusterClientPort())
                .clusterServerPort(configuration.clusterServerPort())
                .build();
    }
}

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
  public static GreengrassV2Client getClient() {
    return GreengrassV2Client.builder()
            .httpClient(LambdaWrapper.HTTP_CLIENT)
            .build();
  }
}

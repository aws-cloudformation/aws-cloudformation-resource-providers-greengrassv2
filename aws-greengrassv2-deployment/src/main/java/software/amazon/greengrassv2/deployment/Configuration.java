/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import java.util.Map;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-greengrassv2-deployment.json");
    }

    @Override
    public Map<String, String> resourceDefinedTags(ResourceModel resourceModel) {
        return resourceModel.getTags();
    }
}

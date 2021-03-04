/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.componentversion;

import software.amazon.cloudformation.proxy.StdCallbackContext;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode(callSuper = true)
public class CallbackContext extends StdCallbackContext {
    /**
     * Used as a part of checking whether read-only properties were specified
     * as a part of an initial create or not (since the same properties are set
     * in the model as part of the creation process).
     */
    boolean resourceCreated = false;
}

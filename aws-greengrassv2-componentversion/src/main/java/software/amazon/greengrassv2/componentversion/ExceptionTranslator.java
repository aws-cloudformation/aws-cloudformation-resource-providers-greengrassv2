/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.services.greengrassv2.model.AccessDeniedException;
import software.amazon.awssdk.services.greengrassv2.model.ConflictException;
import software.amazon.awssdk.services.greengrassv2.model.InternalServerException;
import software.amazon.awssdk.services.greengrassv2.model.ResourceNotFoundException;
import software.amazon.awssdk.services.greengrassv2.model.ThrottlingException;
import software.amazon.awssdk.services.greengrassv2.model.ValidationException;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;

public class ExceptionTranslator {

    public static BaseHandlerException translateToCfnExceptionForCreatedResource(String operationName,
                                                                                 String resourceIdentifier,
                                                                                 final Exception ex) {
        if (ex instanceof ResourceNotFoundException) {
            return new CfnNotFoundException(ResourceModel.TYPE_NAME, resourceIdentifier, ex);
        } else if(ex instanceof ConflictException) {
            return new CfnResourceConflictException(ex);
        }

        return translateToCfnException(operationName, ex);
    }

    public static BaseHandlerException translateToCfnException(String operationName, final Throwable ex) {

        if (ex instanceof AccessDeniedException) {
            return new CfnAccessDeniedException(ex);
        } else if (ex instanceof InternalServerException) {
            return new CfnServiceInternalErrorException(operationName, ex);
        } else if (ex instanceof ValidationException) {
            return new CfnInvalidRequestException(ex);
        } else if (ex instanceof ThrottlingException) {
            return new CfnThrottlingException(ex);
        }

        return new CfnGeneralServiceException(ex);
    }
}

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.cluster;

import software.amazon.awssdk.services.greengrassv2.model.AccessDeniedException;
import software.amazon.awssdk.services.greengrassv2.model.ConflictException;
import software.amazon.awssdk.services.greengrassv2.model.InternalServerException;
import software.amazon.awssdk.services.greengrassv2.model.RequestAlreadyInProgressException;
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
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;

public class ExceptionTranslator {

    public static BaseHandlerException translateToCfnExceptionForCreatedResource(String operationName,
                                                                                 String resourceIdentifier,
                                                                                 final Exception ex) {
        if (ex instanceof ResourceNotFoundException) {
            return new CfnNotFoundException(ResourceModel.TYPE_NAME, resourceIdentifier, ex);
        }
        return translateToCfnException(operationName, ex);
    }

    public static BaseHandlerException translateToCfnException(
            final String operationName,
            final Throwable exception) {
        if (exception instanceof ResourceNotFoundException) {
            return new CfnNotFoundException(operationName, exception.getMessage());
        } else if (exception instanceof ValidationException) {
            return new CfnInvalidRequestException(operationName, exception);
        } else if (exception instanceof AccessDeniedException) {
            return new CfnAccessDeniedException(operationName, exception);
        } else if (exception instanceof InternalServerException) {
            return new CfnServiceInternalErrorException(operationName, exception);
        } else if (exception instanceof ThrottlingException) {
            return new CfnServiceLimitExceededException(exception);
        } else if (exception instanceof ConflictException || exception instanceof RequestAlreadyInProgressException) {
            return new CfnResourceConflictException(ResourceModel.TYPE_NAME, operationName, exception.getMessage());
        } else {
            return new CfnGeneralServiceException(exception.getMessage(), exception);
        }
    }
}

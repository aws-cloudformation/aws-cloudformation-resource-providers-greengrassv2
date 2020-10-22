package software.amazon.greengrassv2.deployment;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.iot.model.InternalFailureException;
import software.amazon.awssdk.services.iot.model.InvalidRequestException;
import software.amazon.awssdk.services.iot.model.ResourceAlreadyExistsException;
import software.amazon.awssdk.services.iot.model.ServiceUnavailableException;
import software.amazon.awssdk.services.iot.model.ThrottlingException;
import software.amazon.awssdk.services.iot.model.UnauthorizedException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;

public class ExceptionTranslator {

    public static void translateToCfnException(
            final AwsServiceException exception) {
        if (exception instanceof InvalidRequestException) {
            throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME, exception);
        }
        if (exception instanceof ThrottlingException) {
            throw new CfnThrottlingException(ResourceModel.TYPE_NAME, exception);
        }
        if (exception instanceof UnauthorizedException) {
            throw new CfnAccessDeniedException(ResourceModel.TYPE_NAME, exception);
        }
        if (exception instanceof ServiceUnavailableException || exception instanceof InternalFailureException) {
            throw new CfnServiceInternalErrorException(ResourceModel.TYPE_NAME, exception);
        }
        if (exception instanceof ResourceAlreadyExistsException) {
            throw new CfnResourceConflictException(exception);
        }
        throw new CfnGeneralServiceException(exception.getMessage(), exception);
    }
}

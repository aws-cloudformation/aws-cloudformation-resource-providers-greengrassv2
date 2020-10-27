package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.dynamodb.model.InternalServerErrorException;
import software.amazon.awssdk.services.dynamodb.model.LimitExceededException;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnResourceConflictException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnServiceLimitExceededException;

public class ExceptionTranslator {

    public static void translateToCfnException(
            final AwsServiceException exception) {
        if (exception instanceof InternalServerErrorException) {
            throw new CfnServiceInternalErrorException(ResourceModel.TYPE_NAME, exception);
        }
        else if (exception instanceof ResourceInUseException) {
            throw new CfnResourceConflictException(exception);
        }
        else if (exception instanceof LimitExceededException) {
            throw new CfnServiceLimitExceededException(exception);
        }

        throw new CfnGeneralServiceException(exception.getMessage(), exception);
    }
}

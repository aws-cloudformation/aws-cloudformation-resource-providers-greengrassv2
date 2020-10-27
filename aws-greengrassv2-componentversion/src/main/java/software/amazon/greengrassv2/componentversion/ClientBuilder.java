package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
  public static DynamoDbClient getClient() {
    return DynamoDbClient.builder()
            .httpClient(LambdaWrapper.HTTP_CLIENT)
            .build();
  }
}

package software.amazon.greengrassv2.componentversion;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<DynamoDbClient> proxyClient;

    @Mock
    DynamoDbClient dynamoDbClient;

    @BeforeEach
    public void setup() {
        proxy = spy(new AmazonWebServicesClientProxy(logger, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis()));
        dynamoDbClient = mock(DynamoDbClient.class);
        proxyClient = MOCK_PROXY(proxy, dynamoDbClient);
    }

    @AfterEach
    public void tear_down() {
        verify(dynamoDbClient, atLeastOnce()).serviceName();
        verifyNoMoreInteractions(dynamoDbClient);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        CreateTableResponse createTableResponse = CreateTableResponse.builder().build();

        DescribeTableResponse describeTableResponseWithCreating = DescribeTableResponse.builder()
                .table(TableDescription.builder()
                        .tableStatus(TableStatus.CREATING)
                        .build())
                .build();

        DescribeTableResponse describeTableResponseWithActive = DescribeTableResponse.builder()
                .table(TableDescription.builder()
                        .tableStatus(TableStatus.ACTIVE)
                        .build())
                .build();

        doReturn(createTableResponse, describeTableResponseWithCreating, describeTableResponseWithActive)
                .when(proxy)
                .injectCredentialsAndInvokeV2(ArgumentMatchers.any(), ArgumentMatchers.any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ThrowException_When_DDB_in_wrong_status() {
        final CreateHandler handler = new CreateHandler();

        final ResourceHandlerRequest<ResourceModel> request =
                ResourceHandlerRequest.<ResourceModel>builder().desiredResourceState(ResourceModel.builder().build()).build();

        CreateTableResponse createTableResponse = CreateTableResponse.builder().build();

        DescribeTableResponse describeTableResponse = DescribeTableResponse.builder()
                .table(TableDescription.builder()
                        .tableStatus(TableStatus.DELETING)
                        .build())
                .build();

        doReturn(createTableResponse, describeTableResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(ArgumentMatchers.any(), ArgumentMatchers.any());

        assertThrows(CfnGeneralServiceException.class,
                () -> handler.handleRequest(proxy, request, new CallbackContext(), proxyClient, logger),
                "Expected handler throws CfnGeneralServiceException, but it did not");
    }
}

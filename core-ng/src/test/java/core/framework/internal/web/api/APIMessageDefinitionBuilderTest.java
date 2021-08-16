package core.framework.internal.web.api;

import core.framework.internal.kafka.TestMessage;
import core.framework.internal.module.ServiceRegistry;
import core.framework.json.JSON;
import core.framework.util.ClasspathResources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author neo
 */
class APIMessageDefinitionBuilderTest {
    private APIMessageDefinitionBuilder builder;

    @BeforeEach
    void createAPIDefinitionBuilder() {
        var messages = new ArrayList<ServiceRegistry.MessagePublish>();
        messages.add(new ServiceRegistry.MessagePublish("topic", TestMessage.class));
        builder = new APIMessageDefinitionBuilder(messages);
    }

    @Test
    void build() {
        APIMessageDefinitionResponse response = builder.build();
        APIMessageDefinitionResponse expectedResponse = JSON.fromJSON(APIMessageDefinitionResponse.class, ClasspathResources.text("api-test/test-message.json"));
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
    }
}

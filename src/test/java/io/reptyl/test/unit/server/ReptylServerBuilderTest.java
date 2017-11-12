package io.reptyl.test.unit.server;

import io.reptyl.ReptylServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static io.reptyl.ReptylServer.Builder.DEFAULT_HOST;
import static io.reptyl.ReptylServer.Builder.DEFAULT_PORT;
import static io.reptyl.ReptylServer.Builder.DEFAULT_WORKER_NAME;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * This test class must reside in a subpackage of {@link io.reptyl.test.unit} because of the classpath scanning, otherwise
 * the scanner would find the controllers created for the other test classes.
 * Because of the dependency injection system, the test cases will not make use of a regular argument captor, but will
 * inspect the configuration object by reflection.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReptylServerBuilderTest {

    @Test
    public void useDefaultServerPort() throws Exception {

        ReptylServer.Builder builder = ReptylServer.builder();
        builder.build();

        ReptylServer server = builder.build();

        assertThat("the default port should be used if no explicit configuration given", server.getServerConfiguration().getPort(), equalTo(DEFAULT_PORT));
    }

    @Test
    public void useDefaultServerHost() throws Exception {

        ReptylServer.Builder builder = ReptylServer.builder();
        builder.build();

        ReptylServer server = builder.build();

        assertThat("the default host should be used if no explicit configuration given", server.getServerConfiguration().getHost(), equalTo(DEFAULT_HOST));
    }

    @Test
    public void useDefaultWorkerName() throws Exception {

        ReptylServer.Builder builder = ReptylServer.builder();
        builder.build();

        ReptylServer server = builder.build();

        assertThat("the default worker name should be used if no explicit configuration given", server.getServerConfiguration().getWorkerName(), equalTo(DEFAULT_WORKER_NAME));
    }

    @Test
    public void applyTheConfiguredServerPort() throws Exception {

        ReptylServer.Builder builder = ReptylServer
                .builder()
                .port(1234);

        builder.build();

        ReptylServer server = builder.build();

        assertThat("the given port should be used", server.getServerConfiguration().getPort(), equalTo(1234));
    }

    @Test
    public void applyTheConfiguredServerHost() throws Exception {

        ReptylServer.Builder builder = ReptylServer
                .builder()
                .host("example.org");

        ReptylServer server = builder.build();

        assertThat("the given host should be used", server.getServerConfiguration().getHost(), equalTo("example.org"));
    }

    @Test
    public void applyTheConfiguredWorkerName() throws Exception {

        ReptylServer.Builder builder = ReptylServer
                .builder()
                .workerName("TEST-WORKER");

        builder.build();

        ReptylServer server = builder.build();

        assertThat("the given host should be used", server.getServerConfiguration().getWorkerName(), equalTo("TEST-WORKER"));
    }
}

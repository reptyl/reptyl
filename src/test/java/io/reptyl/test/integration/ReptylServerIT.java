package io.reptyl.test.integration;

import io.reptyl.ReptylServer;
import io.restassured.RestAssured;
import io.undertow.server.HttpServerExchange;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.undertow.util.StatusCodes.INSUFFICIENT_STORAGE;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

public class ReptylServerIT {

    @BeforeClass
    public static void setUp() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = 8080;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void applicationShouldRespondOnDefaultPortWithoutConfiguration() {

        ReptylServer reptylServer = ReptylServer
                .builder()
                .build();

        reptylServer.start();

        // @formatter:off

        when()
            .get()
        .then()
            .assertThat()
                .statusCode(SC_NOT_FOUND);

        // @formatter:on

        reptylServer.stop();
    }

    @Test
    public void multipleServersOnDifferentPortsCanBeCreatedWithoutConfiguration() {

        ReptylServer reptylServer1 = ReptylServer
                .builder()
                .port(8080)
                .build();

        reptylServer1.start();

        ReptylServer reptylServer2 = ReptylServer
                .builder()
                .port(8081)
                .build();

        reptylServer2.start();

        // @formatter:off

        given()
            .port(8080)
        .when()
            .get()
        .then()
            .assertThat()
                .statusCode(SC_NOT_FOUND);

        given()
            .port(8081)
        .when()
            .get()
        .then()
            .assertThat()
                .statusCode(SC_NOT_FOUND);

        // @formatter:on

        reptylServer1.stop();
        reptylServer2.stop();
    }

    @Test
    public void eachServerShouldSupportIsolatedPackageScanning() {

        ReptylServer reptylServer1 = ReptylServer
                .builder()
                .port(8080)
                .scanPackage("io.reptyl.test.integration.package1")
                .build();

        reptylServer1.start();

        ReptylServer reptylServer2 = ReptylServer
                .builder()
                .port(8081)
                .scanPackage("io.reptyl.test.integration.package2")
                .build();

        reptylServer2.start();

        // @formatter:off

        /*
         * THE FIRST SERVER SHOULD SERVE /test1 AND /test2 BUT REFUSE /test3
         */

        given()
            .port(8080)
        .when()
            .get("/test1")
        .then()
            .assertThat()
                .statusCode(SC_ACCEPTED);

        given()
            .port(8080)
        .when()
            .get("/test2")
        .then()
            .assertThat()
                .statusCode(SC_ACCEPTED);

        given()
            .port(8080)
        .when()
            .get("/test3")
        .then()
            .assertThat()
                .statusCode(SC_NOT_FOUND);

        /*
         * THE SECOND SERVER SHOULD SERVE /test3 ONLY
         */

        given()
            .port(8081)
        .when()
            .get("/test1")
        .then()
            .assertThat()
                .statusCode(SC_NOT_FOUND);

        given()
            .port(8081)
        .when()
            .get("/test2")
        .then()
            .assertThat()
                .statusCode(SC_NOT_FOUND);

        given()
            .port(8081)
        .when()
            .get("/test3")
        .then()
            .assertThat()
                .statusCode(SC_ACCEPTED);

        // @formatter:on

        reptylServer1.stop();
        reptylServer2.stop();
    }

    @Test
    public void annotatedExceptionsShouldBeApplied() {

        ReptylServer reptylServer1 = ReptylServer
                .builder()
                .port(8080)
                .scanPackage("io.reptyl.test.integration.package3")
                .build();

        reptylServer1.start();

        // @formatter:off

        given()
            .port(8080)
        .when()
            .get("/test")
        .then()
            .assertThat()
                .statusCode(INSUFFICIENT_STORAGE);

        // @formatter:on

        reptylServer1.stop();
    }

    @Test
    public void manuallyRegisterController() {

        ReptylServer reptylServer1 = ReptylServer
                .builder()
                .port(8080)
                .withController(Controller.class)
                .build();

        reptylServer1.start();

        // @formatter:off

        given()
            .port(8080)
        .when()
            .get("/test-manually-registered")
        .then()
            .assertThat()
                .statusCode(INSUFFICIENT_STORAGE);

        // @formatter:on

        reptylServer1.stop();
    }

    @Singleton
    public static class Controller {

        @GET
        @Path("/test-manually-registered")
        public void test(HttpServerExchange exchange) {
            exchange.setStatusCode(INSUFFICIENT_STORAGE);
            exchange.getResponseSender().send("");
        }
    }
}

package io.reptyl.test.integration;

import io.reptyl.ReptylServer;
import io.reptyl.test.integration.package3.Controller4;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.undertow.util.StatusCodes.INSUFFICIENT_STORAGE;
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
    public void annotatedExceptionsShouldBeApplied() {

        ReptylServer reptylServer1 = ReptylServer
                .builder()
                .port(8080)
                .withController(new Controller4())
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
}

package io.reptyl.test.example;

import io.reptyl.example.petstore.PetStore;
import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.undertow.util.StatusCodes.OK;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

public class PetStoreTest {

    private static PetStore petStore;

    @BeforeClass
    public static void setUp() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = 8081;
        RestAssured.baseURI = "http://localhost";

        petStore = PetStore.build();
        petStore.start();
    }

    @AfterClass
    public static void tearDown() {

        petStore.stop();
    }

    @Test
    public void petCollectionShouldBeReadable() {

        // @formatter:off

        when()
            .get("/pets")
        .then()
            .assertThat()
                .statusCode(OK)
                .body("id", hasSize(3));

        // @formatter:on
    }

    @Test
    public void petShouldBeReadable() {

        // @formatter:off

        when()
            .get("/pets/1")
        .then()
            .assertThat()
                .statusCode(OK)
                .body("id", notNullValue());

        // @formatter:on
    }

    @Test
    public void storeCollectionShouldBeReadable() {

        // @formatter:off

        when()
            .get("/stores")
        .then()
            .assertThat()
                .statusCode(OK)
                .body("id", hasSize(2));

        // @formatter:on
    }

    @Test
    public void storeShouldBeReadable() {

        // @formatter:off

        given()
            .baseUri("http://localhost")
            .port(8081)
        .when()
            .get("/stores/1")
        .then()
            .assertThat()
                .statusCode(OK)
                .body("id", notNullValue());

        // @formatter:on
    }

    @Test
    public void petsInStoreCollectionShouldBeReadable() {

        // @formatter:off

        when()
            .get("/stores/1/pets")
        .then()
            .assertThat()
                .statusCode(OK)
                .body("id", hasSize(2));

        // @formatter:on
    }
}

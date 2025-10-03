package com.example.demoservice.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import java.io.File;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiIsolatedTest {
    @Container
    static DockerComposeContainer<?> compose = new DockerComposeContainer<>(
            new File("docker-compose.yaml")
    ).withExposedService("demo-service", 8081);

    @BeforeAll
    static void setup() {
        compose.start();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @AfterAll
    static void teardown() {
        compose.stop();
    }

    @Test
    @Order(1)
    void createUser_shouldReturnCreatedUser() {
        given()
            .contentType("application/json")
            .body("{" +
                    "\"username\": \"apiuser\"," +
                    "\"password\": \"password123\"," +
                    "\"email\": \"apiuser@example.com\"," +
                    "\"firstName\": \"Api\"," +
                    "\"lastName\": \"User\"," +
                    "\"enabled\": true," +
                    "\"role\": \"USER\"}")
        .when()
            .post("/api/users")
        .then()
            .statusCode(201)
            .body("username", equalTo("apiuser"));
    }

    @Test
    @Order(2)
    void getAllUsers_shouldReturnList() {
        when()
            .get("/api/users")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(3)
    void createUser_shouldFailValidation() {
        given()
            .contentType("application/json")
            .body("{" +
                    "\"username\": \"\"," +
                    "\"password\": \"123\"," +
                    "\"email\": \"not-an-email\"," +
                    "\"firstName\": \"\"," +
                    "\"lastName\": \"\"," +
                    "\"enabled\": true," +
                    "\"role\": \"\"}")
        .when()
            .post("/api/users")
        .then()
            .statusCode(400)
            .body("username", notNullValue())
            .body("password", notNullValue())
            .body("email", notNullValue());
    }
}


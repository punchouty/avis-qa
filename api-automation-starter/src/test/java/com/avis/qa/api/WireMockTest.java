package com.avis.qa.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class WireMockTest {

    private GenericContainer<?> wiremockContainer;
    private String wiremockBase;

    @BeforeClass
    public void setup() {
        wiremockContainer = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock"));
        wiremockContainer.withExposedPorts(8080)
                .withClasspathResourceMapping("wiremock",
                        "/home/wiremock",
                        BindMode.READ_ONLY)
                .withNetwork(Network.SHARED)
                .withNetworkAliases("wiremock");
        wiremockContainer.start();
        wiremockBase = "http://" + wiremockContainer.getHost() + ":" + wiremockContainer.getMappedPort(8080);

    }

    @AfterClass
    public void teardown() {
        wiremockContainer.stop();
    }

    @Test
    public void wiremockMapping() {
        RestAssured.baseURI = wiremockBase;
        Response response = given().get("/endpoint_1/success.json");
        System.out.println(response.asPrettyString());
    }

    @Test
    public void wiremockFiles() {
        RestAssured.baseURI = wiremockBase;
        Response response = given().get("/users/2");
        System.out.println(response.asPrettyString());
    }
}

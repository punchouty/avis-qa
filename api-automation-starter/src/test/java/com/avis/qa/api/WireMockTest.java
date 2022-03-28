package com.avis.qa.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.utility.DockerImageName;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class WireMockTest {

    private GenericContainer<?> wiremockContainer;
    private PostgreSQLContainer<?> postgreSQLContainer;
    private GenericContainer<?> apiContainer;
    private String apiBase;
    private String wiremockBase;

    @BeforeClass
    public void setup() {
        Network network = Network.newNetwork();
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:10.5")
                .withDatabaseName("integration-tests-db")
                .withUsername("sa")
                .withPassword("sa")
                .withExposedPorts(5432)
                .withNetwork(network)
                .withNetworkAliases("db");
        postgreSQLContainer.start();

        JdbcDatabaseDelegate containerDelegate = new JdbcDatabaseDelegate(postgreSQLContainer, "");
        ScriptUtils.runInitScript(containerDelegate, "data/1-schema.sql");
        ScriptUtils.runInitScript(containerDelegate, "data/2-data.sql");

        String dbJdbcUrl = "jdbc:postgresql://db:5432/integration-tests-db";

        wiremockContainer = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock"));
        wiremockContainer.withExposedPorts(8080)
                .withClasspathResourceMapping("wiremock",
                        "/home/wiremock",
                        BindMode.READ_ONLY)
                .withNetwork(network)
                .withNetworkAliases("api");
        wiremockContainer.start();
        wiremockBase = "http://" + wiremockContainer.getHost() + ":" + wiremockContainer.getMappedPort(8080);

        apiContainer = new GenericContainer<>(DockerImageName.parse("demo-api:0.1"));
        apiContainer.withExposedPorts(8080)
                .dependsOn(postgreSQLContainer, wiremockContainer)
                .withEnv("DB_URL", dbJdbcUrl)
                .withEnv("DB_USER", "sa")
                .withEnv("DB_PASSWORD", "sa")
                .withNetwork(network)
                .withNetworkAliases("api");
        apiContainer.start();
        apiBase = "http://" + apiContainer.getHost() + ":" + apiContainer.getMappedPort(8080);
        System.out.println("RestAssured.baseURI : " + RestAssured.baseURI);
    }

    @AfterClass
    public void teardown() {
        apiContainer.stop();
        postgreSQLContainer.stop();
        wiremockContainer.stop();
    }

    @Test
    public void test() {
        RestAssured.baseURI = apiBase;
        Response response = given().get("/locations");
        System.out.println(response.asPrettyString());
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
        Response response = given().get("/api/mytest");
        System.out.println(response.asPrettyString());
    }
}

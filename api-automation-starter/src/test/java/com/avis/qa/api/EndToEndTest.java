package com.avis.qa.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.utility.DockerImageName;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;

public class EndToEndTest {

    private PostgreSQLContainer<?> postgreSQLContainer;
    private GenericContainer<?> apiContainer;

    @BeforeClass
    public void setup() {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:10.5")
                .withDatabaseName("integration-tests-db")
                .withUsername("sa")
                .withPassword("sa")
                .withExposedPorts(5432)
                .withNetwork(Network.SHARED)
                .withNetworkAliases("db");
        postgreSQLContainer.start();

        JdbcDatabaseDelegate containerDelegate = new JdbcDatabaseDelegate(postgreSQLContainer, "");
        ScriptUtils.runInitScript(containerDelegate, "data/1-schema.sql");
        ScriptUtils.runInitScript(containerDelegate, "data/2-data.sql");

        String dbJdbcUrl = "jdbc:postgresql://db:5432/integration-tests-db";

        apiContainer = new GenericContainer<>(DockerImageName.parse("demo-api:0.1"));
        apiContainer.withExposedPorts(9000)
                .dependsOn(postgreSQLContainer)
                .withEnv("EXTERNAL_API_URL", "https://reqres.in/api")
                .withEnv("DB_URL", dbJdbcUrl)
                .withEnv("DB_USER", "sa")
                .withEnv("DB_PASSWORD", "sa")
                .withNetwork(Network.SHARED)
                .withNetworkAliases("api");
        apiContainer.start();
        RestAssured.baseURI = "http://" + apiContainer.getHost() + ":" + apiContainer.getMappedPort(9000);
        System.out.println("RestAssured.baseURI : " + RestAssured.baseURI);
    }

    @AfterClass
    public void teardown() {
        apiContainer.stop();
        postgreSQLContainer.stop();
    }

    @Test
    public void e2e() {
        Response response = given().get("/users/2");
        System.out.println(response.asPrettyString());
    }

    @Test
    public void test() {
        Response response = given().get("/locations");
        System.out.println(response.asPrettyString());
    }
}

package com.avis.qa.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.utility.DockerImageName;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class ComponentTest {

    private GenericContainer<?> wiremockContainer;
    private PostgreSQLContainer<?> postgreSQLContainer;
    private GenericContainer<?> apiContainer;
    private String apiBase;
    private String wiremockBase;

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

        wiremockContainer = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock"));
        wiremockContainer.withExposedPorts(8080)
                .withClasspathResourceMapping("wiremock",
                        "/home/wiremock",
                        BindMode.READ_ONLY)
                .withNetwork(Network.SHARED)
                .withNetworkAliases("wiremock")
                .withLogConsumer(new ToStringConsumer() {
                    @Override
                    public void accept(OutputFrame outputFrame) {
                        if (outputFrame.getBytes() != null) {
                            try {
                                System.out.write(outputFrame.getBytes());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
        wiremockContainer.start();
        wiremockBase = "http://wiremock:8080";

        apiContainer = new GenericContainer<>(DockerImageName.parse("demo-api:0.1"));
        apiContainer.withExposedPorts(9000)
                .dependsOn(postgreSQLContainer, wiremockContainer)
                .withEnv("EXTERNAL_API_URL", wiremockBase)
                .withEnv("DB_URL", dbJdbcUrl)
                .withEnv("DB_USER", "sa")
                .withEnv("DB_PASSWORD", "sa")
                .withNetwork(Network.SHARED)
                .withNetworkAliases("api")
                .withLogConsumer(new ToStringConsumer() {
                    @Override
                    public void accept(OutputFrame outputFrame) {
                        if (outputFrame.getBytes() != null) {
                            try {
                                System.out.write(outputFrame.getBytes());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
        apiContainer.start();
        apiBase = "http://" + apiContainer.getHost() + ":" + apiContainer.getMappedPort(9000);
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
        Response response = given().get("/users/2");
        System.out.println(response.asPrettyString());
    }
}

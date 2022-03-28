package com.avis.qa.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.utility.DockerImageName;
import org.testng.annotations.*;

import io.restassured.RestAssured.*;
import io.restassured.matcher.RestAssuredMatchers.*;
import org.hamcrest.Matchers.*;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static io.restassured.RestAssured.given;

public class SampleApiTest {

    private PostgreSQLContainer<?> postgreSQLContainer;
    private GenericContainer<?> apiContainer;

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

        apiContainer = new GenericContainer<>(DockerImageName.parse("demo-api:0.1"));
        apiContainer.withExposedPorts(8080)
                .dependsOn(postgreSQLContainer)
                .withEnv("DB_URL", dbJdbcUrl)
                .withEnv("DB_USER", "sa")
                .withEnv("DB_PASSWORD", "sa")
                .withNetwork(network)
                .withNetworkAliases("api");
        apiContainer.start();
        RestAssured.baseURI = "http://" + apiContainer.getHost() + ":" + apiContainer.getMappedPort(8080);
        System.out.println("RestAssured.baseURI : " + RestAssured.baseURI);
    }

    @AfterClass
    public void teardown() {
        apiContainer.stop();
        postgreSQLContainer.stop();
    }

    @Test
    public void test() {
        Response response = given().get("/locations");
        System.out.println(response.asPrettyString());
    }

    @Test
    public void debug() throws SQLException {
        System.out.println("******* DB Table location ************");
        ResultSet resultSet = performQuery(postgreSQLContainer, "SELECT * from location");
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1) + "|" + resultSet.getString(2) + "|" + resultSet.getString(3) + "|" + resultSet.getString(4));
        }
        System.out.println("Simple test : " + postgreSQLContainer.getDatabaseName());
        String logs = postgreSQLContainer.getLogs();
        System.out.println("******* Postgres ************");
        System.out.println(logs);
        logs = apiContainer.getLogs();
        System.out.println("******* API ************");
        System.out.println(logs);
        Response response = given().get("/locations");
        System.out.println(response.asPrettyString());

    }

    @Test
    public void db() throws SQLException {
        ResultSet resultSet = performQuery(postgreSQLContainer, "SELECT * from location");
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1) + "|" + resultSet.getString(2) + "|" + resultSet.getString(3) + "|" + resultSet.getString(4));
        }

    }

    protected ResultSet performQuery(JdbcDatabaseContainer<?> container, String sql) throws SQLException {
        DataSource ds = getDataSource(container);
        Statement statement = ds.getConnection().createStatement();
        statement.execute(sql);
        return statement.getResultSet();
    }

    protected DataSource getDataSource(JdbcDatabaseContainer<?> container) {
        System.out.println("getJdbcUrl : " + container.getJdbcUrl());
        System.out.println("getUsername : " + container.getUsername());
        System.out.println("getPassword : " + container.getPassword());
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(container.getJdbcUrl());
        hikariConfig.setUsername(container.getUsername());
        hikariConfig.setPassword(container.getPassword());
        hikariConfig.setDriverClassName(container.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }
}

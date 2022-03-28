package com.avis.qa.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.restassured.RestAssured;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.utility.DockerImageName;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static io.restassured.RestAssured.given;

public class SampleDatabaseTest {

    private PostgreSQLContainer<?> postgreSQLContainer;
    private GenericContainer<?> apiContainer;
    private static String api_url = "";

    @BeforeTest
    public void setup() {
        Network network = Network.newNetwork();
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:10.5")
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
    }

    @AfterTest
    public void teardown() {
        postgreSQLContainer.stop();
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

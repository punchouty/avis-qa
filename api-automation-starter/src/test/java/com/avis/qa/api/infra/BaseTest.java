package com.avis.qa.api.infra;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Properties;

import com.avis.qa.api.util.DbManager;
import com.avis.qa.api.util.ExcelReader;
import com.avis.qa.api.util.MonitoringMail;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.Status;

public class BaseTest {

    private static Properties or = new Properties();
    private static Properties config = new Properties();
    private static FileInputStream fis;
    private static Logger log = Logger.getLogger(BaseTest.class);
    public static ExcelReader excel = new ExcelReader(".//src//test//resources//excel//testdata.xlsx");
    public static MonitoringMail mail = new MonitoringMail();

    @BeforeSuite
    public void setUp() {
        // loading the log file
        PropertyConfigurator.configure("./src/test/resources/properties/log4j.properties");

        // loading the OR and Config properties
        try {
            fis = new FileInputStream("./src/test/resources/properties/config.properties");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            config.load(fis);
            log.info("Config properties loaded !!!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            fis = new FileInputStream("./src/test/resources/properties/OR.properties");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            or.load(fis);
            log.info("OR properties loaded !!!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            DbManager.setMysqlDbConnection();
            log.info("DB Connection established !!!");
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @AfterSuite
    public void tearDown() {
        log.info("Test Execution completed !!!");
    }

}

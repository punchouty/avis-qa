package com.avis.qa.api.util;

public class TestConfig {

    public static String server="smtp.gmail.com";
    public static String from = "corporate@way2automation.com";
    public static String password = "Selenium@123";
    public static String[] to ={"seleniumcoaching@gmail.com","trainer@way2automation.com"};
    public static String subject = "Test Report";

    public static String messageBody ="TestMessage";
    public static String attachmentPath="C:\\Users\\way2automation\\Desktop\\Desktop\\devopsimg.jpg";
    public static String attachmentName="error.jpg";

    //SQL DATABASE DETAILS
    public static String driver="net.sourceforge.jtds.jdbc.Driver";
    public static String dbConnectionUrl="jdbc:jtds:sqlserver://192.101.44.22;DatabaseName=monitor_eval";
    public static String dbUserName="sa";
    public static String dbPassword="$ql$!!1";

    //MYSQL DATABASE DETAILS
    public static String databaseDriver="com.mysql.cj.jdbc.Driver";
    public static String databaseUserName = "root";
    public static String databasePassword = "selenium";
    public static String jdbcUrl = "jdbc:mysql://localhost:3306/batchoct2021";

}

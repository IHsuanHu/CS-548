package edu.stevens.cs548.clinic.webapp;


import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
// TODO define database connection
@DataSourceDefinition( name="java:global/jdbc/cs548",
        className="org.postgresql.ds.PGSimpleDataSource",
        user="${ENV=DATABASE_USERNAME}",
        password="${ENV=DATABASE_PASSWORD}",
        databaseName="${ENV=DATABASE}",
        serverName="${ENV=DATABASE_HOST}",
        portNumber=5432)
public class AppConfig {
}

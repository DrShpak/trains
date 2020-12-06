package dao.dbDAO;

import org.postgresql.util.PSQLException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnectionBuilder implements ConnectionBuilder {
    private final Properties properties;
    private final String PATH_TO_CONFIG = "src/main/resources/localConfig.properties";
    private final String DB_NAME = "trainsdb";

    public PostgresConnectionBuilder() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(PATH_TO_CONFIG));
            Class.forName(properties.getProperty("db.driver"));
        } catch (ClassNotFoundException | IOException ex) {
            //todo log it
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection con;
        try {
            con = DriverManager.getConnection(
                properties.getProperty("db.host") + DB_NAME,
                properties.getProperty("db.login"),
                properties.getProperty("db.password"));
        } catch (PSQLException e) {
            createDBandTables();
            con = DriverManager.getConnection(
                properties.getProperty("db.host") + DB_NAME,
                properties.getProperty("db.login"),
                properties.getProperty("db.password"));
        }
        return con;
    }

    private void createDBandTables() throws SQLException {
        var connection = DriverManager.getConnection(
            properties.getProperty("db.host"),
            properties.getProperty("db.login"),
            properties.getProperty("db.password"));

        //create "trainsdb" db
        var stm = connection.prepareStatement(properties.getProperty("CREATE_DATABASE"));
        stm.executeUpdate();

        //create routes table
        stm = connection.prepareStatement(properties.getProperty("CREATE_ROUTES_TABLE"));
        stm.executeUpdate();

        //create intermediate_stations table
        stm = connection.prepareStatement(properties.getProperty("CREATE_INTERMEDIATES_STATIONS_TABLE"));
        stm.executeUpdate();
    }
}
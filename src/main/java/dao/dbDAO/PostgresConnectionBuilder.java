package dao.dbDAO;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnectionBuilder implements ConnectionBuilder {
    private final Properties properties;
    private final String PATH_TO_CONFIG = "src/main/resources/postgresConfig.properties";

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
        return DriverManager.getConnection(
            properties.getProperty("db.host"),
            properties.getProperty("db.login"),
            properties.getProperty("db.password"));
    }
}
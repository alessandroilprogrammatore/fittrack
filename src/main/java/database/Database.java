package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for obtaining JDBC connections.
 */
public class Database {
    // JDBC 4 drivers such as PostgreSQL's automatically register themselves.
    // No manual Class.forName("org.postgresql.Driver") call is required.

    private Database() {}

    /**
     * Returns a connection using environment variables or default values.
     */
    public static Connection getConnection() throws SQLException {
        String url  = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/fittrack");
        String user = System.getenv().getOrDefault("DB_USER", "fittrack");
        String pass = System.getenv().getOrDefault("DB_PASS", "fittrack");
        return DriverManager.getConnection(url, user, pass);
    }
}

package tk.airshipcraft.playerstats.data;

import tk.airshipcraft.playerstats.PlayerStats;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class to handle database connection.
 * Will come back to it later as it's not really that important at this stage of development.
 */
/**
 * Establishes a connection to the database.
 */
public class UserDatabase {

    private static Connection connection;

    private PlayerStats plugin;

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static String DATABASE;
    private static String USERNAME;
    private static String PASSWORD;

    public UserDatabase(PlayerStats plugin) {
        this.plugin = plugin;

        this.DATABASE = "playerstats";
        this.USERNAME = "root";
        this.PASSWORD = plugin.getDbPass().getString("db-pass");
    }

    /**
     * Main connection method
     * @throws SQLException
     */
    public void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false",
                    USERNAME,
                    PASSWORD
            );
            createPlayerTable();

        } catch (SQLException e) {
            try {
                String mysqlUrl = "jdbc:mysql://localhost/";
                connection = DriverManager.getConnection(mysqlUrl, USERNAME, PASSWORD);
                String create_database = "CREATE DATABASE IF NOT EXISTS playerstats;";
                PreparedStatement stmt = connection.prepareStatement(create_database);
                stmt.execute();
                plugin.getLogger().info("Created database 'playerstats'...");
                createPlayerTable();

            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
    }

    /**
     * Boolean showing if connection is established.
     * @return boolean
     */
    public boolean isConnected() { return connection != null; }

    public static Connection getConnection() {
        return connection;
    }

    /**
     * Disconnect method
     */
    public void disconnect() {
        if(isConnected()) {
            try { //using a try catch to catch connection errors (like wrong sql password...)
                if (connection != null && !connection.isClosed()) { //checking if connection isn't null to
                    //avoid receiving a nullpointer
                    connection.close(); //closing the connection field variable.
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets info about the connection as a string including the HOST, PORT, DB, USER, PASS
     * @return String
     */
    public String getConnectionInfo() {
        return "HOST: " + HOST + " " +
                "PORT: " + PORT + " " +
                "DB: " + DATABASE + " " +
                "USER: " + USERNAME + " " +
                "PASS: " + PASSWORD;
    }

    public void createPlayerTable() {
        String create_applications = "CREATE TABLE IF NOT EXISTS players" +
                "(ID INT NOT NULL AUTO_INCREMENT , PRIMARY KEY (ID), UUID varchar(36), JOIN_DATE timestamp, BAL int);";
        try {
            PreparedStatement stmt = connection.prepareStatement(create_applications);
            stmt.executeUpdate();
            plugin.getLogger().info("Checking player table and creating if not exist...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
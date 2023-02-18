package tk.airshipcraft.playerstats;

import tk.zune.playerstats.PlayerStats;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Player object class for setting and getting values of players from database
 */
public class PlayerObject {

    private PlayerStats plugin;

    private UUID uuid;
    private Timestamp joinDate;
    private int bal;

    /**
     * Checks to see if player is in the database;
     * if not then sets default values for them
     * @param plugin
     * @param uuid
     * @throws SQLException
     */
    public PlayerObject(PlayerStats plugin, UUID uuid) throws SQLException {
        this.plugin = plugin;

        this.uuid = uuid;

        PreparedStatement stmt = plugin.getDb().getConnection().prepareStatement("SELECT JOIN_DATE, BAL FROM players WHERE UUID = ?;");
        stmt.setString(1, uuid.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            joinDate = rs.getTimestamp("JOIN_DATE");
            bal = rs.getInt("BAL");
        } else {
            joinDate = new java.sql.Timestamp(System.currentTimeMillis());
            bal = 0;
            PreparedStatement stmt1 = plugin.getDb().getConnection().prepareStatement("INSERT INTO players (ID, UUID, JOIN_DATE, BAL) VALUES (" +
                    "default," +
                    "'" + uuid + "'," +
                    joinDate +
                    bal + ");");
            stmt1.executeUpdate();
        }
    }

    /**
     * Sets the default balance
     * @param bal
     */
    public void setBal(int bal) {
        this.bal = bal;
        try {
            PreparedStatement stmt = plugin.getDb().getConnection().prepareStatement("UPDATE players SET BAL = " + bal + " WHERE UUID = '" + uuid + "';");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the join timestamp
     * @param joinDate
     */
    public void saveJoinDate(Timestamp joinDate) {
        this.joinDate = joinDate;
        try {
            PreparedStatement stmt = plugin.getDb().getConnection().prepareStatement("UPDATE players SET JOIN_DATE = " + joinDate + " WHERE UUID = '" + uuid + "';");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for join date of player
     * @return Timestamp
     */
    public Timestamp getJoinDate() { return joinDate; }

    /**
     * Getter for balance of player
     * @return bal
     */
    public int getBal() { return bal; }
}

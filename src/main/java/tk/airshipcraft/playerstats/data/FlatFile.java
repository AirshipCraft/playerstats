package tk.airshipcraft.playerstats.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tk.airshipcraft.playerstats.PlayerStats;

import java.io.File;
import java.util.UUID;

/**
 * Class that handles the creation of per-user data files
 *
 * TODO: Switch to MySQL or some other database instead of readable files
 */
public class FlatFile implements Listener {

    PlayerStats plugin;
    UUID u;
    File userFile;
    FileConfiguration userConfig;
    String dir = System.getProperty("user.dir");
    String directoryPathFile = dir + File.separator + "plugins" + File.separator + "UserData";
    String directoryPathFileData = directoryPathFile + File.separator + "data";

    public FlatFile(UUID u){
        this.u = u;
        userFile = new File(plugin.getDataFolder(), u + ".yml");
        userConfig = YamlConfiguration.loadConfiguration(userFile);
    }

    public FlatFile(PlayerStats plugin) {
        this.plugin = plugin;
    }

    public void dataFileCreator(String fileName) {
        if (new File(directoryPathFile).mkdirs())
            Bukkit.getLogger().info("Generated user data folder and files...");
        if (new File(directoryPathFileData).mkdirs())
            Bukkit.getLogger().info("Generated user data folder and files...");
        userFile = new File(plugin.getDataFolder(), fileName);
        if (!userFile.exists())
            plugin.saveDefaultConfig();
        YamlConfiguration.loadConfiguration(userFile);
    }

    // user "bob" joins server first time
    // user "bob" UUID = 42069
    // user "bob" IP = 0.0.0.0
    // server create userFile for "bob", uuid "42069", ip "0.0.0.0"
    // user "bob" leaves server
    // "bob" => "steve"
    // user "steve" joins server ("bob" joins server 2nd time)
    // user "steve" UUID = 42069
    // user "steve" ip 0.0.0.0
    // server opens userFile for "bob", uuid "42069", up "0.0.0.0"

    // humans see:
    // "bob" joins server 1st time
    // "bob" => "steve"
    // "steve" (formerly bob) joins server 2nd time

    // server sees:
    // "bob" joins server 1st time
    // "bob" => "steve"
    // "steve" joins server 1st time

    public void createUser(final Player player){
        if ( !(userFile.exists()) ) {
            try {
                YamlConfiguration UserConfig = YamlConfiguration.loadConfiguration(userFile);
                UserConfig.set("User.Info.PreviousName", player.getName());
                UserConfig.set("User.Info.UniqueID", player.getUniqueId().toString());
                UserConfig.set("User.Info.ipAddress", player.getAddress().getAddress().getHostAddress());
                UserConfig.save(userFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Use this to get the data file of a specific user using UUID
     * ``userConfig = YamlConfiguration.loadConfiguration(userFile);``
     * @return userConfig
     */
    public FileConfiguration getUserFile(){
        return userConfig;
    }

    /**
     * Saves the user file
     */
    public void saveUserFile() {
        try {
            getUserFile().save(userFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is the event that fires when a user joins;
     * it automatically runs createUser() on player join
     * and checks to see if that player is new or not;
     * if they are then it creates a new file for them.
     * @param e
     */
    @EventHandler
    public void joinCreateUser(PlayerJoinEvent e) {
        FlatFile user = new FlatFile(e.getPlayer().getUniqueId());
        user.createUser(e.getPlayer());
    }
}

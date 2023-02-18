package tk.zune.playerstats;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tk.airshipcraft.playerstats.PlayerManager;
import tk.airshipcraft.playerstats.data.FlatFile;
import tk.airshipcraft.playerstats.data.UserDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public final class PlayerStats extends JavaPlugin implements Listener {

    //TODO: debug mode
    boolean debugMode = true;
    private UserDatabase db;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        playerManager = new PlayerManager();

        // This part breaks it for some reason?
        if(debugMode) {
            String password = getDbPass().getString("db-pass");
            getLogger().warning("[DEBUG] DO NOT SHARE OR POST PASSWORD ANYWHERE!");
            getLogger().warning("[DEBUG] Database password = " + password);
        }

        registerEvents();
        getLogger().info("Events registered!");

        getLogger().info("Loading/creating configuration and data files....");
        createFiles();
        getLogger().info("Created files successfully!");
        connectDatabase();
        if (db.isConnected()){
            getLogger().info("Connected to database successfully!");
        }

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        db.disconnect();
        getLogger().info("Disabled!");
    }

    /**
     * Registers events
     */
    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new Playtime(), this);
        Bukkit.getPluginManager().registerEvents(new FlatFile(this), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(this), this);
    }

    /**
     * Establishes connection to the database
     */
    public void connectDatabase() {
        db = new UserDatabase(this);
        try {
            db.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private File dbPassFile;
    private FileConfiguration dbPass;

    /**
     * Creates file containing the password for the database
     */
    public void createFiles() {
        dbPassFile = new File(getDataFolder(), "dbpass.yml");

        if(!dbPassFile.exists()) {
            dbPassFile.getParentFile().mkdirs();
            saveResource("dbpass.yml", false);
        }

        dbPass = YamlConfiguration.loadConfiguration(dbPassFile);

        try {
            dbPass.load(dbPassFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the dbpass file
     * @return dbpass file
     */
    public FileConfiguration getDbPass() {
        return dbPass;
    }

    /**
     * Getter for the database
     * @return db
     */
    public UserDatabase getDb() { return db;}
    public PlayerManager getPlayerManager() { return playerManager; }

//    public void setConfigManager(ConfigManager configManager) {
//        this.configManager = configManager;
//        configManager.createfiles();
//        ConfigManager.registerConfig();
//        configManager.saveConfig();
//    }
}
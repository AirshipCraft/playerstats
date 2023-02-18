package tk.airshipcraft.playerstats;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles players by placing them in a hashmap for database access
 */
public class PlayerManager {

    private HashMap<UUID, PlayerObject> playerObjects = new HashMap<UUID, PlayerObject>();

    public PlayerObject getCustomPlayer(UUID uuid) {
        return playerObjects.get(uuid);
    }

    public void addCustomPlayer(UUID uuid, PlayerObject player) {
        playerObjects.put(uuid, player);
    }

    public void removeCustomPlayer(UUID uuid) {
        playerObjects.remove(uuid);
    }
}

package cc.co.evenprime.bukkit.nocheat.checks.inventory;

import cc.co.evenprime.bukkit.nocheat.NoCheat;
import cc.co.evenprime.bukkit.nocheat.NoCheatPlayer;
import cc.co.evenprime.bukkit.nocheat.checks.Check;
import cc.co.evenprime.bukkit.nocheat.config.ConfigurationCacheStore;
import cc.co.evenprime.bukkit.nocheat.data.DataStore;

/**
 * Abstract base class for Inventory checks, provides some convenience
 * methods for access to data and config that's relevant to this checktype
 */
public abstract class InventoryCheck extends Check {

    private static final String id = "inventory";

    public InventoryCheck(NoCheat plugin, String name) {
        super(plugin, id, name);
    }

    /**
     * Get the "InventoryData" object that belongs to the player. Will ensure
     * that such a object exists and if not, create one
     * 
     * @param player
     * @return
     */
    public static InventoryData getData(NoCheatPlayer player) {
        DataStore base = player.getDataStore();
        InventoryData data = base.get(id);
        if(data == null) {
            data = new InventoryData();
            base.set(id, data);
        }
        return data;
    }

    /**
     * Get the InventoryConfig object that belongs to the world that the player
     * currently resides in.
     * 
     * @param player
     * @return
     */
    public static InventoryConfig getConfig(NoCheatPlayer player) {
        return getConfig(player.getConfigurationStore());
    }

    public static InventoryConfig getConfig(ConfigurationCacheStore cache) {
        InventoryConfig config = cache.get(id);
        if(config == null) {
            config = new InventoryConfig(cache.getConfiguration());
            cache.set(id, config);
        }
        return config;
    }
}

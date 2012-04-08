package cc.co.evenprime.bukkit.nocheat.checks.moving;

import java.util.Locale;
import cc.co.evenprime.bukkit.nocheat.NoCheat;
import cc.co.evenprime.bukkit.nocheat.NoCheatPlayer;
import cc.co.evenprime.bukkit.nocheat.actions.ParameterName;
import cc.co.evenprime.bukkit.nocheat.checks.Check;
import cc.co.evenprime.bukkit.nocheat.config.ConfigurationCacheStore;
import cc.co.evenprime.bukkit.nocheat.data.DataStore;
import cc.co.evenprime.bukkit.nocheat.data.PreciseLocation;

/**
 * Abstract base class for Moving checks, provides some convenience
 * methods for access to data and config that's relevant to this checktype
 */
public abstract class MovingCheck extends Check {

    private static final String id = "moving";

    public MovingCheck(NoCheat plugin, String name) {
        super(plugin, id, name);
    }

    @Override
    public String getParameter(ParameterName wildcard, NoCheatPlayer player) {

        if(wildcard == ParameterName.LOCATION) {
            PreciseLocation from = getData(player).from;
            return String.format(Locale.US, "%.2f,%.2f,%.2f", from.x, from.y, from.z);
        } else if(wildcard == ParameterName.MOVEDISTANCE) {
            PreciseLocation from = getData(player).from;
            PreciseLocation to = getData(player).to;
            return String.format(Locale.US, "%.2f,%.2f,%.2f", to.x - from.x, to.y - from.y, to.z - from.z);
        } else if(wildcard == ParameterName.LOCATION_TO) {
            PreciseLocation to = getData(player).to;
            return String.format(Locale.US, "%.2f,%.2f,%.2f", to.x, to.y, to.z);
        } else
            return super.getParameter(wildcard, player);

    }

    /**
     * Get the "MovingData" object that belongs to the player. Will ensure
     * that such a object exists and if not, create one
     * 
     * @param player
     * @return
     */
    public static MovingData getData(NoCheatPlayer player) {
        DataStore base = player.getDataStore();
        MovingData data = base.get(id);
        if(data == null) {
            data = new MovingData();
            base.set(id, data);
        }
        return data;
    }

    /**
     * Get the MovingConfig object that belongs to the world that the player
     * currently resides in.
     * 
     * @param player
     * @return
     */
    public static MovingConfig getConfig(NoCheatPlayer player) {
        return getConfig(player.getConfigurationStore());
    }

    public static MovingConfig getConfig(ConfigurationCacheStore cache) {
        MovingConfig config = cache.get(id);
        if(config == null) {
            config = new MovingConfig(cache.getConfiguration());
            cache.set(id, config);
        }
        return config;
    }
}

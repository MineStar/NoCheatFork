package cc.co.evenprime.bukkit.nocheat;

import java.util.List;
import org.bukkit.event.Listener;
import cc.co.evenprime.bukkit.nocheat.config.ConfigurationCacheStore;

public interface EventManager extends Listener {

    /**
     * Used for debug output, if checks are activated for the world-specific
     * config that is given as a parameter
     * @param cc The config
     * @return A list of active/enabled checks
     */
    public List<String> getActiveChecks(ConfigurationCacheStore cc);
}

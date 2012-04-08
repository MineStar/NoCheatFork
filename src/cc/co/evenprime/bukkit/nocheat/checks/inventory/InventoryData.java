package cc.co.evenprime.bukkit.nocheat.checks.inventory;

import org.bukkit.Material;
import cc.co.evenprime.bukkit.nocheat.DataItem;

/**
 * Player specific data for the inventory checks
 * 
 */
public class InventoryData implements DataItem {

    // Keep track of the violation levels of the three checks
    public int      dropVL;
    public int      instantBowVL;
    public double   instantEatVL;

    // Time and amount of dropped items
    public long     dropLastTime;
    public int      dropCount;

    // Times when bow shootinhg and eating started
    public long     lastBowInteractTime;
    public long     lastEatInteractTime;

    // What the player is eating
    public Material foodMaterial;
}

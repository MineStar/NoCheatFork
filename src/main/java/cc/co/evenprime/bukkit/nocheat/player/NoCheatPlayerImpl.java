package cc.co.evenprime.bukkit.nocheat.player;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.MobEffectList;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import cc.co.evenprime.bukkit.nocheat.NoCheat;
import cc.co.evenprime.bukkit.nocheat.NoCheatPlayer;
import cc.co.evenprime.bukkit.nocheat.config.ConfigurationCacheStore;
import cc.co.evenprime.bukkit.nocheat.data.DataStore;
import cc.co.evenprime.bukkit.nocheat.data.ExecutionHistory;

import com.bukkit.gemo.utils.UtilPermissions;

public class NoCheatPlayerImpl implements NoCheatPlayer {

    private final MobEffectList JUMP;
    private final MobEffectList FASTER_MOVEMENT;
    
    private Player player;
    private final NoCheat plugin;
    private final DataStore data;
    private ConfigurationCacheStore config;
    private long lastUsedTime;
    private final ExecutionHistory history;

    public NoCheatPlayerImpl(Player player, NoCheat plugin) {

        this.player = player;
        this.plugin = plugin;
        this.data = new DataStore();
        this.history = new ExecutionHistory();

        this.lastUsedTime = System.currentTimeMillis();
        
        JUMP = MobEffectList.getByName("jump_boost");
        if (JUMP == null) {
            throw new RuntimeException();
        }
        FASTER_MOVEMENT = MobEffectList.getByName("speed");
        if (FASTER_MOVEMENT == null) {
            throw new RuntimeException();
}
    }

    public void refresh(Player player) {
        this.player = player;
        this.config = plugin.getConfig(player);
    }

    public boolean isDead() {
        return ((Damageable) this.player).getHealth() <= 0 || this.player.isDead();
    }

    public boolean hasPermission(String permission) {
        // if(permission == null) {
        // // System.out.println("NoCheat: Warning, asked for null permission");
        // return false;
        // }
        return UtilPermissions.playerCanUseCommand(player, permission);
    }

    public DataStore getDataStore() {
        return data;
    }

    public ConfigurationCacheStore getConfigurationStore() {
        return config;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return player.getName();
    }

    public int getTicksLived() {
        return player.getTicksLived();
    }

    public float getSpeedAmplifier() {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        if (ep.hasEffect(FASTER_MOVEMENT)) {
            // Taken directly from Minecraft code, should work
            return 1.0F + 0.2F * (float) (ep.getEffect(FASTER_MOVEMENT).getAmplifier() + 1);
        } else {
            return 1.0F;
        }
    }

    @Override
    public float getJumpAmplifier() {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        if (ep.hasEffect(JUMP)) {
            int amp = ep.getEffect(JUMP).getAmplifier();
            // Very rough estimates only
            if (amp > 20) {
                return 1.5F * (float) (ep.getEffect(JUMP).getAmplifier() + 1);
            } else {
                return 1.2F * (float) (ep.getEffect(JUMP).getAmplifier() + 1);
            }
        } else {
            return 1.0F;
        }
    }

    public boolean isSprinting() {
        return player.isSprinting();
    }

    public void setLastUsedTime(long currentTimeInMilliseconds) {
        this.lastUsedTime = currentTimeInMilliseconds;
    }

    public boolean shouldBeRemoved(long currentTimeInMilliseconds) {
        if (lastUsedTime > currentTimeInMilliseconds) {
            // Should never happen, but if it does, fix it somewhat
            lastUsedTime = currentTimeInMilliseconds;
        }
        return lastUsedTime + 60000L < currentTimeInMilliseconds;
    }

    public boolean isCreative() {
        return player.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public ExecutionHistory getExecutionHistory() {
        return history;
    }

    @Override
    public void dealFallDamage() {

    }
}

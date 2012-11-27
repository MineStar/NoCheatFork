package com.bukkit.gemo.FlyModProtection;

import java.util.HashMap;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.core.MinestarCore;

public class FMProtectionPL implements Listener {
    /************************/
    /** VARS */
    /************************/
    public HashMap<String, FMChunkArea> flyAreas;
    private HashMap<String, FMChunk> currentChunks;
    private HashMap<String, Boolean> inZone;

    private static TreeMap<String, Long> timeMap = new TreeMap<String, Long>();

    // ///////////////////////////////////
    //
    // CONSTRUCTOR
    //
    // ///////////////////////////////////
    public FMProtectionPL() {
        flyAreas = new HashMap<String, FMChunkArea>();
        currentChunks = new HashMap<String, FMChunk>();
        inZone = new HashMap<String, Boolean>();

        long time = System.currentTimeMillis();
        for (Player p : Bukkit.getOnlinePlayers()) {
            long extTime = time;
            extTime += (Math.random() * (3000 - 1000));
            timeMap.put(p.getName(), extTime);
        }
    }

    // GET PLAYER
    public static Player getPlayer(String name) {
        Player[] pList = Bukkit.getOnlinePlayers();
        for (Player player : pList) {
            if (player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }

    // ///////////////////////////////////
    //
    // ON MOVE
    //
    // ///////////////////////////////////
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        this.doZoneCheck(event.getPlayer(), event.getFrom(), event.getTo());
    }

    private void doZoneCheck(Player player, Location from, Location to) {
        if (player.isOp()) {
            Boolean forceCheck = MinestarCore.getPlayer(player.getPlayer()).getBoolean("flight.forceCheck");
            if (forceCheck == null) {
                forceCheck = false;
            }

            Boolean allowFlight = MinestarCore.getPlayer(player.getPlayer()).getBoolean("flight.allowFlight");
            if (allowFlight == null) {
                allowFlight = false;
            }

            if (player.getGameMode() != GameMode.ADVENTURE && !forceCheck) {
                player.setAllowFlight(true);
            }
            return;
        }

        // CHECK TIME
        if (System.currentTimeMillis() <= timeMap.get(player.getName())) {
            return;
        }

        // CHECK GROUP
        String groupName = UtilPermissions.getGroupName(player);
        if (groupName.equalsIgnoreCase("vip") || groupName.equalsIgnoreCase("default") || groupName.equalsIgnoreCase("probe")) {
            if (player.getAllowFlight()) {
                removePermission(player, false);
            }
            return;
        }

        // CREATE NEW CHECKTIME
        long time = System.currentTimeMillis();
        long extTime = time;
        extTime += (Math.random() * (500 - 1500));
        timeMap.put(player.getName(), extTime);

        // IN SPACEMAP = RETURN
        if (to.getWorld().getName().equalsIgnoreCase("space"))
            return;

        // GET NEW CHUNK
        Chunk chunk = to.getBlock().getChunk();
        if (!currentChunks.containsKey(player.getName())) {
            currentChunks.put(player.getName(), new FMChunk(chunk));
        }

        // SAME CHUNK = RETURN
        if (currentChunks.get(player.getName()).isInChunk(chunk)) {
            return;
        }

        // UPDATE CURRENT CHUNK
        currentChunks.put(player.getName(), new FMChunk(chunk));
        if (!flyAreas.containsKey(player.getName())) {
            // NO FLY AREA DEFINED = REMOVE PERMISSION
            if (player.getAllowFlight()) {
                removePermission(player, false);
                return;
            }
        } else {
            // AREA DEFINED = HANDLE MOVEMENT
            // NOT IN AREA = REMOVE PERMISSION
            if (!flyAreas.get(player.getName()).isInArea(chunk)) {
                if (player.getAllowFlight()) {
                    removePermission(player, true);
                    return;
                }
            } else {
                // IN AREA = ADD PERMISSION
                if (!player.getAllowFlight()) {
                    addPermission(player, true);
                    CraftPlayer cplayer = (CraftPlayer) player;
                    cplayer.getHandle().onGround = true;
                    return;
                }
            }
        }
    }

    // REMOVE PERMISSION
    public void removePermission(Player player, boolean showMSG) {
        player.setAllowFlight(false);
        player.setFlying(false);
        CraftPlayer cplayer = (CraftPlayer) player;
        cplayer.getHandle().onGround = true;
        if (showMSG) {
            String playerName = player.getName();
            if (inZone.containsKey(playerName)) {
                if (inZone.get(playerName)) {
                    player.sendMessage(ChatColor.AQUA + "[FlyZone] " + ChatColor.RED + "You have left your Flymod-Zone!");
                    inZone.put(playerName, false);
                    return;
                }
            }
            player.sendMessage(ChatColor.AQUA + "[FlyZone] " + ChatColor.RED + "You have left your Flymod-Zone!");
            inZone.put(playerName, false);
        }
    }
    // ADD PERMISSION
    public void addPermission(Player player, boolean showMSG) {
        if (player.getGameMode() != GameMode.ADVENTURE) {
            player.setAllowFlight(true);
            player.setFlying(true);
            CraftPlayer cplayer = (CraftPlayer) player;
            cplayer.getHandle().onGround = true;
            if (showMSG)
                player.sendMessage(ChatColor.AQUA + "[FlyZone] " + ChatColor.GREEN + "You have entered your Flymod-Zone!");

            inZone.put(player.getName(), true);
        }
    }
    // ///////////////////////////////////
    //
    // RESET METHODS
    //
    // ///////////////////////////////////
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.doZoneCheck(event.getPlayer(), event.getPlayer().getLocation(), event.getRespawnLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        long time = System.currentTimeMillis();
        long extTime = time;
        extTime += (Math.random() * (3000 - 1000));
        timeMap.put(event.getPlayer().getName(), extTime);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        timeMap.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.doZoneCheck(event.getPlayer(), event.getFrom(), event.getTo());
    }
}

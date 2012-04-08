package com.bukkit.gemo.FlyModProtection;

import org.bukkit.Chunk;

public class FMChunkArea {
    public boolean empty = false;
    public int chunk1_x, chunk1_z;
    public int chunk2_x, chunk2_z;
    public String worldName = "";
    public long lastSavedTime = 0;

    // CONSTRUCTOR
    public FMChunkArea() {
        empty = true;
    }

    // CONSTRUCTOR
    public FMChunkArea(Chunk c1, Chunk c2) {
        this.worldName = c1.getWorld().getName();
        this.chunk1_x = Math.min(c1.getX(), c2.getX());
        this.chunk1_z = Math.min(c1.getZ(), c2.getZ());
        this.chunk2_x = Math.max(c1.getX(), c2.getX());
        this.chunk2_z = Math.max(c1.getZ(), c2.getZ());
        lastSavedTime = System.currentTimeMillis();
    }

    // CONSTRUCTOR
    public FMChunkArea(String worldName, int c1x, int c1z, int c2x, int c2z) {
        this.worldName = worldName;
        this.chunk1_x = Math.min(c1x, c2x);
        this.chunk1_z = Math.min(c1z, c2z);
        this.chunk2_x = Math.max(c1x, c2x);
        this.chunk2_z = Math.max(c1z, c2z);
        lastSavedTime = System.currentTimeMillis();
        empty = false;
    }

   
    @Override
    public String toString() {
        return worldName + " - " + empty +  " - " + chunk1_x + " / " + chunk1_z + " -- " + chunk2_x + " / " + chunk2_z;
    }

    // IS LOCATION IN AREA?
    public boolean isInArea(Chunk chunk) {
        if (!chunk.getWorld().getName().equalsIgnoreCase(worldName))
            return false;
        if (chunk.getX() < chunk1_x)
            return false;
        if (chunk.getX() > chunk2_x)
            return false;
        if (chunk.getZ() < chunk1_z)
            return false;
        if (chunk.getZ() > chunk2_z)
            return false;
        return true;
    }

    public void updatePositions() {
        if (empty)
            return;

        int x = chunk1_x;
        int z = chunk1_z;
        int x2 = chunk2_x;
        int z2 = chunk2_z;

        this.chunk1_x = Math.min(x, x2);
        this.chunk1_z = Math.min(z, z2);
        this.chunk2_x = Math.max(x, x2);
        this.chunk2_z = Math.max(z, z2);
        lastSavedTime = System.currentTimeMillis();
    }
}

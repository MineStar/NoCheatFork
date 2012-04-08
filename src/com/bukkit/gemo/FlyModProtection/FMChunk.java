package com.bukkit.gemo.FlyModProtection;

import org.bukkit.Chunk;

public class FMChunk {
    public int chunk_x, chunk_z;
    public String worldName = "";

    // CONSTRUCTOR
    public FMChunk(Chunk c1) {
        this.worldName = c1.getWorld().getName();
        this.chunk_x = c1.getX();
        this.chunk_z = c1.getZ();
    }

    // IS LOCATION IN AREA?
    public boolean isInChunk(Chunk chunk) {
        return chunk.getX() == chunk_x && chunk.getZ() == chunk_z && chunk.getWorld().getName().equalsIgnoreCase(worldName);
    }
}

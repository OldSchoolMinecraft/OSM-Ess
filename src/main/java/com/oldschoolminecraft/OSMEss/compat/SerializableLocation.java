package com.oldschoolminecraft.OSMEss.compat;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerializableLocation
{
    public String world;
    public double x, y, z;
    public float yaw, pitch;

    public SerializableLocation(String world, double x, double y, double z, float yaw, float pitch)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location toBukkitLocation()
    {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}

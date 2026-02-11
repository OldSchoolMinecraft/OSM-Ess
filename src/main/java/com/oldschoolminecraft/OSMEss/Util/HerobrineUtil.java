package com.oldschoolminecraft.OSMEss.Util;

import org.bukkit.Location;

public class HerobrineUtil
{
    private static Location currentLocation;

    public static void updateLocation(Location location)
    {
        currentLocation = location;
    }

    public static Location getCurrentLocation()
    {
        return currentLocation;
    }
}

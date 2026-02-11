package com.oldschoolminecraft.OSMEss.Handlers;

import net.minecraft.server.Entity;
import java.lang.reflect.Field;

public final class EntityIdAllocator
{

    private static Field entityCountField;
    private static int herobrineEntityID = -1;

    static
    {
        try
        {
            entityCountField = Entity.class.getDeclaredField("entityCount");
            entityCountField.setAccessible(true);
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to access Entity.entityCount", e);
        }
    }

    public static int getHerobrineEntityID()
    {
        try
        {
            if (herobrineEntityID != -1) return herobrineEntityID;

            int id = entityCountField.getInt(null);
            entityCountField.setInt(null, id + 1);
            herobrineEntityID = id;
            return herobrineEntityID;
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to allocate entity ID", e);
        }
    }
}

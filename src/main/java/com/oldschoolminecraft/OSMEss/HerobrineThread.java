package com.oldschoolminecraft.OSMEss;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class HerobrineThread extends Thread {

    public Player player;
    public int secondsRemaining;
    private final Runnable herobrineEndCallback;
    private boolean running;

    public HerobrineThread(Player player, int durationInSeconds, Runnable herobrineEndCallback) {
        this.player = player;
        this.secondsRemaining = durationInSeconds;
        this.herobrineEndCallback = herobrineEndCallback;
    }

    public void run() {
        running = true;

        while (running) {
            if (secondsRemaining == 0) {
                Packet29DestroyEntity packet = new Packet29DestroyEntity();
                packet.a = 957192;
                ((CraftPlayer)player).getHandle().netServerHandler.sendPacket(packet);

                herobrineEndCallback.run();

                running = false;
            }

            if (secondsRemaining == 1) {
                Packet29DestroyEntity packet = new Packet29DestroyEntity();
                packet.a = 957192;
                ((CraftPlayer)player).getHandle().netServerHandler.sendPacket(packet);
            }


            else if (secondsRemaining == 3) {
                putHerobrineInFront(player);
            }

            secondsRemaining--;
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        }
    }

    public void putHerobrineInFront(Player player) {
        Vector direction = player.getLocation().getDirection();
        Vector frontVector = direction.multiply(2);
        Location locationAhead = player.getEyeLocation().add(frontVector.toLocation(player.getWorld()));
        player.getLocation().setX(locationAhead.getX());
        player.getLocation().setY(locationAhead.getBlockY());
        player.getLocation().setZ(locationAhead.getZ());


        Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn();
        packet.a = 957192; // Entity ID
        packet.b = "Herobrine"; // Name (limit 16 chars)
        packet.c = (int) locationAhead.getX() * 32; // X pos
        packet.d = (int) locationAhead.getY() * 32; // Y pos
        packet.e = (int) locationAhead.getZ() * 32; // Z pos

        double dx = (int) player.getLocation().getX() * 32 - packet.c;
        double dy = (int) player.getLocation().getY() * 32 - packet.d;
        double dz = (int) player.getLocation().getZ() * 32 - packet.e;

        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length != 0.0) {
            dx /= length;
            dy /= length;
            dz /= length;
        }

        // yaw: rotation around Y axis (left/right)
        float yaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        // pitch: up/down
        float pitch = (float) -(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * (180.0 / Math.PI));

        packet.f = (byte) yaw; // Rotation
        packet.g = (byte) pitch; // Pitch

        packet.h = 276; // Current Item (Diamond Sword)


        // Send to client
        ((CraftPlayer)player).getHandle().netServerHandler.sendPacket(packet);
        player.damage((int) 0.5);
    }
}

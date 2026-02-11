package com.oldschoolminecraft.OSMEss;

import com.oldschoolminecraft.OSMEss.Handlers.EntityIdAllocator;
import com.oldschoolminecraft.OSMEss.Util.HerobrineUtil;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet33RelEntityMoveLook;
import net.minecraft.server.Packet34EntityTeleport;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class HerobrineThread extends Thread {

    public Player player;
    private Location currentHerobrineLocation;
    public int secondsRemaining;
    private final Runnable herobrineEndCallback;
    private boolean running;

    public HerobrineThread(Player player, Location currentHerobrineLocation, int durationInSeconds, Runnable herobrineEndCallback) {
        this.player = player;
        this.secondsRemaining = durationInSeconds;
        this.herobrineEndCallback = herobrineEndCallback;
    }

    public void run() {
        running = true;

        while (running) {
            if (secondsRemaining == 0) {
                Packet29DestroyEntity packet = new Packet29DestroyEntity();
                packet.a = EntityIdAllocator.getHerobrineEntityID();
                ((CraftPlayer)player).getHandle().netServerHandler.sendPacket(packet);

                herobrineEndCallback.run();

                running = false;
            }

            if (secondsRemaining == 1) {
                Packet29DestroyEntity packet = new Packet29DestroyEntity();
                packet.a = EntityIdAllocator.getHerobrineEntityID();
                ((CraftPlayer)player).getHandle().netServerHandler.sendPacket(packet);
            }


            else if (secondsRemaining == 3) {
                putHerobrineInFront(player);
            }

            secondsRemaining--;
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        }
    }

    private Location snapToGround(Location loc) {
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        int y = world.getHighestBlockYAt(x, z);

        // Feet go one block above the solid surface
        return new Location(world,
                loc.getX(),
                y,
                loc.getZ());
    }

    public void putHerobrineInFront(Player player) {
        CraftPlayer cp = (CraftPlayer) player;

        Location playerEye = player.getEyeLocation();

        // 2 blocks in front of player
        Vector forward = playerEye.getDirection().normalize().multiply(2.0);
        Location targetLoc = playerEye.clone().add(forward.toLocation(player.getWorld()));

        // Snap to ground (feet position)
        targetLoc = snapToGround(targetLoc);

        // ---- Eye positions ----
        double fakeEyeY   = targetLoc.getY() + 1.62;
        double playerEyeY = playerEye.getY();

        // ---- Eye → eye deltas ----
        double dx = playerEye.getX() - targetLoc.getX();
        double dy = playerEyeY - fakeEyeY;
        double dz = playerEye.getZ() - targetLoc.getZ();

        // ---- Rotation ----
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) -(Math.atan2(dy, horizontal) * 180.0 / Math.PI);

        // Convert to protocol bytes
        byte yawByte   = (byte) ((yaw   * 256.0f) / 360.0f);
        byte pitchByte = (byte) ((pitch * 256.0f) / 360.0f);

        // ---- Teleport packet ----
        Packet34EntityTeleport tp = new Packet34EntityTeleport();
        tp.a = EntityIdAllocator.getHerobrineEntityID();
        tp.b = (int) Math.floor(targetLoc.getX() * 32.0);
        tp.c = (int) Math.floor(targetLoc.getY() * 32.0);
        tp.d = (int) Math.floor(targetLoc.getZ() * 32.0);
        tp.e = yawByte;
        tp.f = pitchByte;

        HerobrineUtil.updateLocation(targetLoc);

        cp.getHandle().netServerHandler.sendPacket(tp);

        // Optional damage effect
        player.damage(1);
    }
}

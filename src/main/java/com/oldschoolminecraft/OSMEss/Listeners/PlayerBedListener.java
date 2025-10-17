package com.oldschoolminecraft.OSMEss.Listeners;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerListener;

public class PlayerBedListener extends PlayerListener {

    public OSMEss plugin;

    public PlayerBedListener(OSMEss plugin) {
        this.plugin = plugin;
    }

    private int sleeping = 0;
    private final int needed = (Bukkit.getOnlinePlayers().length / 2);

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        if (this.sleeping >= needed) {
            this.sleeping++;
            Bukkit.broadcastMessage("§8" + sleeping + "/" + Bukkit.getOnlinePlayers().length + " §7players are sleeping! Time has been set to day!");
            Bukkit.getServer().getWorld("world").setTime(1000);
            resetSleeperCount();
        }
        else {
            this.sleeping++;
            Bukkit.broadcastMessage("§8" + player.getName() + " §7is now in bed! §8" + getNeeded() + " §7is now needed to turn it day!");
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        if (this.sleeping >= needed) {return;}
        else {
            this.sleeping--;
            Bukkit.broadcastMessage("§8" + player.getName() + " §7is no longer in bed! §8" + getNeeded() + " §7is now needed to turn it day!");
        }
    }

    public void resetSleeperCount() {
        this.sleeping = 0;
    }

    public int getNeeded() {
        return needed;
    }

}

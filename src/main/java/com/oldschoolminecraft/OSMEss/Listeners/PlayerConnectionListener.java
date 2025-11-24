package com.oldschoolminecraft.OSMEss.Listeners;

import com.oldschoolminecraft.OSMEss.Commands.CommandList;
import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.vanish.Invisiman;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener extends PlayerListener {

    public OSMEss plugin;
    public PlayerConnectionListener(OSMEss plugin) {
        this.plugin = plugin;
    }

//    @EventHandler
//    public void on(PlayerPreLoginEvent event) {}
//
//    @EventHandler
//    public void on(PlayerLoginEvent event) {}

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.playerDataHandler.hasData(player)) {
            plugin.playerDataHandler.createData(player);
        }
        else {
            plugin.playtimeHandler.updateLastLogin(player);
            Bukkit.getServer().getLogger().info("[OSM-Ess] Retrieved data for " + player.getName() + "! (Filename: " + player.getName().toLowerCase() + ".json)");
        }
        
        if (!plugin.isOSASEnabled()) { // Fallback option if OSAS is not installed.
            if (plugin.auctionHandler.hasHostItemsToReturn(player)) {
                plugin.auctionHandler.returnAuctionHostItems(player);
                player.sendMessage("§9Items you put for auction have been §breturned§9!");
            }

            if (plugin.auctionHandler.hasAuctionWonItemsToGive(player)) {
                plugin.auctionHandler.giveAuctionWonItems(player);
                player.sendMessage("§9Items you won from an auction have been §bgiven§9!");
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.playtimeHandler.updateTotalPlaytime(player);

        if (Invisiman.instance.isVanished(player)) { // Remove them from arraylist if they quit while vanished.
            if (CommandList.vanished.contains(player)) {
                CommandList.vanished.remove(player);
            }
        }
    }
}

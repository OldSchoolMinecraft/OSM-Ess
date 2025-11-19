package com.oldschoolminecraft.OSMEss.Listeners;

import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.osas.impl.event.PlayerAuthenticationEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

import java.util.UUID;

public class OSASPoseidonListener extends CustomEventListener {

    public OSMEss plugin;

    public OSASPoseidonListener(OSMEss plugin) {
        this.plugin = plugin;
    }

    public void onCustomEvent(Event event) {
        if (!(event instanceof PlayerAuthenticationEvent)) return;

        if (plugin.isOSASEnabled()) {
            PlayerAuthenticationEvent authEvent = (PlayerAuthenticationEvent) event;
            UUID playerUUID = authEvent.getPlayer();

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all.getUniqueId().equals(playerUUID)) {
                    Bukkit.getServer().getLogger().info("[OSM-Ess] Successful login for player " + all.getName() + "!");
                    if (plugin.auctionHandler.hasHostItemsToReturn(all)) {
                        plugin.auctionHandler.returnAuctionHostItems(all);
                        all.sendMessage("§9Items you put for auction have been §breturned§9!");
                    }

                    if (plugin.auctionHandler.hasAuctionWonItemsToGive(all)) {
                        plugin.auctionHandler.giveAuctionWonItems(all);
                        all.sendMessage("§9Items you won from an auction have been §bgiven§9!");
                    }

                    return;
                }
            }

            Bukkit.getServer().getLogger().info("[OSM-Ess] Records not found for UUID: " + playerUUID);
        }
    }
}

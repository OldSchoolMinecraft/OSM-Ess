package com.oldschoolminecraft.OSMEss.Listeners;

import com.oldschoolminecraft.OSMEss.Commands.CommandList;
import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.vanish.Invisiman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreProcessListener implements Listener {

    public OSMEss plugin;

    public CommandPreProcessListener(OSMEss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (event.getMessage().startsWith("/vanish")) {
            if (player.isOp() || player.hasPermission("invisiman.vanish")) {
                if (Invisiman.instance.isVanished(player)) {
                    if (CommandList.vanished.contains(player)) {
                        CommandList.vanished.remove(player);
                    }
                }

            }
        }
    }
}

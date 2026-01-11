package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class CommandIgnoreBC implements CommandExecutor {

    private final OSMEss plugin;
    private final JSONObject data;

    public CommandIgnoreBC(OSMEss plugin) {
        this.plugin = plugin;
        this.data = new JSONObject();
        this.plugin.getCommand("ignorebroadcast").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] strings) {

        if (cmd.getName().equalsIgnoreCase("ignorebroadcast") || cmd.getName().equalsIgnoreCase("ignorebroadcasts")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (plugin.isScheduledDeathEnabled()) {
                    if (plugin.scheduledDeath.getTimeToLive() <= 30) {
                        player.sendMessage(plugin.cmdDisabledRestart);
                        return true;
                    }
                }

                if (plugin.playerDataHandler.hasIgnoreBroadcast(player)) {
                    plugin.playerDataHandler.updateIgnoreBroadcast(player, false);
                    player.sendMessage("§f[§aOSM§f] §bYou will §anow §bsee auto broadcast messages!");
                    return true;
                }
                else {
                    plugin.playerDataHandler.updateIgnoreBroadcast(player, true);
                    player.sendMessage("§f[§aOSM§f] §bYou will §cno longer §bsee auto broadcast messages!");
                    return true;
                }
            }
            else {
                sender.sendMessage("Command can only be executed by a player!");
                return true;
            }
        }

        return true;
    }
}

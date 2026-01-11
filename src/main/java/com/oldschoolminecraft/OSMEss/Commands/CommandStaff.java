package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStaff implements CommandExecutor {

    private final OSMEss plugin;

    public CommandStaff(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("staff").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("staff")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.staff")) {
                    if (plugin.isScheduledDeathEnabled()) {
                        if (plugin.scheduledDeath.getTimeToLive() <= 30) {
                            player.sendMessage(plugin.cmdDisabledRestart);
                            return true;
                        }
                    }

                    if (args.length == 0) {
                        if (plugin.inventoryHandler.hasSavedInventory(player)) {
                            plugin.inventoryHandler.loadSavedInventory(player);
                            plugin.inventoryHandler.wipeSavedInventory(player);

                            player.sendMessage("§fStaff mode has been §4disabled§f!");
                            return true;
                        }
                        else {
                            plugin.inventoryHandler.saveInventory(player);
                            player.getInventory().clear();

                            plugin.inventoryHandler.giveStaffTools(player);
                            player.sendMessage("§fStaff mode has been §aenabled§f!");
                            return true;
                        }
                    }
                }
                else {
                    player.sendMessage(plugin.noPermission);
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

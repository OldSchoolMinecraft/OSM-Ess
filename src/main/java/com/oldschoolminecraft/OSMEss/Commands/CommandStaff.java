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

                if (player.isOp() || player.hasPermission("stafftools.staff")) {
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

                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("reloadcfg")) {
                            if (player.isOp() || player.hasPermission("stafftools.reload")) {
                                plugin.staffToolsCFG.reload();
                                player.sendMessage("§aReloaded stafftools.yml file!");
                                return true;
                            }
                            else {
                                player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage("§cUsage: /staff reload");
                            return true;
                        }
                    }
                    else {
                        player.sendMessage("§cUsage: /staff or /staff reload");
                        return true;
                    }
                }
                else {
                    player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                    return true;
                }
            }
            else {
                if (args.length != 1) {
                    sender.sendMessage("§cUsage: /staff reload");
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("reloadcfg")) {
                    plugin.staffToolsCFG.reload();
                    sender.sendMessage("§aReloaded stafftools.yml file!");
                    return true;
                }
                else {
                    sender.sendMessage("§cUsage: /staff reload");
                    return true;
                }
            }
        }
        return true;
    }
}

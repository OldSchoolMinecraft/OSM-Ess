package com.oldschoolminecraft.OSMEss.Commands;

import com.earth2me.essentials.User;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetWarp implements CommandExecutor {

    private final OSMEss plugin;

    public CommandSetWarp(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("setwarp").setExecutor(this);
    }

    public boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setwarp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("essentials.setwarp")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /setwarp <warp>");
                        return true;
                    }

                    if (isInteger(args[0])) {
                        player.sendMessage("§cError: A number alone cannot be used as it may break the pagination.");
                        return true;
                    }
                    try {
                        User user = plugin.essentials.getUser(player);
                        plugin.essentials.getWarps().setWarp(args[0], user.getLocation());
                        player.sendMessage("§7Warp " + args[0] + " set.");
                        return true;

                    } catch (Exception ex) {
                        Bukkit.getLogger().warning("Error whilst creating warp " + args[0] + " at " + player.getName() + "'s location!");
                        Bukkit.getServer().getLogger().warning(ex.getMessage());
                        return true;
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

package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandWarn implements CommandExecutor {

    private final OSMEss plugin;

    public CommandWarn(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("warn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("warn")) {

            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.warn")) {
                    if (args.length < 2) {
                        player.sendMessage("§cUsage: /warn <player> <reason>");
                        return true;
                    }

                    Player other = Bukkit.getServer().getPlayerExact(args[0].toLowerCase());

                    if (other == null) {
                        player.sendMessage(plugin.playerNotFound);
                        return true;
                    }

                    String reason = "";
                    for (int i = 1; i < args.length; i++) {
                        reason = reason + args[i] + " ";

                    }

                    plugin.addWarning(other, reason.trim());
                    Bukkit.broadcastMessage("§c" + other.getName() + " warned by " + player.getName() + " for:");
                    Bukkit.broadcastMessage("§4-> §e" + reason.replaceAll("\\s+", " ").trim());

                    Bukkit.getLogger().warning(other.getName() + " (" + other.getAddress().getAddress().getHostAddress() + ") warned by " + player.getName() + " for:");
                    Bukkit.getLogger().warning(reason.replaceAll("\\s+", " ").trim());
                    return true;
                }
                else {
                    player.sendMessage(plugin.noPermission);
                    return true;
                }
            }
            if (args.length < 2) {
                sender.sendMessage("Usage: /warn <player> <reason>");
                return true;
            }

            Player other = Bukkit.getServer().getPlayerExact(args[0]);

            if (other == null) {
                sender.sendMessage("Error: Player not found.");
                return true;
            }

            String reason = "";
            for (int i = 1; i < args.length; i++) {
                reason = reason + args[i] + " ";
            }

            plugin.addWarning(other, reason.trim());
            Bukkit.broadcastMessage("§c" + other.getName() + " warned by CONSOLE for:");
            Bukkit.broadcastMessage("§4-> §e" + reason.replaceAll("\\s+", " ").trim());

            Bukkit.getLogger().warning(other.getName() + " (" + other.getAddress().getAddress().getHostAddress() + ") warned by CONSOLE for:");
            Bukkit.getLogger().warning(reason.replaceAll("\\s+", " ").trim());
            return true;
        }

        return true;
    }
}

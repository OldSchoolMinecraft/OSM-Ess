package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChatColor implements CommandExecutor {

    private final OSMEss plugin;

    public CommandChatColor(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("chatcolor").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("chatcolor") || cmd.getName().equalsIgnoreCase("cc")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.chatcolor")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /chatcolor <color code>");
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("&0") || args[0].equalsIgnoreCase("black")) {
                        plugin.updateChatColorMessage(player, "&0");
                        player.sendMessage("§aChat color message set to §aBLACK§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&1") || args[0].equalsIgnoreCase("darkblue")) {
                        plugin.updateChatColorMessage(player, "&1");
                        player.sendMessage("§aChat color message set to §1DARK_BLUE§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&2") || args[0].equalsIgnoreCase("darkgreen")) {
                        plugin.updateChatColorMessage(player, "&2");
                        player.sendMessage("§aChat color message set to §aDARK_GREEN§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&3") || args[0].equalsIgnoreCase("darkaqua")) {
                        plugin.updateChatColorMessage(player, "&3");
                        player.sendMessage("§aChat color message set to §3DARK_AQUA§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&4") || args[0].equalsIgnoreCase("darkred")) {
                        plugin.updateChatColorMessage(player, "&4");
                        player.sendMessage("§aChat color message set to §4DARK_RED§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&5") || args[0].equalsIgnoreCase("darkpurple")) {
                        plugin.updateChatColorMessage(player, "&5");
                        player.sendMessage("§aChat color message set to §5DARK_PURPLE§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&6") || args[0].equalsIgnoreCase("gold")) {
                        plugin.updateChatColorMessage(player, "&6");
                        player.sendMessage("§aChat color message set to §6GOLD§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&7") || args[0].equalsIgnoreCase("gray")) {
                        plugin.updateChatColorMessage(player, "&7");
                        player.sendMessage("§aChat color message set to §7GRAY§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&8") || args[0].equalsIgnoreCase("darkgray")) {
                        plugin.updateChatColorMessage(player, "&8");
                        player.sendMessage("§aChat color message set to §8DARK_GRAY§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&9") || args[0].equalsIgnoreCase("blue")) {
                        plugin.updateChatColorMessage(player, "&9");
                        player.sendMessage("§aChat color message set to §9BLUE§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&a") || args[0].equalsIgnoreCase("green")) {
                        plugin.updateChatColorMessage(player, "&a");
                        player.sendMessage("§aChat color message set to GREEN!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&b") || args[0].equalsIgnoreCase("aqua")) {
                        plugin.updateChatColorMessage(player, "&b");
                        player.sendMessage("§aChat color message set to §bAQUA§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&c") || args[0].equalsIgnoreCase("red")) {
                        plugin.updateChatColorMessage(player, "&c");
                        player.sendMessage("§aChat color message set to §cRED§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&d") || args[0].equalsIgnoreCase("lightpurple") || args[0].equalsIgnoreCase("pink")) {
                        plugin.updateChatColorMessage(player, "&d");
                        player.sendMessage("§aChat color message set to §dLIGHT_PURPLE§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&e") || args[0].equalsIgnoreCase("yellow")) {
                        plugin.updateChatColorMessage(player, "&e");
                        player.sendMessage("§aChat color message set to §eYELLOW§a!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("&f") || args[0].equalsIgnoreCase("white") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("default")) {
//                        plugin.updateChatColorMessage(player, "&f");
                        if (plugin.hasChatColorMessageSet(player)) {
                            plugin.removeChatColorSetting(player);
                        }
                        player.sendMessage("§aChat color message set to §fWHITE§a!");
                        return true;
                    }
                    else {
                        player.sendMessage("§cInvalid color code inputted!");
                        player.sendMessage("§cUse /colors to see available color codes!");
                    }
                }
            }
        }

        return true;
    }
}

package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
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
                    if (args.length == 0 || args.length > 2) {
                        if (player.isOp() || player.hasPermission("osmess.chatcolor.other")) {
                            player.sendMessage("§cUsage: /chatcolor <color code> [player]");
                            return true;
                        }
                        else {
                            player.sendMessage("§cUsage: /chatcolor <color code>");
                            return true;
                        }
                    }

                    if (args.length == 1) {
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
                            player.sendMessage("§aChat color message set to §2DARK_GREEN§a!");
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
                        if (args[0].equalsIgnoreCase("rainbow") || args[0].equalsIgnoreCase("rgb")) {
                            plugin.updateChatColorMessage(player, "&rgb");
                            player.sendMessage("§aChat color message set to §cR§6A§eI§aN§9B§1O§4W§a!");
                            return true;
                        }
                        if (args[0].equalsIgnoreCase("&f") || args[0].equalsIgnoreCase("white") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("default")) {
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

                    if (args.length == 2) {
                        if (player.isOp() || player.hasPermission("osmess.chatcolor.other")) {
                            Player other = Bukkit.getPlayer(args[1]);

                            if (other == null) {
                                player.sendMessage("§cPlayer is not online!");
                                return true;
                            }

                            if (args[0].equalsIgnoreCase("&0") || args[0].equalsIgnoreCase("black")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&0");
                                    player.sendMessage("§aChat color message set to §aBLACK §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&1") || args[0].equalsIgnoreCase("darkblue")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&1");
                                    player.sendMessage("§aChat color message set to §1DARK_BLUE §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&2") || args[0].equalsIgnoreCase("darkgreen")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&2");
                                    player.sendMessage("§aChat color message set to §2DARK_GREEN §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&3") || args[0].equalsIgnoreCase("darkaqua")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&3");
                                    player.sendMessage("§aChat color message set to §3DARK_AQUA §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&4") || args[0].equalsIgnoreCase("darkred")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&4");
                                    player.sendMessage("§aChat color message set to §4DARK_RED §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&5") || args[0].equalsIgnoreCase("darkpurple")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&5");
                                    player.sendMessage("§aChat color message set to §5DARK_PURPLE §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&6") || args[0].equalsIgnoreCase("gold")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&6");
                                    player.sendMessage("§aChat color message set to §6GOLD §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&7") || args[0].equalsIgnoreCase("gray")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&7");
                                    player.sendMessage("§aChat color message set to §7GRAY §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&8") || args[0].equalsIgnoreCase("darkgray")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&8");
                                    player.sendMessage("§aChat color message set to §8DARK_GRAY §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&9") || args[0].equalsIgnoreCase("blue")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&9");
                                    player.sendMessage("§aChat color message set to §9BLUE §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&a") || args[0].equalsIgnoreCase("green")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&a");
                                    player.sendMessage("§aChat color message set to GREEN §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&b") || args[0].equalsIgnoreCase("aqua")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&b");
                                    player.sendMessage("§aChat color message set to §bAQUA §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&c") || args[0].equalsIgnoreCase("red")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&c");
                                    player.sendMessage("§aChat color message set to §cRED §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&d") || args[0].equalsIgnoreCase("lightpurple") || args[0].equalsIgnoreCase("pink")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&d");
                                    player.sendMessage("§aChat color message set to §dLIGHT_PURPLE §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&e") || args[0].equalsIgnoreCase("yellow")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&e");
                                    player.sendMessage("§aChat color message set to §eYELLOW §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("rainbow") || args[0].equalsIgnoreCase("rgb")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.updateChatColorMessage(other, "&rgb");
                                    player.sendMessage("§aChat color message set to §cR§6A§eI§aN§9B§1O§4W §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            if (args[0].equalsIgnoreCase("&f") || args[0].equalsIgnoreCase("white") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("default")) {
                                if (plugin.hasChatColorMessageSet(other)) {
                                    plugin.removeChatColorSetting(other);
                                    player.sendMessage("§aChat color message set to §fWHITE §afor " + other.getName() + "!");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§c" + other.getName() + " does not have a chat setting set!");
                                    return true;
                                }
                            }
                            else {
                                player.sendMessage("§cInvalid color code inputted!");
                                player.sendMessage("§cUse /colors to see available color codes!");
                            }
                        }
                        else {
                            player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                            return true;
                        }
                    }
                }
                else {
                    player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                    return true;
                }
            }
            else {
                if (args.length != 2) {
                    sender.sendMessage("Usage: /chatcolor <color> <player>");
                    return true;
                }

                Player other = Bukkit.getPlayer(args[1]);

                if (other == null) {
                    sender.sendMessage("Player is not online!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("&0") || args[0].equalsIgnoreCase("black")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&0");
                        sender.sendMessage("Chat color message set to BLACK for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&1") || args[0].equalsIgnoreCase("darkblue")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&1");
                        sender.sendMessage("Chat color message set to DARK_BLUE for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&2") || args[0].equalsIgnoreCase("darkgreen")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&2");
                        sender.sendMessage("Chat color message set to DARK_GREEN for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&3") || args[0].equalsIgnoreCase("darkaqua")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&3");
                        sender.sendMessage("Chat color message set to DARK_AQUA for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&4") || args[0].equalsIgnoreCase("darkred")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&4");
                        sender.sendMessage("Chat color message set to DARK_RED for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&5") || args[0].equalsIgnoreCase("darkpurple")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&5");
                        sender.sendMessage("Chat color message set to DARK_PURPLE for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&6") || args[0].equalsIgnoreCase("gold")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&6");
                        sender.sendMessage("Chat color message set to GOLD for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&7") || args[0].equalsIgnoreCase("gray")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&7");
                        sender.sendMessage("Chat color message set to GRAY for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&8") || args[0].equalsIgnoreCase("darkgray")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&8");
                        sender.sendMessage("Chat color message set to DARK_GRAY for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&9") || args[0].equalsIgnoreCase("blue")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&9");
                        sender.sendMessage("Chat color message set to BLUE for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&a") || args[0].equalsIgnoreCase("green")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&a");
                        sender.sendMessage("Chat color message set to GREEN for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&b") || args[0].equalsIgnoreCase("aqua")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&b");
                        sender.sendMessage("Chat color message set to AQUA for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&c") || args[0].equalsIgnoreCase("red")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&c");
                        sender.sendMessage("Chat color message set to RED for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&d") || args[0].equalsIgnoreCase("lightpurple") || args[0].equalsIgnoreCase("pink")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&d");
                        sender.sendMessage("Chat color message set to LIGHT_PURPLE for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&e") || args[0].equalsIgnoreCase("yellow")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&e");
                        sender.sendMessage("Chat color message set to YELLOW for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("rainbow") || args[0].equalsIgnoreCase("rgb")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.updateChatColorMessage(other, "&rgb");
                        sender.sendMessage("Chat color message set to RAINBOW " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("&f") || args[0].equalsIgnoreCase("white") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("default")) {
                    if (plugin.hasChatColorMessageSet(other)) {
                        plugin.removeChatColorSetting(other);
                        sender.sendMessage("Chat color message set to WHITE for " + other.getName() + "!");
                        return true;
                    }
                    else {
                        sender.sendMessage(other.getName() + " does not have a chat setting set!");
                        return true;
                    }
                }
                else {
                    sender.sendMessage("Invalid color code inputted!");
                    sender.sendMessage("Use /colors to see available color codes!");
                }
            }
        }

        return true;
    }
}

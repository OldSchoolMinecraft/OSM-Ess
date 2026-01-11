package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHighlightWarp implements CommandExecutor {

    private final OSMEss plugin;

    public CommandHighlightWarp(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("highlightwarp").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("highlightwarp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.highlightwarp")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /highlightwarp <warp name> <color>");
                        return true;
                    }

                    try {
                        if (plugin.essentials.getWarps().getWarp(args[0]) != null || plugin.isWarpNameHighlighted(args[0])) {
                            if (args[1].equalsIgnoreCase("&0") || args[1].equalsIgnoreCase("black")) {
                                plugin.setWarpNameHighlightColor(args[0], "&0");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §0BLACK§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&1") || args[1].equalsIgnoreCase("darkblue")) {
                                plugin.setWarpNameHighlightColor(args[0], "&1");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §1DARK BLUE§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&2") || args[1].equalsIgnoreCase("darkgreen")) {
                                plugin.setWarpNameHighlightColor(args[0], "&2");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §2DARK GREEN§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&3") || args[1].equalsIgnoreCase("darkaqua")) {
                                plugin.setWarpNameHighlightColor(args[0], "&3");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §3DARK AQUA§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&4") || args[1].equalsIgnoreCase("darkred")) {
                                plugin.setWarpNameHighlightColor(args[0], "&4");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §4DARK RED§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&5") || args[1].equalsIgnoreCase("darkpurple")) {
                                plugin.setWarpNameHighlightColor(args[0], "&5");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §5DARK PURPLE§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&6") || args[1].equalsIgnoreCase("gold")) {
                                plugin.setWarpNameHighlightColor(args[0], "&6");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §6GOLD§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&7") || args[1].equalsIgnoreCase("gray")) {
                                plugin.setWarpNameHighlightColor(args[0], "&7");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in GRAY.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&8") || args[1].equalsIgnoreCase("darkgray") || args[1].equalsIgnoreCase("off")  || args[1].equalsIgnoreCase("reset")) {
                                if (plugin.isWarpNameHighlighted(args[0])) {
                                    plugin.delWarpNameHighlighted(args[0]);
                                    player.sendMessage("§7Warp §8" + args[0] + " §7no longer highlighted.");
                                    return true;
                                }
                                else {
                                    player.sendMessage("§cError: Warp is not highlighted.");
                                    return true;
                                }
                            }

                            if (args[1].equalsIgnoreCase("&9") || args[1].equalsIgnoreCase("blue")) {
                                plugin.setWarpNameHighlightColor(args[0], "&9");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §9BLUE§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&a") || args[1].equalsIgnoreCase("green")) {
                                plugin.setWarpNameHighlightColor(args[0], "&a");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §aGREEN§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&b") || args[1].equalsIgnoreCase("aqua")) {
                                plugin.setWarpNameHighlightColor(args[0], "&b");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §bAQUA§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&c") || args[1].equalsIgnoreCase("red")) {
                                plugin.setWarpNameHighlightColor(args[0], "&c");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §cRED§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&d") || args[1].equalsIgnoreCase("lightpurple") || args[1].equalsIgnoreCase("pink")) {
                                plugin.setWarpNameHighlightColor(args[0], "&d");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §dLIGHT PURPLE§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&e") || args[1].equalsIgnoreCase("yellow")) {
                                plugin.setWarpNameHighlightColor(args[0], "&e");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §eYELLOW§7.");
                                return true;
                            }

                            if (args[1].equalsIgnoreCase("&f") || args[1].equalsIgnoreCase("white")) {
                                plugin.setWarpNameHighlightColor(args[0], "&f");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §fWHITE§7.");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("rainbow1") || args[1].equalsIgnoreCase("rgb1")) {
                                plugin.setWarpNameHighlightColor(args[0], "&rgb1");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §cR§6A§eI§aN§9B§1O§4W §c#§61§7.");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("rainbow2") || args[1].equalsIgnoreCase("rgb2")) {
                                plugin.setWarpNameHighlightColor(args[0], "&rgb2");
                                player.sendMessage("§7Warp §8" + args[0] + " §7now highlighted in §4R§cA§6I§eN§aB§2O§bW §3#§d2§7.");
                                return true;
                            }

                            else {
                                player.sendMessage("§cInvalid color code provided!");
                                player.sendMessage("§cUse /colors to see available color codes!");
                                return true;
                            }
                        }
                    } catch (Exception ex) {
                        player.sendMessage("§cError: " + ex.getMessage());
                        return true;
                    }

                    return true;
                }
                else {
                    player.sendMessage(plugin.noPermission);
                    return true;
                }
            }
            else {
                if (args.length != 2) {
                    sender.sendMessage("Usage: /highlightwarp <warp name> <color>");
                    return true;
                }

                try {
                    if (plugin.essentials.getWarps().getWarp(args[0]) != null || plugin.isWarpNameHighlighted(args[0])) {
                        if (args[1].equalsIgnoreCase("&0") || args[1].equalsIgnoreCase("black")) {
                            plugin.setWarpNameHighlightColor(args[0], "&0");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in BLACK.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&1") || args[1].equalsIgnoreCase("darkblue")) {
                            plugin.setWarpNameHighlightColor(args[0], "&1");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in DARK BLUE§7.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&2") || args[1].equalsIgnoreCase("darkgreen")) {
                            plugin.setWarpNameHighlightColor(args[0], "&2");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in DARK GREEN.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&3") || args[1].equalsIgnoreCase("darkaqua")) {
                            plugin.setWarpNameHighlightColor(args[0], "&3");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in DARK AQUA.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&4") || args[1].equalsIgnoreCase("darkred")) {
                            plugin.setWarpNameHighlightColor(args[0], "&4");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in DARK RED.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&5") || args[1].equalsIgnoreCase("darkpurple")) {
                            plugin.setWarpNameHighlightColor(args[0], "&5");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in DARK PURPLE.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&6") || args[1].equalsIgnoreCase("gold")) {
                            plugin.setWarpNameHighlightColor(args[0], "&6");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in §6GOLD§7.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&7") || args[1].equalsIgnoreCase("gray")) {
                            plugin.setWarpNameHighlightColor(args[0], "&7");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in GRAY.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&8") || args[1].equalsIgnoreCase("darkgray") || args[1].equalsIgnoreCase("off")  || args[1].equalsIgnoreCase("reset")) {
                            if (plugin.isWarpNameHighlighted(args[0])) {
                                plugin.delWarpNameHighlighted(args[0]);
                                sender.sendMessage("Warp " + args[0] + " no longer highlighted.");
                                return true;
                            }
                            else {
                                sender.sendMessage("Error: Warp is not highlighted.");
                                return true;
                            }
                        }

                        if (args[1].equalsIgnoreCase("&9") || args[1].equalsIgnoreCase("blue")) {
                            plugin.setWarpNameHighlightColor(args[0], "&9");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in BLUE.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&a") || args[1].equalsIgnoreCase("green")) {
                            plugin.setWarpNameHighlightColor(args[0], "&a");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in GREEN.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&b") || args[1].equalsIgnoreCase("aqua")) {
                            plugin.setWarpNameHighlightColor(args[0], "&b");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in AQUA.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&c") || args[1].equalsIgnoreCase("red")) {
                            plugin.setWarpNameHighlightColor(args[0], "&c");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in RED.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&d") || args[1].equalsIgnoreCase("lightpurple") || args[1].equalsIgnoreCase("pink")) {
                            plugin.setWarpNameHighlightColor(args[0], "&d");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in LIGHT PURPLE.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&e") || args[1].equalsIgnoreCase("yellow")) {
                            plugin.setWarpNameHighlightColor(args[0], "&e");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in YELLOW.");
                            return true;
                        }

                        if (args[1].equalsIgnoreCase("&f") || args[1].equalsIgnoreCase("white")) {
                            plugin.setWarpNameHighlightColor(args[0], "&f");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in WHITE.");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("rainbow1") || args[1].equalsIgnoreCase("rgb1")) {
                            plugin.setWarpNameHighlightColor(args[0], "&rgb1");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in RAINBOW #1.");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("rainbow2") || args[1].equalsIgnoreCase("rgb2")) {
                            plugin.setWarpNameHighlightColor(args[0], "&rgb2");
                            sender.sendMessage("Warp " + args[0] + " now highlighted in RAINBOW #2.");
                            return true;
                        }

                        else {
                            sender.sendMessage("Invalid color code provided!");
                            sender.sendMessage("Use /colors to see available color codes!");
                            return true;
                        }
                    }
                } catch (Exception ex) {
                    sender.sendMessage("Error: " + ex.getMessage());
                    return true;
                }

                return true;
            }
        }

        return true;
    }
}

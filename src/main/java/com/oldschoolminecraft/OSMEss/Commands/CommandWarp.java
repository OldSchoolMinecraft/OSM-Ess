package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandWarp implements CommandExecutor {

    private final OSMEss plugin;

    public CommandWarp(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("warp").setExecutor(this);
    }

    private static final List<ChatColor> colorsSet1 = Arrays.asList(
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.BLUE,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_RED
    );

    public static String applyRainbowSet1(String message) {
        String msg = ChatColor.stripColor(message);

        int colorIndex = -1;
        StringBuilder newMessage = new StringBuilder();

        for (char c : msg.toCharArray()) {
            colorIndex++;

            if (colorIndex >= colorsSet1.size())
                colorIndex = 0;

            newMessage.append(colorsSet1.get(colorIndex)).append(c);
        }

        return newMessage.toString();
    }

    private static final List<ChatColor> colorsSet2 = Arrays.asList(
            ChatColor.DARK_RED,
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.DARK_GREEN,
            ChatColor.AQUA,
            ChatColor.DARK_AQUA,
            ChatColor.LIGHT_PURPLE,
            ChatColor.DARK_PURPLE
    );

    public static String applyRainbowSet2(String message) {
        String msg = ChatColor.stripColor(message);

        int colorIndex = -1;
        StringBuilder newMessage = new StringBuilder();

        for (char c : msg.toCharArray()) {
            colorIndex++;

            if (colorIndex >= colorsSet2.size())
                colorIndex = 0;

            newMessage.append(colorsSet2.get(colorIndex)).append(c);
        }

        return newMessage.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("warp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) { // Show all warps.
                    if (!plugin.essentials.getWarps().isEmpty()) {
                        List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();


                        StringBuilder stringBuilder = new StringBuilder();
                        for (String warp : warps) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append(", ");
                            }

                            if (plugin.isWarpNameHighlighted(warp)) {
                                if (plugin.isWarpNameHighlightedInRGB1(warp)) {
                                    stringBuilder.append(applyRainbowSet1(warp) + "§7");
                                }
                                else if (plugin.isWarpNameHighlightedInRGB2(warp)) {
                                    stringBuilder.append(applyRainbowSet2(warp) + "§7");
                                }
                                else {
                                    stringBuilder.append(ChatColor.translateAlternateColorCodes('&', plugin.getWarpNameHighlightColor(warp) + warp + "§7"));
                                }
                            }
                            else {
                                stringBuilder.append("§8" + warp + "§7");
                            }
                        }

                        player.sendMessage("§7Warps: §8" + stringBuilder.toString());
                        return true;
                    }
                    else {
                        player.sendMessage(plugin.warpNotDefined);
                        return true;
                    }
                }

                if (args.length == 1) { // Teleport to an existing warp.
                    if (!plugin.essentials.getWarps().isEmpty()) {
                        try {
                            if (plugin.essentials.getWarps().getWarp(args[0]) != null) {
                                World world = plugin.essentials.getWarps().getWarp(args[0]).getWorld();
                                double x = plugin.essentials.getWarps().getWarp(args[0]).getBlockX() + 0.5;
                                double y = plugin.essentials.getWarps().getWarp(args[0]).getBlockY();
                                double z = plugin.essentials.getWarps().getWarp(args[0]).getBlockZ() + 0.5;
                                float yaw = plugin.essentials.getWarps().getWarp(args[0]).getYaw();
                                float pitch = plugin.essentials.getWarps().getWarp(args[0]).getPitch();

                                player.teleport(new Location(world, x, y, z, yaw, pitch));

                                if (plugin.isWarpNameHighlighted(args[0])) {
                                    if (plugin.isWarpNameHighlightedInRGB1(args[0])) {
                                        player.sendMessage("§7Warping to " + applyRainbowSet1(args[0]) + "§7.");
                                    }
                                    else if (plugin.isWarpNameHighlightedInRGB2(args[0])) {
                                        player.sendMessage("§7Warping to " + applyRainbowSet2(args[0]) + "§7.");
                                    }
                                    else {
                                        player.sendMessage("§7Warping to " + plugin.getWarpNameHighlightColor(args[0]) + args[0] + "§7.");
                                    }

                                    return true;
                                }
                                else {
                                    player.sendMessage("§7Warping to §8" + args[0] + "§7.");
                                    return true;
                                }
                            }
                            else {
                                player.sendMessage("§cError: That warp does not exist.");
                                return true;
                            }
                        } catch (Exception ex) {
                            player.sendMessage("§cError: " + ex.getMessage());

                            Bukkit.getLogger().warning("Error whilst warping " + player.getName() + " to " + args[0] + "!");
                            Bukkit.getServer().getLogger().warning(ex.getMessage());
                            return true;
                        }
                    }
                    else {
                        player.sendMessage(plugin.warpNotDefined);
                        return true;
                    }
                }

                if (args.length == 2) { // Teleport a player to a specific warp.
                    if (player.isOp() || player.hasPermission("essentials.warp.otherplayers")) {
                        Player other = Bukkit.getServer().getPlayer(args[1]);

                        if (other == null) {
                            player.sendMessage(plugin.playerNotFound);
                            return true;
                        }

                        if (!plugin.essentials.getWarps().isEmpty()) {
                            try {
                                if (plugin.essentials.getWarps().getWarp(args[0]) != null) {
                                    World world = plugin.essentials.getWarps().getWarp(args[0]).getWorld();
                                    double x = plugin.essentials.getWarps().getWarp(args[0]).getBlockX() + 0.5;
                                    double y = plugin.essentials.getWarps().getWarp(args[0]).getBlockY();
                                    double z = plugin.essentials.getWarps().getWarp(args[0]).getBlockZ() + 0.5;
                                    float yaw = plugin.essentials.getWarps().getWarp(args[0]).getYaw();
                                    float pitch = plugin.essentials.getWarps().getWarp(args[0]).getPitch();

                                    other.teleport(new Location(world, x, y, z, yaw, pitch));

                                    if (plugin.isWarpNameHighlighted(args[0])) {
                                        if (plugin.isWarpNameHighlightedInRGB1(args[0])) {
                                            player.sendMessage("§7Warping §8" + other.getName() + " §7to " + applyRainbowSet1(args[0]) + "§7.");
                                        }
                                        else if (plugin.isWarpNameHighlightedInRGB2(args[0])) {
                                            player.sendMessage("§7Warping §8" + other.getName() + " §7to " + applyRainbowSet2(args[0]) + "§7.");
                                        }
                                        else {
                                            player.sendMessage("§7Warping §8" + other.getName() + " §7to " + plugin.getWarpNameHighlightColor(args[0]) + args[0] + "§7.");
                                        }

                                        return true;
                                    }
                                    else {
                                        player.sendMessage("§7Warping §8" + other.getName() + " §7to §8" + args[0] + "§7.");
                                        return true;
                                    }
                                }
                                else {
                                    player.sendMessage("§cError: That warp does not exist.");
                                    return true;
                                }
                            } catch (Exception ex) {
                                player.sendMessage("§cError: " + ex.getMessage());

                                Bukkit.getLogger().warning("Error whilst warping " + other.getName() + " to " + args[0] + "!");
                                Bukkit.getServer().getLogger().warning(ex.getMessage());
                                return true;
                            }
                        }
                        else {
                            player.sendMessage(plugin.warpNotDefined);
                            return true;
                        }
                    }
                    else { // No permission; show them the warp list again.
                        if (!plugin.essentials.getWarps().isEmpty()) {
                            List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();


                            StringBuilder stringBuilder = new StringBuilder();
                            for (String warp : warps) {
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append(", ");
                                }

                                if (plugin.isWarpNameHighlighted(warp)) {
                                    if (plugin.isWarpNameHighlightedInRGB1(warp)) {
                                        stringBuilder.append(applyRainbowSet1(warp));
                                    }
                                    else if (plugin.isWarpNameHighlightedInRGB2(warp)) {
                                        stringBuilder.append(applyRainbowSet2(warp));
                                    }
                                    else {
                                        stringBuilder.append(ChatColor.translateAlternateColorCodes('&', plugin.getWarpNameHighlightColor(warp) + warp + "§7"));
                                    }
                                }
                                else {
                                    stringBuilder.append("§8" + warp + "§7");
                                }
                            }

                            player.sendMessage("§7Warps: §8" + stringBuilder.toString());
                            return true;
                        }
                        else {
                            player.sendMessage(plugin.warpNotDefined);
                            return true;
                        }
                    }
                }
                else {
                    if (!plugin.essentials.getWarps().isEmpty()) {
                        List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();


                        StringBuilder stringBuilder = new StringBuilder();
                        for (String warp : warps) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append(", ");
                            }

                            if (plugin.isWarpNameHighlighted(warp)) {
                                if (plugin.isWarpNameHighlightedInRGB1(warp)) {
                                    stringBuilder.append(applyRainbowSet1(warp));
                                }
                                else if (plugin.isWarpNameHighlightedInRGB2(warp)) {
                                    stringBuilder.append(applyRainbowSet2(warp));
                                }
                                else {
                                    stringBuilder.append(ChatColor.translateAlternateColorCodes('&', plugin.getWarpNameHighlightColor(warp) + warp + "§7"));
                                }
                            }
                            else {
                                stringBuilder.append("§8" + warp + "§7");
                            }
                        }

                        player.sendMessage("§7Warps: §8" + stringBuilder.toString());
                        return true;
                    }
                    else {
                        player.sendMessage(plugin.warpNotDefined);
                        return true;
                    }
                }
            }
            else {
                if (args.length == 0) {
                    if (!plugin.essentials.getWarps().isEmpty()) {
                        List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();


                        StringBuilder stringBuilder = new StringBuilder();
                        for (String warp : warps) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append(", ");
                            }

                            stringBuilder.append(warp);
                        }

                        sender.sendMessage("Warps: " + stringBuilder.toString());
                        return true;
                    }
                    else {
                        sender.sendMessage("Error: No warps defined.");
                        return true;
                    }
                }
                if (args.length == 2) {
                    Player other = Bukkit.getServer().getPlayer(args[1]);

                    if (other == null) {
                        sender.sendMessage("Error: Player not found.");
                        return true;
                    }

                    if (!plugin.essentials.getWarps().isEmpty()) {
                        try {
                            if (plugin.essentials.getWarps().getWarp(args[0]) != null) {
                                World world = plugin.essentials.getWarps().getWarp(args[0]).getWorld();
                                double x = plugin.essentials.getWarps().getWarp(args[0]).getBlockX() + 0.5;
                                double y = plugin.essentials.getWarps().getWarp(args[0]).getBlockY();
                                double z = plugin.essentials.getWarps().getWarp(args[0]).getBlockZ() + 0.5;
                                float yaw = plugin.essentials.getWarps().getWarp(args[0]).getYaw();
                                float pitch = plugin.essentials.getWarps().getWarp(args[0]).getPitch();

                                other.teleport(new Location(world, x, y, z, yaw, pitch));

                                sender.sendMessage("Warping " + other.getName() + " to " + args[0] + ".");
                                return true;
                            }
                            else {
                                sender.sendMessage("Error: That warp does not exist.");
                            }
                        } catch (Exception ex) {
                            Bukkit.getLogger().warning("Error whilst warping " + other.getName() + " to " + args[0] + "!");
                            Bukkit.getServer().getLogger().warning(ex.getMessage());
                            return true;
                        }
                    }
                    else {
                        sender.sendMessage(plugin.warpNotDefined);
                        return true;
                    }
                }

                else {
                    sender.sendMessage("Usage /warp or /warp <warp name> <player>");
                    return true;
                }

                return true;
            }
        }

        return true;
    }
}

package com.oldschoolminecraft.OSMEss.Commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public String buildWarpPageString(List<String> items, int page, int size) {
        StringBuilder stringBuilder = new StringBuilder();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, items.size());

        if (startIndex >= items.size() || startIndex < 0) {
            return "Error: Invalid page number provided.";
        }

        // Use a standard loop for explicit control
        for (int i = startIndex; i < endIndex; i++) {
            if (plugin.isWarpNameHighlighted(items.get(i))) {
                if (plugin.isWarpNameHighlightedInRGB1(items.get(i))) {
                    stringBuilder.append(applyRainbowSet1(items.get(i)) + "§7");
                }
                else if (plugin.isWarpNameHighlightedInRGB2(items.get(i))) {
                    stringBuilder.append(applyRainbowSet2(items.get(i)) + "§7");
                }
                else {
                    stringBuilder.append(ChatColor.translateAlternateColorCodes('&', plugin.getWarpNameHighlightColor(items.get(i)) + items.get(i) + "§7"));
                }
            }
            else {
                stringBuilder.append("§8" + items.get(i) + "§7");
            }

            if (i < endIndex - 1) {
                stringBuilder.append(", "); // Add a separator
            }
        }
        return stringBuilder.toString();
    }

    public String buildConsoleWarpPageString(List<String> items, int page, int size) { // Non colored format.
        StringBuilder stringBuilder = new StringBuilder();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, items.size());

        if (startIndex >= items.size() || startIndex < 0) {
            return "Error: Invalid page number provided.";
        }

        // Use a standard loop for explicit control
        for (int i = startIndex; i < endIndex; i++) {
            stringBuilder.append(items.get(i));

            if (i < endIndex - 1) {
                stringBuilder.append(", "); // Add a separator
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("warp")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    if (!plugin.essentials.getWarps().isEmpty()) {
                        List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();

                        int pageNumber = 1; // First page
                        int pageSize = 20; // Max size per page
                        String result = buildWarpPageString(warps, pageNumber, pageSize);

                        player.sendMessage("§7Warps §7(§3" + warps.size() + "§7) §7Page §8" + pageNumber + "§7: §8" + result);
                        return true;
                    }
                    else {
                        player.sendMessage(plugin.warpNotDefined);
                        return true;
                    }
                }
                if (args.length == 1) { // Checking if it's a number, if not, it's a warp.
                    if (!plugin.essentials.getWarps().isEmpty()) {
                        try {
                            List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();

                            int pageNumber = Integer.valueOf(args[0]); // First page
                            int pageSize = 20; // Max size per page

                            int startIndex = (pageNumber - 1) * pageSize;

                            if (startIndex >= warps.size() || startIndex < 0) {
                                player.sendMessage(plugin.invalidPageNum);
                                return true;
                            }

                            String result = buildWarpPageString(warps, pageNumber, pageSize);

                            player.sendMessage("§7Warps §7(§3" + warps.size() + "§7) §7Page §8" + pageNumber + "§7: §8" + result);
                            return true;

                        } catch (NumberFormatException numEx) { // Not a number, so probably a warp.
                            try {
                                if (plugin.essentials.getWarps().getWarp(args[0]) != null) {
                                    User user = plugin.essentials.getUser(player);
                                    Trade trade = new Trade(player.getName(), plugin.essentials);
                                    user.getTeleport().warp(args[0], trade);
                                    return true;
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
                    }
                    else {
                        player.sendMessage(plugin.warpNotDefined);
                        return true;
                    }
                }
                if (args.length == 2) { // Teleport a player to a specific warp, if they have permission.
                    if (player.isOp() || player.hasPermission("essentials.warp.otherplayers")) {
                        Player other = Bukkit.getServer().getPlayer(args[1]);

                        if (other == null) {
                            player.sendMessage(plugin.playerNotFound);
                            return true;
                        }

                        if (!plugin.essentials.getWarps().isEmpty()) {
                            try {
                                if (plugin.essentials.getWarps().getWarp(args[0]) != null) {
                                    User user = plugin.essentials.getUser(other);
                                    Trade trade = new Trade(other.getName(), plugin.essentials);
                                    user.getTeleport().warp(args[0], trade);
                                    return true;
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
                    else { // No Permission; Show them the warp list.
                        if (!plugin.essentials.getWarps().isEmpty()) {
                            List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();

                            int pageNumber = 1; // First page
                            int pageSize = 20; // Max size per page
                            String result = buildWarpPageString(warps, pageNumber, pageSize);

                            player.sendMessage("§7Warps §7(§3" + warps.size() + "§7) §7Page §8" + pageNumber + "§7: §8" + result);
                            return true;
                        }
                        else {
                            player.sendMessage(plugin.warpNotDefined);
                            return true;
                        }
                    }
                }
                else { // Args beyond scope; Show them the warp list.
                    if (!plugin.essentials.getWarps().isEmpty()) {
                        List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();

                        int pageNumber = 1; // First page
                        int pageSize = 20; // Max size per page
                        String result = buildWarpPageString(warps, pageNumber, pageSize);

                        player.sendMessage("§7Warps §7(§3" + warps.size() + "§7) §7Page §8" + pageNumber + "§7: §8" + result);
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

                        int pageNumber = 1; // First page
                        int pageSize = 20; // Max size per page
                        String result = buildConsoleWarpPageString(warps, pageNumber, pageSize);

                        sender.sendMessage("Warps (" + warps.size() + ") Page " + pageNumber + ": " + result);
                        return true;
                    }
                    else {
                        sender.sendMessage("Error: No warps defined.");
                        return true;
                    }
                }
                if (args.length == 1) {
                    if (!plugin.essentials.getWarps().isEmpty()) {
                        try {
                            List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();

                            int pageNumber = Integer.valueOf(args[0]); // First page
                            int pageSize = 20; // Max size per page

                            int startIndex = (pageNumber - 1) * pageSize;

                            if (startIndex >= warps.size() || startIndex < 0) {
                                sender.sendMessage("Error: Invalid page number provided.");
                                return true;
                            }

                            String result = buildWarpPageString(warps, pageNumber, pageSize);

                            sender.sendMessage("Warps (" + warps.size() + ") Page " + pageNumber + ": " + result);
                            return true;

                        } catch (NumberFormatException numEx) { // Not a number.
                            sender.sendMessage("Error: Invalid page number provided.");
                            return true;
                        }
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
                                User user = plugin.essentials.getUser(other);
                                Trade trade = new Trade(other.getName(), plugin.essentials);
                                user.getTeleport().warp(args[0], trade);
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
                    sender.sendMessage("Usage: /warp or /warp <warp> <player>");
                    return true;
                }

                return true;
            }
        }

        return true;
    }
}

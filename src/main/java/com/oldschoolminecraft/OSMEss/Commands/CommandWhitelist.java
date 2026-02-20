package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandWhitelist implements CommandExecutor {

    private final OSMEss plugin;

    public CommandWhitelist(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("whitelist").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("whitelist")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("bukkit.command.whitelist")) {
                    if (args.length == 0) {
                        player.sendMessage("§7Whitelist Commands:");
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.add")) {player.sendMessage("§b/whitelist §3add §8- §7Adds a player to the whitelist.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.remove")) {player.sendMessage("§b/whitelist §3remove §8- §7Removes a player from the whitelist.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.find")) {player.sendMessage("§b/whitelist §3find §8- §7Looks for a name with the given chars.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.enable")) {player.sendMessage("§b/whitelist §3on §8- §7Turns on the whitelist.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.disable")) {player.sendMessage("§b/whitelist §3off §8- §7Turns off the whitelist.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.list")) {player.sendMessage("§b/whitelist §3list §8- §7Shows a list of whitelisted players.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.reload")) {player.sendMessage("§b/whitelist §3reload §8- §7Reloads the whitelist file.");}

                        if (Bukkit.getServer().hasWhitelist()) {
                            player.sendMessage("§7Whitelist Status: §aEnabled");
                            return true;
                        }
                        else {
                            player.sendMessage("§7Whitelist Status: §cDisabled");
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("add")) {
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.add")) {
                            if (args.length != 2) {
                                player.sendMessage("§cUsage: /whitelist add <player>");
                                return true;
                            }
                            OfflinePlayer other = Bukkit.getServer().getOfflinePlayer(args[1]);

                            if (other.isWhitelisted()) {
                                player.sendMessage("§c" + other.getName() + " is already whitelisted!");
                                return true;
                            }

                            other.setWhitelisted(true);
                            player.sendMessage("§a" + other.getName() + " is now whitelisted!");
                            Bukkit.getServer().getLogger().info(player.getName() + " added " +  other.getName() + " to the whitelist!");
                            return true;
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("remove")) {
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.remove")) {
                            if (args.length != 2) {
                                player.sendMessage("§cUsage: /whitelist remove <player>");
                                return true;
                            }
                            OfflinePlayer other = Bukkit.getServer().getOfflinePlayer(args[1]);

                            if (!other.isWhitelisted()) {
                                player.sendMessage("§c" + other.getName() + " is not whitelisted!");
                                return true;
                            }

                            other.setWhitelisted(false);
                            player.sendMessage("§a" + other.getName() + " is no longer whitelisted!");
                            Bukkit.getServer().getLogger().info(player.getName() + " removed " +  other.getName() + " from the whitelist!");
                            return true;
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("find")) {
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.find")) {
                            if (args.length != 2) {
                                player.sendMessage("§cUsage: /whitelist find <name>");
                                return true;
                            }

                            Set<OfflinePlayer> offlinePlayers = Bukkit.getServer().getWhitelistedPlayers();
                            List<OfflinePlayer> filteredList = offlinePlayers.stream()
                                    .filter(offline -> offline.getName().toLowerCase().contains(args[1].toLowerCase()))
                                    .collect(Collectors.toList());

                            if (filteredList.isEmpty()) {
                                player.sendMessage("§7Found §40 §7whitelisted names containing §8'§3" + args[1] + "§8'§7.");
                                return true;
                            }
                            else {

                                if (filteredList.size() == 1) {player.sendMessage("§7Found §2" + filteredList.size() + " §7whitelisted name containing §8'§3" + args[1] + "§8'§7:");}
                                else player.sendMessage("§7Found §2" + filteredList.size() + " §7whitelisted names containing §8'§3" + args[1] + "§8'§7:");

                                for (int i = 0; i < filteredList.size(); i++) {
                                    player.sendMessage("§7- §8" + filteredList.get(i).getName());
                                }
                            }

                            return true;
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("on")) {
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.enable")) {
                            if (args.length != 1) {
                                player.sendMessage("§cUsage: /whitelist enable or /whitelist on");
                                return true;
                            }

                            if (Bukkit.getServer().hasWhitelist()) {
                                player.sendMessage("§cWhitelist is already enabled!");
                                return true;
                            }
                            else {
                                Bukkit.getServer().setWhitelist(true);
                                player.sendMessage("§fWhitelist has been §aenabled§f!");
                                Bukkit.getServer().getLogger().info(player.getName() + " enabled the whitelist!");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("disable") ||  args[0].equalsIgnoreCase("off")) {
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.disable")) {
                            if (args.length != 1) {
                                player.sendMessage("§cUsage: /whitelist disable or /whitelist off");
                                return true;
                            }


                            if (!Bukkit.getServer().hasWhitelist()) {
                                player.sendMessage("§cWhitelist is not currently enabled!");
                                return true;
                            }
                            else {
                                Bukkit.getServer().setWhitelist(false);
                                player.sendMessage("§fWhitelist has been §4disabled§f!");
                                Bukkit.getServer().getLogger().info(player.getName() + " disabled the whitelist!");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("list")) {
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.list")) {
                            if (args.length != 1) {
                                player.sendMessage("§cUsage: /whitelist list");
                                return true;
                            }
                            StringBuilder stringBuilder = new StringBuilder();

                            if (plugin.isPermissionsExEnabled()) {
                                for (OfflinePlayer all : Bukkit.getServer().getWhitelistedPlayers().stream()
                                        .sorted(Comparator.comparing(OfflinePlayer::getName))
                                        .collect(Collectors.toList())) { // Sort in alphabetical order.
                                    if (stringBuilder.length() > 0) {
                                        stringBuilder.append(", ");
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Administrator")
                                            || PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Admin")) {
                                        stringBuilder.append("§c" + all.getName()).append("§7");
                                        continue;
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Senior_Mod")
                                            || PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("SeniorMod")
                                            || PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Sr_Mod")
                                            || PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("SrMod")
                                            || PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Moderator")
                                            || PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Mod")) {
                                        stringBuilder.append("§5" + all.getName()).append("§7");
                                        continue;
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Trusted")) {
                                        stringBuilder.append("§2" + all.getName()).append("§7");
                                        continue;
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("420")) {
                                        stringBuilder.append("§a" + all.getName()).append("§7");
                                        continue;
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Hydra")) {
                                        stringBuilder.append("§3" + all.getName()).append("§7");
                                        continue;
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("VIP")) {
                                        stringBuilder.append("§d" + all.getName()).append("§7");
                                        continue;
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Explorer")) {
                                        stringBuilder.append("§e" + all.getName()).append("§7");
                                        continue;
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Contributor")) {
                                        stringBuilder.append("§b" + all.getName()).append("§7");
                                        continue;
                                    }

                                    if (PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Supporter++")
                                            || PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Supporter+")
                                            || PermissionsEx.getPermissionManager().getUser(all.getName()).inGroup("Supporter")) {
                                        stringBuilder.append("§6" + all.getName()).append("§7");
                                        continue;
                                    }

                                    stringBuilder.append("§8" + all.getName()).append("§7");

                                    List<String> playerNames = new ArrayList<>();
                                    if (all.getName() != null) {
                                        playerNames.add(all.getName());
                                    }
                                }

                                player.sendMessage("§7Whitelisted players(§3" + Bukkit.getWhitelistedPlayers().size() + "§7): §8" + stringBuilder.toString());
                                return true;
                            }
                            else { // No PermissionsEx. Default listing of whitelisted players.
                                for (OfflinePlayer all : Bukkit.getServer().getWhitelistedPlayers().stream()
                                        .sorted(Comparator.comparing(OfflinePlayer::getName))
                                        .collect(Collectors.toList())) {
                                    if (stringBuilder.length() > 0) {
                                        stringBuilder.append(", ");
                                    }

                                    if (all.isOp()) {
                                        stringBuilder.append("§c" + all.getName()).append("§7");
                                        continue;
                                    }

                                    stringBuilder.append("§8" + all.getName()).append("§7");
                                }

                                player.sendMessage("§7Whitelisted players(§3" + Bukkit.getWhitelistedPlayers().size() + "§7): §8" + stringBuilder.toString());
                                return true;
                            }

                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.reload")) {
                            if (args.length != 1) {
                                player.sendMessage("§cUsage: /whitelist reload");
                                return true;
                            }

                            Bukkit.getServer().reloadWhitelist();
                            player.sendMessage("§aWhitelist file reloaded!");
                            return true;
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    else {
                        player.sendMessage("§7Whitelist Commands:");
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.add")) {player.sendMessage("§b/whitelist §3add §8- §7Adds a player to the whitelist.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.remove")) {player.sendMessage("§b/whitelist §3remove §8- §7Removes a player from the whitelist.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.find")) {player.sendMessage("§b/whitelist §3find §8- §7Looks for a name with the given chars.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.enable")) {player.sendMessage("§b/whitelist §3on §8- §7Turns on the whitelist.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.disable")) {player.sendMessage("§b/whitelist §3off §8- §7Turns off the whitelist.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.list")) {player.sendMessage("§b/whitelist §3list §8- §7Shows a list of whitelisted players.");}
                        if (player.isOp() || player.hasPermission("bukkit.command.whitelist.reload")) {player.sendMessage("§b/whitelist §3reload §8- §7Reloads the whitelist file.");}

                        if (Bukkit.getServer().hasWhitelist()) {
                            player.sendMessage("§7Whitelist Status: §aEnabled");
                            return true;
                        }
                        else {
                            player.sendMessage("§7Whitelist Status: §cDisabled");
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
                if (args.length == 0) {
                    sender.sendMessage("Whitelist Commands:");
                    sender.sendMessage("/whitelist add - Adds a player to the whitelist.");
                    sender.sendMessage("/whitelist remove - Removes a player from the whitelist.");
                    sender.sendMessage("/whitelist find - Looks for a name with the given chars.");
                    sender.sendMessage("/whitelist on - Turns on the whitelist.");
                    sender.sendMessage("/whitelist off - Turns off the whitelist.");
                    sender.sendMessage("/whitelist list - Shows a list of whitelisted players.");
                    sender.sendMessage("/whitelist reload - Reloads the whitelist file.");

                    if (Bukkit.getServer().hasWhitelist()) {
                        sender.sendMessage("Whitelist Status: Enabled");
                        return true;
                    }
                    else {
                        sender.sendMessage("Whitelist Status: Disabled");
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length != 2) {
                        sender.sendMessage("Usage: /whitelist add <player>");
                        return true;
                    }
                    OfflinePlayer other = Bukkit.getServer().getOfflinePlayer(args[1]);

                    if (other.isWhitelisted()) {
                        sender.sendMessage(other.getName() + " is already whitelisted!");
                        return true;
                    }

                    other.setWhitelisted(true);
                    sender.sendMessage(other.getName() + " is now whitelisted!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length != 2) {
                        sender.sendMessage("Usage: /whitelist remove <player>");
                        return true;
                    }
                    OfflinePlayer other = Bukkit.getServer().getOfflinePlayer(args[1]);

                    if (!other.isWhitelisted()) {
                        sender.sendMessage(other.getName() + " is not whitelisted!");
                        return true;
                    }

                    other.setWhitelisted(false);
                    sender.sendMessage(other.getName() + " is no longer whitelisted!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("find")) {
                    if (args.length != 2) {
                        sender.sendMessage("Usage: /whitelist find <name>");
                        return true;
                    }

                    Set<OfflinePlayer> offlinePlayers = Bukkit.getServer().getWhitelistedPlayers();
                    List<OfflinePlayer> filteredList = offlinePlayers.stream()
                            .filter(offline -> offline.getName().toLowerCase().contains(args[1].toLowerCase()))
                            .collect(Collectors.toList());

                    if (filteredList.isEmpty()) {
                        sender.sendMessage("Found 0 whitelisted names containing '" + args[1] + "'.");
                        return true;
                    }
                    else {

                        if (filteredList.size() == 1) {sender.sendMessage("Found " + filteredList.size() + " whitelisted name containing '" + args[1] + "':");}
                        else sender.sendMessage("Found " + filteredList.size() + " whitelisted names containing '" + args[1] + "':");

                        for (int i = 0; i < filteredList.size(); i++) {
                            sender.sendMessage("- " + filteredList.get(i).getName());
                        }
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("on")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /whitelist enable or /whitelist on");
                        return true;
                    }

                    if (Bukkit.getServer().hasWhitelist()) {
                        sender.sendMessage("Whitelist is already enabled!");
                        return true;
                    }
                    else {
                        Bukkit.getServer().setWhitelist(true);
                        sender.sendMessage("Whitelist has been enabled!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("disable") ||  args[0].equalsIgnoreCase("off")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /whitelist disable or /whitelist off");
                        return true;
                    }

                    if (!Bukkit.getServer().hasWhitelist()) {
                        sender.sendMessage("Whitelist is not currently enabled!");
                        return true;
                    }
                    else {
                        Bukkit.getServer().setWhitelist(false);
                        sender.sendMessage("Whitelist has been disabled!");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("list")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /whitelist list");
                        return true;
                    }
                    StringBuilder stringBuilder = new StringBuilder();

                    for (OfflinePlayer all : Bukkit.getServer().getWhitelistedPlayers().stream()
                            .sorted(Comparator.comparing(OfflinePlayer::getName))
                            .collect(Collectors.toList())) { // Sort in alphabetical order.
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(", ");
                        }

                        stringBuilder.append(all.getName());
                    }

                    sender.sendMessage("Whitelisted players(" + Bukkit.getWhitelistedPlayers().size() + "): " + stringBuilder.toString());
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /whitelist reload");
                        return true;
                    }

                    Bukkit.getServer().reloadWhitelist();
                    sender.sendMessage("Whitelist file reloaded!");
                    return true;
                }

                else {
                    sender.sendMessage("Whitelist Commands:");
                    sender.sendMessage("/whitelist add - Adds a player to the whitelist.");
                    sender.sendMessage("/whitelist remove - Removes a player from the whitelist.");
                    sender.sendMessage("/whitelist find - Looks for a name with the given chars.");
                    sender.sendMessage("/whitelist on - Turns on the whitelist.");
                    sender.sendMessage("/whitelist off - Turns off the whitelist.");
                    sender.sendMessage("/whitelist list - Shows a list of whitelisted players.");
                    sender.sendMessage("/whitelist reload - Reloads the whitelist file.");

                    if (Bukkit.getServer().hasWhitelist()) {
                        sender.sendMessage("Whitelist Status: Enabled");
                        return true;
                    }
                    else {
                        sender.sendMessage("Whitelist Status: Disabled");
                        return true;
                    }
                }
            }
        }

        return true;
    }
}


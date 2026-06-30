package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.earth2me.essentials.Util.getSafeDestination;
import static com.oldschoolminecraft.OSMEss.Commands.CommandSeen.getPlayerTimeZone;
import static com.oldschoolminecraft.OSMEss.Handlers.PartyDataHandler.PARTY_DATA_DIR;

public class CommandParty implements CommandExecutor {

    private final OSMEss plugin;

    public CommandParty(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("party").setExecutor(this);
    }

    HashMap<String, String> partyPlayerInvite = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("party")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    player.sendMessage("§2-= PARTY COMMANDS =-");
                    player.sendMessage("§a/party accept §8- §7Accepts the most recent invite.");
                    player.sendMessage("§a/party chat <message> §8- §7Sends a message to online players in your party.");
                    player.sendMessage("§a/party create <name> §8- §7Creates a new party.");
                    player.sendMessage("§a/party decline §8- §7Declines the most recent invite.");
                    player.sendMessage("§a/party delete §8- §7Deletes an existing party you created.");
                    player.sendMessage("§a/party delhome §8- §7Deletes an existing home for your party.");
                    player.sendMessage("§a/party home §8- §7Teleports to your party's home, if created.");
                    player.sendMessage("§a/party info §8- §7Displays information of your current party.");
                    player.sendMessage("§a/party invite <player> §8- §7Invites a player to your party.");
                    player.sendMessage("§a/party kick <player> §8- §7Kicks a player from your party.");
                    player.sendMessage("§a/party leave §8- §7Removes you from an invited party.");
                    player.sendMessage("§a/party sethome §8- §7Sets the home for your party.");
                    player.sendMessage("§a/party tp <player> §8- §7Teleports to an online player in your party.");
                    player.sendMessage("§2-= END OF PARTY COMMANDS =-");
                    return true;
                }

                if (args[0].equalsIgnoreCase("accept")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party accept");
                        return true;
                    }

                    if (partyPlayerInvite.containsKey(player.getName().toLowerCase())) {
                        String value = partyPlayerInvite.get(player.getName().toLowerCase());
                        player.sendMessage("§5[PARTY] §dInvite for party '" + value + "' §aaccepted§d.");
                        plugin.partyDataHandler.sendPartyChatMessage(plugin.partyDataHandler.getPartyPlayerIsIn(Bukkit.getOfflinePlayer(value)), "§d" + player.getName() + " §aaccepted §dyour invite.");
                        plugin.partyDataHandler.addToParty(plugin.partyDataHandler.getPartyPlayerIsIn(Bukkit.getOfflinePlayer(value)), player);

                        partyPlayerInvite.remove(player.getName().toLowerCase());
                        return true;
                    }
                    else {
                        player.sendMessage("§5[PARTY] §cYou don't have a pending invite.");
                        return true;
                    }
                }

                else if (args[0].equalsIgnoreCase("chat")) {
                    if (args.length < 2) {
                        player.sendMessage("§cUsage: /party chat <message>");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else {
                        String message = "";
                        for (int i = 1; i < args.length; i++) {
                            message = message + args[i] + " ";
                        }

                        if (player.hasPermission("essentials.chat.color")) {message = ChatColor.translateAlternateColorCodes('&', message);}

                        plugin.partyDataHandler.sendPartyChatMessage(plugin.partyDataHandler.getPartyPlayerIsIn(player), player, message);

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            if (plugin.essentials.getUser(all).isSocialSpyEnabled()) {
                                all.sendMessage("§5[PARTY]§8:§4[STAFF]§8:§b" + player.getName() + "§8: §c" + message);
                                return true;
                            }
                        }

                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("create")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /party create <name>");
                        return true;
                    }

                    if (plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are already in a party.");
                        return true;
                    }
                    else {

                        if (plugin.partyDataHandler.getPartyName(args[1]) != null) {
                            player.sendMessage("§cParty '" + args[1] + "' already exists.");
                            return true;
                        }
                        else if (args[1].contains("-") ||
                                args[1].contains("+") ||
                                args[1].contains("*") ||
                                args[1].contains("[") ||
                                args[1].contains("]") ||
                                args[1].contains("<") ||
                                args[1].contains(">") ||
                                args[1].contains("(") ||
                                args[1].contains(")") ||
                                args[1].contains(".") ||
                                args[1].contains(",") ||
                                args[1].contains(":") ||
                                args[1].contains(";") ||
                                args[1].contains("/") ||
                                args[1].contains("|") ||
                                args[1].contains("'") ||
                                args[1].contains("?") ||
                                args[1].contains("{") ||
                                args[1].contains("}") ||
                                args[1].contains("~") ||
                                args[1].contains("`")) {
                            player.sendMessage("§5[PARTY] §cYou may not use special characters in party names.");
                            return true;
                        }
                        else {
                            plugin.partyDataHandler.createNewParty(player, args[1]);
                            player.sendMessage("§5[PARTY] §aParty '" + args[1] + "' created.");
                            return true;
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("decline") || args[0].equalsIgnoreCase("deny")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party decline");
                        return true;
                    }

                    if (partyPlayerInvite.containsKey(player.getName().toLowerCase())) {
                        String value = partyPlayerInvite.get(player.getName().toLowerCase());
                        player.sendMessage("§5[PARTY] §dInvite for party '" + value + "' §cdenied§d.");
                        plugin.partyDataHandler.sendPartyChatMessage(plugin.partyDataHandler.getPartyPlayerIsIn(Bukkit.getOfflinePlayer(value)), "§d" + player.getName() + " §cdenied §dyour invite.");

                        partyPlayerInvite.remove(player.getName().toLowerCase());
                        return true;
                    }
                    else {
                        player.sendMessage("§5[PARTY] §cYou don't have a pending invite.");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("delete")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party delete");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else if (!plugin.partyDataHandler.isPartyOwner(player, plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                        player.sendMessage("§5[PARTY] §cYou must be the party owner to do this.");
                        return true;
                    }
                    else {
                        plugin.partyDataHandler.sendPartyChatMessage(plugin.partyDataHandler.getPartyPlayerIsIn(player), "§cParty '" + plugin.partyDataHandler.getPartyPlayerIsIn(player) + "' terminated.");
                        plugin.partyDataHandler.nukeParty(player, plugin.partyDataHandler.getPartyPlayerIsIn(player));
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("delhome")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party delhome");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else if (!plugin.partyDataHandler.isPartyOwner(player, plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                        player.sendMessage("§5[PARTY] §cYou must be the party owner to do this.");
                        return true;
                    }
                    else {
                        if (plugin.partyDataHandler.isPartyHomeDeleted(plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                            player.sendMessage("§5[PARTY] §cParty currently doesn't have a home set.");
                            return true;
                        }

                        plugin.partyDataHandler.delPartyHome(plugin.partyDataHandler.getPartyPlayerIsIn(player));
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("home")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party home");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else {
                        plugin.partyDataHandler.teleportToPartyHome(plugin.partyDataHandler.getPartyPlayerIsIn(player), player);
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("info")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party info");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else {
                        StringBuilder stringBuilderMembers = new StringBuilder();
                        List<String> members = plugin.partyDataHandler.getPartyMembers(PARTY_DATA_DIR, plugin.partyDataHandler.getPartyPlayerIsIn(player));

                        player.sendMessage("§2-= PARTY §a" + plugin.partyDataHandler.getPartyPlayerIsIn(player).toUpperCase() + " §2INFO =-");
                        player.sendMessage("§bOwner: §e" + plugin.partyDataHandler.getPartyOwner(plugin.partyDataHandler.getPartyPlayerIsIn(player)));
                        if (plugin.playerDataHandler.hasTimeZoneData(player) && !getPlayerTimeZone(player).endsWith("c")) {player.sendMessage("§bDate Created: §e" + plugin.partyDataHandler.getPartyDatedCreatedByTimeZone(plugin.partyDataHandler.getPartyPlayerIsIn(player), player));}
                        else {player.sendMessage("§bDate Created: §e" + plugin.partyDataHandler.getPartyDatedCreated(plugin.partyDataHandler.getPartyPlayerIsIn(player)));}

                        for (String member : members) {
                            if (stringBuilderMembers.length() > 0) {stringBuilderMembers.append(", ");}
                            stringBuilderMembers.append("§e" + member.toLowerCase()).append("§6");
                        }

                        player.sendMessage("§bMembers§8(§d" + (members.size() + 1) + "§8)§b: §e" + stringBuilderMembers.toString());
                        player.sendMessage("§bParty Home Location: §e" + plugin.partyDataHandler.getPartyHomeLocation(plugin.partyDataHandler.getPartyPlayerIsIn(player)));
                        player.sendMessage("§2-= END OF PARTY INFO =-");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("invite")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /party invite <player>");
                        return true;
                    }

                    Player other = Bukkit.getServer().getPlayerExact(args[1]);

                    if (other == null) {
                        player.sendMessage(plugin.playerNotFound);
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else if (!plugin.partyDataHandler.isPartyOwner(player, plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                        player.sendMessage("§5[PARTY] §cYou must be the party owner to do this.");
                        return true;
                    }
                    else if (plugin.partyDataHandler.isInParty(other) || plugin.partyDataHandler.isPartyOwner(other, plugin.partyDataHandler.getPartyPlayerIsIn(other))) {
                        player.sendMessage("§5[PARTY] §cPlayer is already in a party of their own.");
                        return true;
                    }
                    else {
                        if (partyPlayerInvite.containsKey(other.getName().toLowerCase())) {
                            partyPlayerInvite.remove(other.getName().toLowerCase());
                            partyPlayerInvite.put(other.getName().toLowerCase(), plugin.partyDataHandler.getPartyPlayerIsIn(player));
                        } else {partyPlayerInvite.put(other.getName().toLowerCase(), plugin.partyDataHandler.getPartyPlayerIsIn(player));}

                        plugin.partyDataHandler.sendPartyChatMessage(plugin.partyDataHandler.getPartyPlayerIsIn(player), "§d" + player.getName() + " §2invited §d" + other.getName() + " §dto join.");
                        other.sendMessage("§5[PARTY] §d" + player.getName() + " has §2invited §dyou to join their party.");
                        other.sendMessage("§5[PARTY] §dUse §a/party accept §dto accept, or §c/party decline §dto decline.");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("kick")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage: /party kick <player>");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else if (!plugin.partyDataHandler.isPartyOwner(player, plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                        player.sendMessage("§5[PARTY] §cYou must be the party owner to do this.");
                        return true;
                    }
                    else {
                        Player other = Bukkit.getServer().getPlayerExact(args[1]);

                        if (other == null) {
                            OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[1]);

                            if (plugin.partyDataHandler.getPartyPlayerIsIn(offline).equalsIgnoreCase(plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                                plugin.partyDataHandler.sendPartyChatMessage(plugin.partyDataHandler.getPartyPlayerIsIn(player), "§d" + offline.getName() + " has been kicked.");
                                plugin.partyDataHandler.removeFromParty(plugin.partyDataHandler.getPartyPlayerIsIn(player), offline);
                                return true;
                            }
                            else {
                                player.sendMessage("§5[PARTY] §cPlayer is not in your party.");
                                return true;
                            }
                        }

                        if (plugin.partyDataHandler.getPartyPlayerIsIn(other).equalsIgnoreCase(plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                            plugin.partyDataHandler.sendPartyChatMessage(plugin.partyDataHandler.getPartyPlayerIsIn(player), "§d" + other.getName() + " has been kicked.");
                            plugin.partyDataHandler.removeFromParty(plugin.partyDataHandler.getPartyPlayerIsIn(player), other);
                            return true;
                        }
                        else {
                            player.sendMessage("§5[PARTY] §cPlayer is not in your party.");
                            return true;
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("leave")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party leave");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else if (plugin.partyDataHandler.isPartyOwner(player, plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                        player.sendMessage("§5[PARTY] §cYou're currently in party that your an owner of.");
                        player.sendMessage("§5[PARTY] §cUse §e/party delete §cto delete/nuke your party.");
                        return true;
                    }
                    else {
                        plugin.partyDataHandler.removeFromParty(plugin.partyDataHandler.getPartyPlayerIsIn(player), player);
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("sethome")) {
                    if (args.length != 1) {
                        player.sendMessage("§cUsage: /party sethome");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else if (!plugin.partyDataHandler.isPartyOwner(player, plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                        player.sendMessage("§5[PARTY] §cYou must be the party owner to do this.");
                        return true;
                    }
                    else {
                        plugin.partyDataHandler.setPartyHome(plugin.partyDataHandler.getPartyPlayerIsIn(player), player);
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
                    if (args.length != 2) {
                        player.sendMessage("§cUsage; /party tp <player>");
                        return true;
                    }

                    if (!plugin.partyDataHandler.isInParty(player)) {
                        player.sendMessage("§5[PARTY] §cYou are not in a party.");
                        return true;
                    }
                    else {
                        Player other = Bukkit.getServer().getPlayerExact(args[1]);

                        if (other == null) {
                            player.sendMessage(plugin.playerNotFound);
                            return true;
                        }

                        if (plugin.partyDataHandler.getPartyPlayerIsIn(other).equalsIgnoreCase(plugin.partyDataHandler.getPartyPlayerIsIn(player))) {
                            try {player.teleport(getSafeDestination(other.getLocation())); player.sendMessage("§5[PARTY] §dTeleported to member §b" + other.getName() + "§d.");
                            } catch (Exception ex) {Bukkit.getServer().getLogger().severe("[OSM-Ess] Error party-tping " + player.getName() + " to " + other.getName() + ": " + ex.getMessage()); ex.printStackTrace(System.err); player.sendMessage("§cError: " + ex.getMessage());}
                            return true;
                        }
                        else {
                            player.sendMessage("§5[PARTY] §cPlayer is not in your party.");
                            return true;
                        }
                    }
                }
                else {
                    player.sendMessage("§2-= PARTY COMMANDS =-");
                    player.sendMessage("§a/party accept §8- §7Accepts the most recent invite.");
                    player.sendMessage("§a/party chat <message> §8- §7Sends a message to online players in your party.");
                    player.sendMessage("§a/party create <name> §8- §7Creates a new party.");
                    player.sendMessage("§a/party decline §8- §7Declines the most recent invite.");
                    player.sendMessage("§a/party delete §8- §7Deletes an existing party you created.");
                    player.sendMessage("§a/party delhome §8- §7Deletes an existing home for your party.");
                    player.sendMessage("§a/party home §8- §7Teleports to your party's home, if created.");
                    player.sendMessage("§a/party info §8- §7Displays information of your current party.");
                    player.sendMessage("§a/party invite <player> §8- §7Invites a player to your party.");
                    player.sendMessage("§a/party kick <player> §8- §7Kicks a player from your party.");
                    player.sendMessage("§a/party leave §8- §7Removes you from an invited party.");
                    player.sendMessage("§a/party sethome §8- §7Sets the home for your party.");
                    player.sendMessage("§a/party tp <player> §8- §7Teleports to an online player in your party.");
                    player.sendMessage("§2-= END OF PARTY COMMANDS =-");
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

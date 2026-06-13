package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandOSMEss implements CommandExecutor {

    private final OSMEss plugin;

    public CommandOSMEss(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("osmess").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("osmess")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.command")) {
                    if (args.length != 1) {
                        player.sendMessage("§7OSM-Ess version §2" + plugin.getDescription().getVersion());
                        if (player.isOp()) {player.sendMessage("§7Administration Commands:");} // Show 'Administration' if they're opped.
                        else {player.sendMessage("§7Available Commands:");}

                        // Show command usages that staff (Mods, Sr. Mods) have permission to, assuming they have the 'osmess.command' permission.
                        if (player.isOp() || player.hasPermission("osmess.command.eablacklist")) {player.sendMessage("§b/osmess §3eablacklist - §7Shows a list of players blocked from /ea.");}
                        if (player.isOp() || player.hasPermission("osmess.command.endauction")) {player.sendMessage("§b/osmess §3endauction §8- §7Ends a current auction.");}
                        if (player.isOp() || player.hasPermission("osmess.command.reload")) {player.sendMessage("§b/osmess §3reload §8- §7Reloads all yml files.");}
                        if (player.isOp() || player.hasPermission("osmess.command.setconfirmbidamount")) {player.sendMessage("§b/osmess §3setconfirmbidamount §8- §7Edits /confirmbid amount minimum.");}
                        if (player.isOp() || player.hasPermission("osmess.command.setconfirmbidpercent")) {player.sendMessage("§b/osmess §3setconfirmbidpercent §8- §7Edits /confirmbid percent minimum.");}
                        if (player.isOp() || player.hasPermission("osmess.command.toggleauction")) {player.sendMessage("§b/osmess §3toggleauction §8- §7Enables/Disables the auction system.");}
                        if (player.isOp() || player.hasPermission("osmess.command.toggleconfirmbid")) {player.sendMessage("§b/osmess §3toggleconfirmbid §8- §7Enables/Disables needing /confirmbid.");}
                        if (player.isOp() || player.hasPermission("osmess.command.toggleea")) {player.sendMessage("§b/osmess §3toggleea §8- §7Enables/Disables explosive arrows.");}
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("eablacklist")) {

                        if (player.isOp() || player.hasPermission("osmess.command.eablacklist")) {
                            List<String> eaBlacklist = plugin.configSettingCFG.getStringList("ExplosiveArrows.disallowedPlayers", new ArrayList<>());

                            if (eaBlacklist.isEmpty()) {
                                player.sendMessage("§7Blacklisted Players (§40§7): §8None");
                                return true;
                            }
                            else {
                                StringBuilder stringBuilder = new StringBuilder();

                                for (String blacklisted : eaBlacklist) {
                                    if (stringBuilder.length() > 0) {
                                        stringBuilder.append(", ");
                                    }

                                    stringBuilder.append("§8" + blacklisted + "§7");
                                }

                                player.sendMessage("§7Blacklisted Players (§3" + eaBlacklist.size() + "§7): §8" + stringBuilder);
                                return true;
                            }
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("endauction")) {
                        if (player.isOp() || player.hasPermission("osmess.command.endauction")) {
                            if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.INACTIVE) {
                                player.sendMessage("§cThere is no auction to forcefully end!");
                                return true;
                            }

                            plugin.auctionHandler.forceEndAuction();
                            Bukkit.broadcastMessage("§9Auction was §cforcefully ended §9by §b" + player.getName() + "§9!");
                            Bukkit.getServer().getLogger().info("[OSM-Ess] Auction was forcefully ended by " + player.getName() + "!");
                            return true;
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("reloadcfg")) {
                        if (player.isOp() || player.hasPermission("osmess.command.reload")) {
                            plugin.autoBroadcastCFG.reload();
                            plugin.blocksReqPlaytimeCFG.reload();
                            plugin.configSettingCFG.reload();
                            plugin.colorMessageCFG.reload();
                            plugin.warningsCFG.reload();

                            player.sendMessage("§aReloaded all yml files!");
                            return true;
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("setconfirmbidamount")) {
                        if (player.isOp() || player.hasPermission("osmess.command.setconfirmbidamount")) {
                            if (args.length == 2) {
                                try {
                                    int amountSettingInputed = Integer.parseInt(args[1]);

                                    try {
                                        plugin.configSettingCFG.setProperty("Settings.Auction.confirmBidMinimum", amountSettingInputed);
                                        plugin.configSettingCFG.save();

                                        Bukkit.getServer().getLogger().severe("[OSM-Ess] confirmBidMinimum updated to $" + amountSettingInputed + "!");
                                        player.sendMessage("§aUpdated 'confirmBidMinimum' to $" + amountSettingInputed + "!");
                                    } catch (Exception e) {
                                        e.printStackTrace(System.err);

                                        player.sendMessage("§cError whilst updating config.yml!");
                                        Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating config.yml!");
                                    }

                                } catch (NumberFormatException ex) {
                                    player.sendMessage(plugin.invalidNumPara);
                                }

                                return true;
                            }
                            else {
                                player.sendMessage("§cUsage: /osmess setconfirmbidamount <amount>");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("setconfirmbidpercent")) {
                        if (player.isOp() || player.hasPermission("osmess.command.setconfirmbidpercent")) {
                            if (args.length == 2) {
                                try {
                                    int percentSettingInputed = Integer.parseInt(args[1]);

                                    try {
                                        plugin.configSettingCFG.setProperty("Settings.Auction.percentageToRequireConfirmation", percentSettingInputed);
                                        plugin.configSettingCFG.save();

                                        Bukkit.getServer().getLogger().severe("[OSM-Ess] percentageToRequireConfirmation updated to " + percentSettingInputed + "%!");
                                        player.sendMessage("§aUpdated 'percentageToRequireConfirmation' to " + percentSettingInputed + "%!");
                                    } catch (Exception e) {
                                        e.printStackTrace(System.err);

                                        player.sendMessage("§cError whilst updating config.yml!");
                                        Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating config.yml!");
                                    }

                                } catch (NumberFormatException ex) {
                                    player.sendMessage(plugin.invalidNumPara);
                                }

                                return true;
                            }
                            else {
                                player.sendMessage("§cUsage: /osmess setconfirmbidpercent <number>");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("toggleauction") || args[0].equalsIgnoreCase("toggleauctionsystem")) {
                        if (player.isOp() || player.hasPermission("osmess.command.toggleauction")) {
                            if (plugin.isAuctionSystemEnabled()) {
                                if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.ACTIVE) {
                                    player.sendMessage("§cThere is currently an active auction!");
                                    return true;
                                }
                                else {
                                    plugin.setAllowAuctionSystem(false);

                                    Bukkit.broadcastMessage("§dThe auction system has been §cdisabled §dby §c" + player.getName() + "§d!");
                                    Bukkit.getServer().getLogger().info("[OSM-Ess] The auction system has been disabled by " + player.getName() + "!");
                                    return true;
                                }
                            }
                            else {
                                plugin.setAllowAuctionSystem(true);
                                Bukkit.broadcastMessage("§dThe auction system has been §cenabled §dby §c" + player.getName() + "§d!");
                                Bukkit.getServer().getLogger().info("[OSM-Ess] The auction system has been enabled by " + player.getName() + "!");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("toggleconfirmbid")) {
                        if (player.isOp() || player.hasPermission("osmess.command.toggleconfirmbid")) {
                            if (plugin.isAuctionSystemEnabled()) {
                                if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.ACTIVE) {
                                    player.sendMessage("§cThere is currently an active auction!");
                                    return true;
                                }
                                else {
                                    if (plugin.isAuctionConfirmBidEnabled()) {
                                        plugin.setAuctionRequireConfirmBid(false);

                                        player.sendMessage("§aConfirmBid requirement disabled!");
                                        Bukkit.getServer().getLogger().info("[OSM-Ess] ConfirmBid requirement has been disabled!");
                                        return true;
                                    }
                                    else {
                                        plugin.setAuctionRequireConfirmBid(true);

                                        player.sendMessage("§aConfirmBid requirement enabled!");
                                        Bukkit.getServer().getLogger().info("[OSM-Ess] ConfirmBid requirement has been enabled!");
                                        return true;
                                    }
                                }
                            }
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("toggleea")) {
                        if (player.isOp() || player.hasPermission("osmess.command.toggleea")) {
                            if (plugin.isExplosiveArrowsEnabled()) {
                                plugin.setExplodingArrows(false);

                                if (!CommandExplosiveArrows.explodeArrow.isEmpty()) {
                                    CommandExplosiveArrows.explodeArrow.clear();
                                }

                                player.sendMessage("§fExploding Arrows are now §4disabled§7!");
                                return true;
                            }
                            else {
                                plugin.setExplodingArrows(true);

                                player.sendMessage("§fExploding Arrows are now §aenabled§7!");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage(plugin.noPermission);
                            return true;
                        }
                    }

                    else {
                        player.sendMessage("§7OSM-Ess version §2" + plugin.getDescription().getVersion());
                        if (player.isOp()) {player.sendMessage("§7Administration Commands:");} // Show 'Administration' if they're opped.
                        else {player.sendMessage("§7Available Commands:");}

                        // Show command usages that staff (Mods, Sr. Mods) have permission to, assuming they have the 'osmess.command' permission.
                        if (player.isOp() || player.hasPermission("osmess.command.eablacklist")) {player.sendMessage("§b/osmess §3eablacklist - §7Shows a list of players blocked from /ea.");}
                        if (player.isOp() || player.hasPermission("osmess.command.endauction")) {player.sendMessage("§b/osmess §3endauction §8- §7Ends a current auction.");}
                        if (player.isOp() || player.hasPermission("osmess.command.reload")) {player.sendMessage("§b/osmess §3reload §8- §7Reloads all yml files.");}
                        if (player.isOp() || player.hasPermission("osmess.command.setconfirmbidamount")) {player.sendMessage("§b/osmess §3setconfirmbidamount §8- §7Edits /confirmbid amount minimum.");}
                        if (player.isOp() || player.hasPermission("osmess.command.setconfirmbidpercent")) {player.sendMessage("§b/osmess §3setconfirmbidpercent §8- §7Edits /confirmbid percent minimum.");}
                        if (player.isOp() || player.hasPermission("osmess.command.toggleauction")) {player.sendMessage("§b/osmess §3toggleauction §8- §7Enables/Disables the auction system.");}
                        if (player.isOp() || player.hasPermission("osmess.command.toggleconfirmbid")) {player.sendMessage("§b/osmess §3toggleconfirmbid §8- §7Enables/Disables needing /confirmbid.");}
                        if (player.isOp() || player.hasPermission("osmess.command.toggleea")) {player.sendMessage("§b/osmess §3toggleea §8- §7Enables/Disables explosive arrows.");}
                        return true;
                    }
                }
                else {
                    player.sendMessage("§7OSM-Ess version " + plugin.getDescription().getVersion());
                    return true;
                }
            }
            else { // CONSOLE executed the command.
                if (args.length == 0) {
                    sender.sendMessage("OSM-Ess version " + plugin.getDescription().getVersion());
                    sender.sendMessage("Administration Commands:");
                    sender.sendMessage("/osmess addblocktoptreq - Adds a block to playtime requirement for placement.");
                    sender.sendMessage("/osmess addtoeablacklist - Adds a player to the blacklist for /ea.");
                    sender.sendMessage("/osmess delblockfromptreq - Removes a block from playtime requirement.");
                    sender.sendMessage("/osmess delfromeablacklist - Removes a player from the blacklist for /ea.");
                    sender.sendMessage("/osmess eablacklist - Shows a list of players blocked from /ea.");
                    sender.sendMessage("/osmess endauction - Ends a current auction.");
                    sender.sendMessage("/osmess reload - Reloads all yml files.");
                    sender.sendMessage("/osmess setconfirmbidamount - Edits /confirmbid amount minimum.");
                    sender.sendMessage("/osmess setconfirmbidpercent - Edits /confirmbid percent minimum.");
                    sender.sendMessage("/osmess toggleauction - Enables/Disables the auction system.");
                    sender.sendMessage("/osmess toggleconfirmbid - Enables/Disables needing /confirmbid.");
                    sender.sendMessage("/osmess toggleea - Enables/Disables exploding arrows.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("addblocktoptreq")) {
                    if (args.length != 2) {
                        sender.sendMessage("Usage: /osmess addblocktoptreq <block name>");
                        return true;
                    }

                    Material material = Material.matchMaterial(args[1]);
                    if (material == null || material == Material.AIR) {
                        sender.sendMessage("Error: Unknown block name or id.");
                        return true;
                    }

                    if (!material.isBlock()) {
                        sender.sendMessage("Error: Not a block.");
                        return true;
                    }

                    if (plugin.isBlockOnPTReq(material)) {
                        sender.sendMessage("Error Block name or id is already on the list.");
                        return true;
                    }

                    plugin.addBlocktoPTReq(material);
                    sender.sendMessage("[OSM-Ess] " + material.name().toUpperCase() + " (" + material.getId() + ") added to playime requirement.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("addtoeablacklist")) {
                    if (args.length != 2) {
                        sender.sendMessage("Usage: /osmess addtoeablacklist <player>");
                        return true;
                    }

                    Player other = Bukkit.getServer().getPlayer(args[1]);

                    if (other == null) {
                        OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[1]);

                        if (!plugin.playerDataHandler.hasData(offline)) {
                            sender.sendMessage("Error: Player never logged in before.");
                            return true;
                        }

                        if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                            sender.sendMessage("Error: Player never logged in before. (no Essentials data)");
                            return true;
                        }

                        if (plugin.isOnEABlacklist(offline)) {
                            sender.sendMessage("Error: Player is already on the explosive arrow blacklist.");
                            return true;
                        }
                        else {
                            plugin.addToEABlacklist(offline);
                            sender.sendMessage("[OSM-Ess] Player " + offline.getName() + " added to explosive arrow blacklist.");
                            return true;
                        }
                    }

                    if (plugin.isOnEABlacklist(other)) {
                        sender.sendMessage("Error: Player is already on the explosive arrow blacklist.");
                        return true;
                    }
                    else {
                        plugin.addToEABlacklist(other);
                        sender.sendMessage("[OSM-Ess] " + other.getName() + " added to explosive arrow blacklist.");
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("delblockfromptreq")) {
                    if (args.length != 2) {
                        sender.sendMessage("Usage: /osmess delblockfromptreq <block name>");
                        return true;
                    }

                    Material material = Material.matchMaterial(args[1]);
                    if (material == null || material == Material.AIR) {
                        sender.sendMessage("Error: Unknown block name or id.");
                        return true;
                    }

                    if (!plugin.isBlockOnPTReq(material)) {
                        sender.sendMessage("Error: Block name or id isn't on the list.");
                        return true;
                    }

                    plugin.delBlockFromPTReq(material);
                    sender.sendMessage("[OSM-Ess] " + material.name().toUpperCase() + " (" + material.getId() + ") removed from playime requirement.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("delfromeablacklist")) {
                    if (args.length != 2) {
                        sender.sendMessage("Usage: /osmess delfromeablacklist <player>");
                        return true;
                    }

                    Player other = Bukkit.getServer().getPlayer(args[1]);

                    if (other == null) {
                        OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[1]);

                        if (!plugin.playerDataHandler.hasData(offline)) {
                            sender.sendMessage("Error: Player never logged in before.");
                            return true;
                        }

                        if (plugin.essentials.getOfflineUser(offline.getName()) == null) {
                            sender.sendMessage("Error: Player never logged in before. (no Essentials data)");
                            return true;
                        }

                        if (!plugin.isOnEABlacklist(offline)) {
                            sender.sendMessage("Error: Player isn't on the explosive arrow blacklist.");
                            return true;
                        }
                        else {
                            plugin.delFromEABlacklist(offline);
                            sender.sendMessage("[OSM-Ess] " +offline.getName() + " removed from explosive arrow blacklist.");
                            return true;
                        }
                    }

                    if (!plugin.isOnEABlacklist(other)) {
                        sender.sendMessage("Error: Player isn't on the explosive arrow blacklist.");
                        return true;
                    }
                    else {
                        plugin.delFromEABlacklist(other);
                        sender.sendMessage("[OSM-Ess] " + other.getName() + " removed from explosive arrow blacklist.");
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("eablacklist")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /osmess eablacklist");
                        return true;
                    }

                    List<String> eaBlacklist = plugin.configSettingCFG.getStringList("ExplosiveArrows.disallowedPlayers", new ArrayList<>());

                    if (eaBlacklist.isEmpty()) {
                        Bukkit.getServer().getLogger().info("[OSM-Ess] Blacklisted Players (" + eaBlacklist.size() + "): None");
                        return true;
                    }
                    else {
                        StringBuilder stringBuilder = new StringBuilder();

                        for (String blacklisted : eaBlacklist) {
                            if (stringBuilder.length() > 0) {
                                stringBuilder.append(", ");
                            }

                            stringBuilder.append(blacklisted);
                        }

                        Bukkit.getServer().getLogger().info("[OSM-Ess] Blacklisted Players (" + eaBlacklist.size() + "): " + stringBuilder);
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("endauction")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /osmess endauction");
                        return true;
                    }

                    if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.INACTIVE) {
                        sender.sendMessage("[OSM-Ess] There is no auction to forcefully end!");
                        return true;
                    }

                    plugin.auctionHandler.forceEndAuction();
                    Bukkit.broadcastMessage("§9Auction was §cforcefully ended §9by §bCONSOLE§9!");
                    Bukkit.getServer().getLogger().info("[OSM-Ess] Auction was forcefully ended!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("reloadcfg")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /osmess reload");
                        return true;
                    }

                    plugin.autoBroadcastCFG.reload();
                    plugin.blocksReqPlaytimeCFG.reload();
                    plugin.configSettingCFG.reload();
                    plugin.colorMessageCFG.reload();
                    plugin.warningsCFG.reload();

                    sender.sendMessage("[OSM-Ess] Reloaded all yml files!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("setconfirmbidamount")) {
                    if (args.length == 2) {
                        try {
                            int amountSettingInputed = Integer.parseInt(args[1]);

                            try {
                                plugin.configSettingCFG.setProperty("Settings.Auction.confirmBidMinimum", amountSettingInputed);
                                plugin.configSettingCFG.save();

                                Bukkit.getServer().getLogger().severe("[OSM-Ess] confirmBidMinimum updated to $" + amountSettingInputed + "!");
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                                
                                Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating config.yml!");
                            }

                        } catch (NumberFormatException ex) {
                            sender.sendMessage("Error: Invalid integer provided.");
                        }

                        return true;
                    }
                    else {
                        sender.sendMessage("Usage: /osmess setconfirmbidamount <amount>");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("setconfirmbidpercent")) {
                    if (args.length == 2) {
                        try {
                            int percentSettingInputed = Integer.parseInt(args[1]);

                            try {
                                plugin.configSettingCFG.setProperty("Settings.Auction.percentageToRequireConfirmation", percentSettingInputed);
                                plugin.configSettingCFG.save();

                                Bukkit.getServer().getLogger().severe("[OSM-Ess] percentageToRequireConfirmation updated to " + percentSettingInputed + "%!");
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                                
                                Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating config.yml!");
                            }

                        } catch (NumberFormatException ex) {
                            sender.sendMessage("Error: Invalid integer provided.");
                        }

                        return true;
                    }
                    else {
                        sender.sendMessage("Usage: /osmess setconfirmbidpercent <number>");
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("toggleauction") ||  args[0].equalsIgnoreCase("toggleauctionsystem")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /osmess toggleauction");
                        return true;
                    }

                    if (plugin.isAuctionSystemEnabled()) {
                        if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.ACTIVE) {
                            sender.sendMessage("[OSM-Ess] There is currently an active auction!");
                            return true;
                        }
                        else {
                            plugin.setAllowAuctionSystem(false);

                            Bukkit.broadcastMessage("§dThe auction system has been §cdisabled §dby the §cSystem Administrator§d!");
                            Bukkit.getServer().getLogger().info("[OSM-Ess] The auction system has been disabled by the System Administrator!");
                            return true;
                        }
                    }
                    else {
                        plugin.setAllowAuctionSystem(true);
                        Bukkit.broadcastMessage("§dThe auction system has been §cenabled §dby the §cSystem Administrator§d!");
                        Bukkit.getServer().getLogger().info("[OSM-Ess] The auction system has been enabled by the System Administrator!");
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("toggleconfirmbid")) {
                    if (plugin.isAuctionSystemEnabled()) {
                        if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.ACTIVE) {
                            sender.sendMessage("[OSM-Ess] There is currently an active auction!");
                            return true;
                        }
                        else {
                            if (plugin.isAuctionConfirmBidEnabled()) {
                                plugin.setAuctionRequireConfirmBid(false);

                                Bukkit.getServer().getLogger().info("[OSM-Ess] ConfirmBid requirement has been disabled!");
                                return true;
                            }
                            else {
                                plugin.setAuctionRequireConfirmBid(true);

                                Bukkit.getServer().getLogger().info("[OSM-Ess] ConfirmBid requirement has been enabled!");
                                return true;
                            }
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("toggleea")) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /osmess toggleea");
                        return true;
                    }

                    if (plugin.isExplosiveArrowsEnabled()) {
                        plugin.setExplodingArrows(false);

                        CommandExplosiveArrows.explodeArrow.clear();

                        Bukkit.getServer().getLogger().info("[OSM-Ess] Exploding Arrows are now disabled!");
                        return true;
                    }
                    else {
                        plugin.setExplodingArrows(true);

                        Bukkit.getServer().getLogger().info("[OSM-Ess] Exploding Arrows are now enabled!");
                        return true;
                    }
                }

                sender.sendMessage("OSM-Ess version " + plugin.getDescription().getVersion());
                sender.sendMessage("Administration Commands:");
                sender.sendMessage("/osmess addblocktoptreq - Adds a block to playtime requirement for placement.");
                sender.sendMessage("/osmess addtoeablacklist - Adds a player to the blacklist for /ea.");
                sender.sendMessage("/osmess delblockfromptreq - Removes a block from playtime requirement.");
                sender.sendMessage("/osmess delfromeablacklist - Removes a player from the blacklist for /ea.");
                sender.sendMessage("/osmess eablacklist - Shows a list of players blocked from /ea.");
                sender.sendMessage("/osmess endauction - Ends a current auction.");
                sender.sendMessage("/osmess reload - Reloads all yml files.");
                sender.sendMessage("/osmess setconfirmbidamount - Edits /confirmbid amount minimum.");
                sender.sendMessage("/osmess setconfirmbidpercent - Edits /confirmbid percent minimum.");
                sender.sendMessage("/osmess toggleauction - Enables/Disables the auction system.");
                sender.sendMessage("/osmess toggleconfirmbid - Enables/Disables needing /confirmbid.");
                sender.sendMessage("/osmess toggleea - Enables/Disables exploding arrows.");
                return true;
            }
        }

        return true;
    }
}

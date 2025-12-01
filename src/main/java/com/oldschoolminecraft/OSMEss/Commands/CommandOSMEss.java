package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                        player.sendMessage("§7OSM-Ess version " + plugin.getDescription().getVersion() + "!");
                        player.sendMessage("§7Administration Commands:");
                        player.sendMessage("§b/osmess §3endauction §8- §7Ends a current auction.");
                        player.sendMessage("§b/osmess §3reload §8- §7Reloads all yml files.");
                        player.sendMessage("§b/osmess §3toggleauction §8- §7Enables/Disables the auction system.");
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("endauction")) {
                        if (player.isOp() || player.hasPermission("osmess.command.endauction")) {
                            if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.INACTIVE) {
                                player.sendMessage("§cThere is no auction to forcefully end!");
                                return true;
                            }

                            plugin.auctionHandler.endVote();
                            Bukkit.broadcastMessage("§fAuction was forcefully ended!");
                            Bukkit.getServer().getLogger().info("Auction was forcefully ended by " + player.getName() + "!");
                            return true;
                        }
                        else {
                            player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                            return true;
                        }
                    }

                    if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("reloadcfg")) {
                        if (player.isOp() || player.hasPermission("osmess.command.reload")) {
                            plugin.configSettingCFG.reload();
                            plugin.colorMessageCFG.reload();
                            plugin.warningsCFG.reload();

                            player.sendMessage("§aReloaded all yml files!");
                            return true;
                        }
                        else {
                            player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
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
                                    Bukkit.getServer().getLogger().info("The auction system has been disabled by " + player.getName() + "!");
                                    return true;
                                }
                            }
                            else {
                                plugin.setAllowAuctionSystem(true);
                                Bukkit.broadcastMessage("§dThe auction system has been §cenabled §dby §c" + player.getName() + "§d!");
                                Bukkit.getServer().getLogger().info("The auction system has been enabled by " + player.getName() + "!");
                                return true;
                            }
                        }
                        else {
                            player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                            return true;
                        }
                    }
                    else {
                        player.sendMessage("§7OSM-Ess version " + plugin.getDescription().getVersion() + "!");
                        player.sendMessage("§7Administration Commands:");
                        player.sendMessage("§b/osmess §3endauction §8- §7Ends a current auction.");
                        player.sendMessage("§b/osmess §3reload §8- §7Reloads all yml files.");
                        player.sendMessage("§b/osmess §3toggleauction §8- §7Enables/Disables the auction system.");
                        return true;
                    }
                }
                else {
                    player.sendMessage("§7OSM-Ess version " + plugin.getDescription().getVersion() + "!");
                    return true;
                }
            }
            else { // CONSOLE executed the command.
                if (args.length != 1) {
                    sender.sendMessage("OSM-Ess version " + plugin.getDescription().getVersion() + "!");
                    sender.sendMessage("Administration Commands:");
                    sender.sendMessage("/osmess endauction - Ends a current auction.");
                    sender.sendMessage("/osmess reload - Reloads all yml files.");
                    sender.sendMessage("/osmess toggleauction - Enables/Disables the auction system.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("endauction")) {
                    if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.INACTIVE) {
                        sender.sendMessage("There is no auction to forcefully end!");
                        return true;
                    }

                    plugin.auctionHandler.endVote();
                    Bukkit.broadcastMessage("Auction was forcefully ended!");
                    Bukkit.getServer().getLogger().info("Auction was forcefully ended!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("reloadcfg")) {
                    plugin.configSettingCFG.reload();
                    plugin.colorMessageCFG.reload();
                    plugin.warningsCFG.reload();

                    sender.sendMessage("Reloaded all yml files!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("toggleauction") ||  args[0].equalsIgnoreCase("toggleauctionsystem")) {
                    if (plugin.isAuctionSystemEnabled()) {
                        if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.ACTIVE) {
                            sender.sendMessage("There is currently an active auction!");
                            return true;
                        }
                        else {
                            plugin.setAllowAuctionSystem(false);

                            Bukkit.broadcastMessage("§dThe auction system has been §cdisabled §dby the §cSystem Administrator§d!");
                            Bukkit.getServer().getLogger().info("The auction system has been disabled by the System Administrator!");
                            return true;
                        }
                    }
                    else {
                        plugin.setAllowAuctionSystem(true);
                        Bukkit.broadcastMessage("§dThe auction system has been §cenabled §dby the §cSystem Administrator§d!");
                        Bukkit.getServer().getLogger().info("The auction system has been enabled by the System Administrator!");
                        return true;
                    }
                }
                sender.sendMessage("OSM-Ess version " + plugin.getDescription().getVersion() + "!");
                sender.sendMessage("Administration Commands:");
                sender.sendMessage("/osmess endauction - Ends a current auction.");
                sender.sendMessage("/osmess reload - Reloads all yml files.");
                sender.sendMessage("/osmess toggleauction - Enables/Disables the auction system.");
                return true;
            }
        }

        return true;
    }
}

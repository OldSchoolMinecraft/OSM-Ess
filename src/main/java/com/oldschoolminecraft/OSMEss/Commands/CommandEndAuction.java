package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandEndAuction implements CommandExecutor {

    private final OSMEss plugin;

    public CommandEndAuction(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("endauction").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("endauction")) {
            if (sender.isOp() || sender.hasPermission("osmess.endauction")) {
                if (plugin.auctionHandler.getAuctionStatus() == AuctionStatus.INACTIVE) {
                    sender.sendMessage("§cThere is no auction to forcefully end!");
                    return true;
                }

                plugin.auctionHandler.endVote();
                Bukkit.broadcastMessage("§fAuction was forcefully ended!");
                Bukkit.getServer().getLogger().info("Auction was forcefully ended!");
                return true;
            }
            else {
                sender.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                return true;
            }
        }

        return true;
    }
}

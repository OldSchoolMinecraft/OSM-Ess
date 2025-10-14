package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandPTT implements CommandExecutor {

    private final OSMEss plugin;

    public CommandPTT(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("ptt").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("ptt")) {
            sender.sendMessage("§7Users with the top play time:");
            java.util.List<java.util.Map.Entry<String, Integer>> topPlaytimes = plugin.playtimeHandler.getTopLongestPlayTime(10);

            if (topPlaytimes.isEmpty()) {
                sender.sendMessage("§cNo playtime data available yet.");
                return true;
            }
            else {
                int rank = 1;
                for (java.util.Map.Entry<String, Integer> entry : topPlaytimes) {
                    String playerName = entry.getKey();
                    int longestPlaytime = entry.getValue();

                    sender.sendMessage("§8" + rank + "§7. §7" + playerName + ": §8" + longestPlaytime);
                    rank++;
                }

                return true;
            }
        }

        return true;
    }
}

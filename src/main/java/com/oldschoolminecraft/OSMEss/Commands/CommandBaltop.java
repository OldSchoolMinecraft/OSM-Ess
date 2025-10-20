package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBaltop implements CommandExecutor {

    public OSMEss plugin;

    public CommandBaltop(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("baltop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("baltop")) {
            sender.sendMessage("§7Users with the top balances (refreshes hourly):");
            java.util.List<java.util.Map.Entry<String, Integer>> topBalances = getTopBalances(10);

            if (topBalances.isEmpty()) {
                sender.sendMessage("§cNo balance data available yet.");
                return true;
            }
            else {
                int rank = 1; //Ripped from LoginStreaks.
                for (java.util.Map.Entry<String, Integer> entry : topBalances) {
                    String playerName = entry.getKey();
                    int mostMoney = entry.getValue();

                    sender.sendMessage("§8" + rank + "§7. §7" + playerName + ": §8$" + mostMoney);
                    rank++;
                }

                return true;
            }
        }
        return true;
    }

    public java.util.List<java.util.Map.Entry<String, Integer>> getTopBalances(int limit) {
        // Return from cache instead of reading files
        if (plugin.cachedTopBalances.size() > limit) {
            return plugin.cachedTopBalances.subList(0, limit);
        }
        return plugin.cachedTopBalances;
    }
}

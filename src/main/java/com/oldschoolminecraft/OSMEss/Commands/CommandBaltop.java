package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBaltop implements CommandExecutor {

    public OSMEss plugin;

    public CommandBaltop(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("baltop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("baltop")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (plugin.isScheduledDeathEnabled()) {
                    if (plugin.scheduledDeath.getTimeToLive() <= 30) {
                        player.sendMessage(plugin.cmdDisabledRestart);
                        return true;
                    }
                }

                if (args.length == 0) { // Show regular balance top 10 from Essentials.
                    player.sendMessage("§7Users with the top balances (refreshes hourly):");
                    java.util.List<java.util.Map.Entry<String, Integer>> topBalances = getTopBalances(10);

                    if (topBalances.isEmpty()) {
                        player.sendMessage("§cNo balance data available yet.");
                        return true;
                    }
                    else {
                        int rank = 1; //Ripped from LoginStreaks.
                        for (java.util.Map.Entry<String, Integer> entry : topBalances) {
                            String playerName = entry.getKey();
                            int mostMoney = entry.getValue();

                            player.sendMessage("§8" + rank + "§7. §7" + playerName + ": §8$" + mostMoney);
                            rank++;
                        }

                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("businessbal") || args[0].equalsIgnoreCase("bbal")) { // Show BusinessBal top 10.
                    if (plugin.isBusinessBalEnabled()) {
                        player.sendMessage("§7BusinessBals with the top balances (refreshes hourly):");

                        java.util.List<java.util.Map.Entry<String, Double>> topBizBalances = getTopBusinessBals(10);

                        if (topBizBalances.isEmpty()) {
                            player.sendMessage("§cNo balance data available yet.");
                            return true;
                        }
                        else {
                            int rank = 1;
                            for (java.util.Map.Entry<String, Double> entry : topBizBalances) {
                                String accountName = entry.getKey();
                                double mostMoney = entry.getValue();

                                player.sendMessage("§8" + rank + "§7. §7" + accountName + ": §8$" + mostMoney);
                                rank++;
                            }

                            return true;
                        }

                    }
                    else {
                        player.sendMessage("§cBusinessBal is not installed.");
                        return true;
                    }
                }
                else { // Show regular balance top 10 from Essentials.
                    player.sendMessage("§7Users with the top balances (refreshes hourly):");
                    java.util.List<java.util.Map.Entry<String, Integer>> topBalances = getTopBalances(10);

                    if (topBalances.isEmpty()) {
                        player.sendMessage("§cNo balance data available yet.");
                        return true;
                    }
                    else {
                        int rank = 1; //Ripped from LoginStreaks.
                        for (java.util.Map.Entry<String, Integer> entry : topBalances) {
                            String playerName = entry.getKey();
                            int mostMoney = entry.getValue();

                            player.sendMessage("§8" + rank + "§7. §7" + playerName + ": §8$" + mostMoney);
                            rank++;
                        }

                        return true;
                    }
                }
            }
            else {
                if (plugin.isScheduledDeathEnabled()) {
                    if (plugin.scheduledDeath.getTimeToLive() <= 30) {
                        sender.sendMessage("Command is disabled as the server is about to restart!");
                        return true;
                    }
                }

                if (args.length == 0) { // Show regular balance top 10 from Essentials.
                    sender.sendMessage("Users with the top balances (refreshes hourly):");
                    java.util.List<java.util.Map.Entry<String, Integer>> topBalances = getTopBalances(10);

                    if (topBalances.isEmpty()) {
                        sender.sendMessage("No balance data available yet.");
                        return true;
                    }
                    else {
                        int rank = 1; //Ripped from LoginStreaks.
                        for (java.util.Map.Entry<String, Integer> entry : topBalances) {
                            String playerName = entry.getKey();
                            int mostMoney = entry.getValue();

                            sender.sendMessage(rank + ". " + playerName + ": $" + mostMoney);
                            rank++;
                        }

                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("businessbal") || args[0].equalsIgnoreCase("bbal")) { // Show BusinessBal top 10.
                    if (plugin.isBusinessBalEnabled()) {
                        sender.sendMessage("BusinessBals with the top balances (refreshes hourly):");

                        java.util.List<java.util.Map.Entry<String, Double>> topBizBalances = getTopBusinessBals(10);

                        if (topBizBalances.isEmpty()) {
                            sender.sendMessage("No balance data available yet.");
                            return true;
                        }
                        else {
                            int rank = 1;
                            for (java.util.Map.Entry<String, Double> entry : topBizBalances) {
                                String accountName = entry.getKey();
                                double mostMoney = entry.getValue();

                                sender.sendMessage(rank + ". " + accountName + ": $" + mostMoney);
                                rank++;
                            }

                            return true;
                        }
                    }
                    else {
                        sender.sendMessage("BusinessBal is not installed.");
                        return true;
                    }
                }
                else { // Show regular balance top 10 from Essentials.
                    sender.sendMessage("Users with the top balances (refreshes hourly):");
                    java.util.List<java.util.Map.Entry<String, Integer>> topBalances = getTopBalances(10);

                    if (topBalances.isEmpty()) {
                        sender.sendMessage("No balance data available yet.");
                        return true;
                    }
                    else {
                        int rank = 1; //Ripped from LoginStreaks.
                        for (java.util.Map.Entry<String, Integer> entry : topBalances) {
                            String playerName = entry.getKey();
                            int mostMoney = entry.getValue();

                            sender.sendMessage(rank + ". " + playerName + ": $" + mostMoney);
                            rank++;
                        }

                        return true;
                    }
                }
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
    public java.util.List<java.util.Map.Entry<String, Double>> getTopBusinessBals(int limit) {
        // Return from cache instead of reading files
        if (plugin.cachedTopBusinessBals.size() > limit) {
            return plugin.cachedTopBusinessBals.subList(0, limit);
        }
        return plugin.cachedTopBusinessBals;
    }
}

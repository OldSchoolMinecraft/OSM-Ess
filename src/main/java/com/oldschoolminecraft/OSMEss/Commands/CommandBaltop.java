package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBaltop implements CommandExecutor {

    private final OSMEss plugin;

    public CommandBaltop(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("baltop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("baltop")) {
            sender.sendMessage("§7Users with the top balances:");
            java.util.List<java.util.Map.Entry<String, Integer>> topBalances = getBalanceTop(10);

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

    public java.util.List<java.util.Map.Entry<String, Integer>> getBalanceTop(int limit) { // Ripped from LoginStreaks.
        java.util.List<java.util.Map.Entry<String, Integer>> topBalances = new java.util.ArrayList<>();

        // Get all player data files
        java.io.File essentialsPlayerDataDir = new java.io.File(plugin.essentials.getDataFolder().getAbsolutePath(), "userdata");
        if (!essentialsPlayerDataDir.exists()) {
            return topBalances;
        }

        java.io.File[] playerFiles = essentialsPlayerDataDir.listFiles();
        if (playerFiles == null) {
            return topBalances;
        }

        // Read each player's longest streak
        for (java.io.File playerFile : playerFiles) {
            if (playerFile.getName().endsWith(".yml")) {
                String playerName = playerFile.getName().substring(0, playerFile.getName().length() - 4);
                int mostMoney = (int) plugin.essentials.getUser(playerName).getMoney(); //Place holder to stop errors, change when methods are in place.
                if (mostMoney > 0) {
                    topBalances.add(new java.util.AbstractMap.SimpleEntry<>(playerName, mostMoney));
                }
            }
        }

        // Sort by longest streak descending
        java.util.Collections.sort(topBalances, new java.util.Comparator<java.util.Map.Entry<String, Integer>>() {
            public int compare(java.util.Map.Entry<String, Integer> a, java.util.Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        // Return top N results
        if (topBalances.size() > limit) {
            return topBalances.subList(0, limit);
        }
        return topBalances;
    }
}

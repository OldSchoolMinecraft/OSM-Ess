package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.HashMap;

public class CommandPing implements CommandExecutor {

    public OSMEss plugin;
    private final HashMap<String, Long> pingCooldown = new HashMap<>(); // 20 second cooldown to prevent spam.

    public CommandPing(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("ping").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("ping") || cmd.getName().equalsIgnoreCase("ms")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0 || args.length != 1) {
                    if (!pingCooldown.containsKey(player.getName()) || System.currentTimeMillis()/1000 - pingCooldown.get(player.getName())/1000 > 25) {
                        pingCooldown.put(player.getName(), System.currentTimeMillis());
                        player.sendMessage("§7Your ping is §8" + getPing(player) + " §7ms!");
                        return true;
                    }
                    else {
                        long cooldownSeconds = System.currentTimeMillis()/1000 - pingCooldown.get(player.getName())/1000;
                        if (cooldownSeconds == 24) {
                            player.sendMessage("§cPlease wait 1 second before running /ping again!");
                            return true;
                        }
                        player.sendMessage("§cPlease wait " + (25 - cooldownSeconds) + " seconds before running /ping again!");
                        return true;
                    }
                }
                Player other = Bukkit.getServer().getPlayer(args[0]);

                if (other == null) {
                    player.sendMessage("§cPlayer is not online!");
                    return true;
                }
                if (!pingCooldown.containsKey(player.getName()) || System.currentTimeMillis()/1000 - pingCooldown.get(player.getName())/1000 > 25) {
                    pingCooldown.put(player.getName(), System.currentTimeMillis());
                    player.sendMessage("§8" + other.getName() + "§7's ping is §8" + getPing(other) + " §7ms!");
                    return true;
                }
                else {
                    long cooldownSeconds = System.currentTimeMillis()/1000 - pingCooldown.get(player.getName())/1000;
                    if (cooldownSeconds == 24) {
                        player.sendMessage("§cPlease wait 1 second before running /ping again!");
                        return true;
                    }
                    player.sendMessage("§cPlease wait " + (25 - cooldownSeconds) + " seconds before running /ping again!");
                    return true;
                }
            }
            else {
                if (args.length != 1) {
                    sender.sendMessage("§cUsage: /ping <player>");
                    return true;
                }
                Player other = Bukkit.getServer().getPlayer(args[0]);

                if (other == null) {
                    sender.sendMessage("§cPlayer is not online!");
                    return true;
                }

                sender.sendMessage("§8" + other.getName() + "§7's ping is §8" + getPing(other) + " §7ms!");
                return true;
            }
        }
        return true;
    }

    public int getPing(Player player) { //
        String address = player.getAddress().getAddress().getHostAddress(); // Will ping to a specified player's ip from the server address.
        return Math.toIntExact(reachAddressTime(address, 1000));
    }

    public long reachAddressTime(String ip, int time) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            long finishTime;
            long startTime = System.currentTimeMillis();

            if (address.isReachable(time)) {
                finishTime = System.currentTimeMillis();
                return (finishTime - startTime);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}

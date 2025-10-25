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
    private final HashMap<String, Long> pingCooldown = new HashMap<>();

    public CommandPing(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("ping").setExecutor(this);
    }
  
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("ping") || cmd.getName().equalsIgnoreCase("ms")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!pingCooldown.containsKey(player.getName()) || System.currentTimeMillis()/1000 - pingCooldown.get(player.getName())/1000 > 20) {
                    pingCooldown.put(player.getName(), System.currentTimeMillis());
                    player.sendMessage("§7Your ping is §8" + getPing() + " §7ms!");
                    return true;
                }
                else {
                    long cooldownSeconds = System.currentTimeMillis()/1000 - pingCooldown.get(player.getName())/1000;
                    if (cooldownSeconds == 19) {
                        player.sendMessage("§cPlease wait 1 second before running /ping again!");
                        return true;
                    }
                    player.sendMessage("§cPlease wait " + (20 - cooldownSeconds) + " seconds before running /ping again!");
                    return true;
                }

            }
            else {
                sender.sendMessage("§cCommand can only be executed by a player!");
                return true;
            }
        }
        return true;
    }

    public int getPing() { //
//        String address = "os-mc.net"; // Always will ping to OSM when a player runs /ping.
        String address = Bukkit.getServer().getIp(); // Will ping to your own server's ip when a player runs /ping.
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

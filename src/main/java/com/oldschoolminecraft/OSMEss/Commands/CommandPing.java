package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
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

                if (args.length != 0) {
                    player.sendMessage("§cUsage: /ping");
                    return true;
                }

                if (!pingCooldown.containsKey(player.getName()) || System.currentTimeMillis()/1000 - pingCooldown.get(player.getName())/1000 > 25) {
                    getPing(player);
                    pingCooldown.put(player.getName(), System.currentTimeMillis());
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
                sender.sendMessage("§cCommand can only be executed by a player!");
                return true;
            }
        }
        return true;
    }

//    Old Method.
//    public int getPing(Player player) {
//        try {
//            String address = player.getAddress().getAddress().getHostAddress();
//            String address = Bukkit.getServer().getIp();
//            int port = Bukkit.getServer().getPort();
//            return Math.toIntExact(getLatency(address, port));
//        } catch (IOException ex) {
//            ex.printStackTrace(System.err);
//        }
//
//        return -1;
//    }
//
//    public long reachAddressTime(String ip, int time) {
//        try {
//            InetAddress address = InetAddress.getByName(ip);
//            long finishTime;
//            long startTime = System.currentTimeMillis();
//
//            if (address.isReachable(time)) {
//                finishTime = System.currentTimeMillis();
//                return (finishTime - startTime);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return -1;
//    }
//    Old Method.

    public void getPing(Player player) {
//        String host = "pha.moe"; Test run with an overseas minecraft server.
        String host = "os-mc.net"; // Replace with target's host or IP
        int port = Bukkit.getPort(); // Replace with target's port
        int timeoutMillis = 700; // Timeout in milliseconds

        try (Socket socket = new Socket()) {
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            long startTime = System.currentTimeMillis();
            socket.connect(socketAddress, timeoutMillis);
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            player.sendMessage("§7Your ping is §8" + responseTime + " §7ms.");
        } catch (SocketTimeoutException ex) {
            player.sendMessage("§cPing request timed out!");
        } catch (IOException exception) {
            player.sendMessage("§cUnable to get a ping response");
        }
    }
}

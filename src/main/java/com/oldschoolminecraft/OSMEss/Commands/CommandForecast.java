package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandForecast implements CommandExecutor {

    private final OSMEss plugin;

    public CommandForecast(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("forecast").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("forecast")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.forecast")) {
                    if (Bukkit.getServer().getWorld("world").hasStorm()) {
                        if (Bukkit.getServer().getWorld("world").isThundering()) {
                            player.sendMessage("§5-= §dWEATHER INFO §5=-");
                            player.sendMessage("§6Current Weather: §eSTORM");
                            player.sendMessage("§6Weather Duration Left: §b" + Bukkit.getServer().getWorld("world").getWeatherDuration() + " §8(§d" + formatTimeFromTicks(Bukkit.getServer().getWorld("world").getWeatherDuration()) + "§8)");
                            player.sendMessage("§6Thundering: §aYES");
                            player.sendMessage("§6Thunder Duration Left: §b" + Bukkit.getServer().getWorld("world").getWeatherDuration() + " §8(§d" + formatTimeFromTicks(Bukkit.getServer().getWorld("world").getWeatherDuration()) + "§8)");
                            return true;
                        }

                        player.sendMessage("§5-= §dWEATHER INFO §5=-");
                        player.sendMessage("§6Current Weather: §eRAIN");
                        player.sendMessage("§6Weather Duration Left: §b" + Bukkit.getServer().getWorld("world").getWeatherDuration() + " §8(§d" + formatTimeFromTicks(Bukkit.getServer().getWorld("world").getWeatherDuration()) + "§8)");
                        player.sendMessage("§6Thundering: §4NO");
                        return true;
                    }

                    player.sendMessage("§5-= §dWEATHER INFO §5=-");
                    player.sendMessage("§6Current Weather: §eSUN");
                    player.sendMessage("§6Weather Duration Left: §b" + Bukkit.getServer().getWorld("world").getWeatherDuration() + " §8(§d" + formatTimeFromTicks(Bukkit.getServer().getWorld("world").getWeatherDuration()) + "§8)");
                    player.sendMessage("§6Thundering: §4NO");
                    return true;
                }
                else {
                    player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                    return true;
                }
            }
            else {
                if (Bukkit.getServer().getWorld("world").hasStorm()) {
                    if (Bukkit.getServer().getWorld("world").isThundering()) {
                        sender.sendMessage("-= WEATHER INFO =-");
                        sender.sendMessage("Current Weather: STORM");
                        sender.sendMessage("Weather Duration Left: " + Bukkit.getServer().getWorld("world").getWeatherDuration() + " (" + formatTimeFromTicks(Bukkit.getServer().getWorld("world").getWeatherDuration()) + ")");
                        sender.sendMessage("Thundering: YES");
                        sender.sendMessage("Thunder Duration Left: " + Bukkit.getServer().getWorld("world").getWeatherDuration() + " (" + formatTimeFromTicks(Bukkit.getServer().getWorld("world").getWeatherDuration()) + ")");
                        return true;
                    }

                    sender.sendMessage("-= WEATHER INFO =-");
                    sender.sendMessage("Current Weather: RAIN");
                    sender.sendMessage("Weather Duration Left: " + Bukkit.getServer().getWorld("world").getWeatherDuration() + " (" + formatTimeFromTicks(Bukkit.getServer().getWorld("world").getWeatherDuration()) + ")");
                    sender.sendMessage("Thundering: NO");
                    return true;
                }

                sender.sendMessage("-= WEATHER INFO =-");
                sender.sendMessage("Current Weather:SUN");
                sender.sendMessage("Weather Duration Left: " + Bukkit.getServer().getWorld("world").getWeatherDuration() + " (" + formatTimeFromTicks(Bukkit.getServer().getWorld("world").getWeatherDuration()) + ")");
                sender.sendMessage("Thundering: NO");
                return true;
            }
        }

        return true;
    }

    public String formatTimeFromTicks(long ticks) {
        long totalSeconds = ticks / 20L;
        long minute = totalSeconds % 3600L / 60L;
        long second = totalSeconds % 60L;
        return minute + "m" + second + "s";
    }
}

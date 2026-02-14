package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetTimeZone implements CommandExecutor {

    private final OSMEss plugin;

    public CommandSetTimeZone(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("settimezone").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("settimezone")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length != 1) {
                    player.sendMessage("§cUsage: /settimezone <timezone>");
                    player.sendMessage("§cTimezones: §bUTC[-12 - +14]");
                    player.sendMessage("§cExample Usage: §b/settimezone utc-12 §cor §b/settimezone utc+14");
                    return true;
                }

                switch (args[0].toLowerCase()) {
                    case "utc-12":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-12:00");
                        player.sendMessage("§aTimeZone will now format by UTC-12.");
                        break;
                    case "utc-11":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-11:00");
                        player.sendMessage("§aTimeZone will now format by UTC-11.");
                        break;
                    case "utc-10":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-10:00");
                        player.sendMessage("§aTimeZone will now format by UTC-10.");
                        break;
                    case "utc-9:30":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-09:30");
                        player.sendMessage("§aTimeZone will now format by UTC-9:30.");
                        break;
                    case "utc-9":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-09:00");
                        player.sendMessage("§aTimeZone will now format by UTC-9.");
                        break;
                    case "utc-8":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-08:00");
                        player.sendMessage("§aTimeZone will now format by UTC-8.");
                        break;
                    case "utc-7":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-07:00");
                        player.sendMessage("§aTimeZone will now format by UTC-7.");
                        break;
                    case "utc-6":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-06:00");
                        player.sendMessage("§aTimeZone will now format by UTC-6.");
                        break;
                    case "utc-5":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-05:00");
                        player.sendMessage("§aTimeZone will now format by UTC-5.");
                        break;
                    case "utc-4":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-04:00");
                        player.sendMessage("§aTimeZone will now format by UTC-4.");
                        break;
                    case "utc-3:30":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-03:30");
                        player.sendMessage("§aTimeZone will now format by UTC-3:30.");
                        break;
                    case "utc-3":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-03:00");
                        player.sendMessage("§aTimeZone will now format by UTC-3.");
                        break;
                    case "utc-2":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-02:00");
                        player.sendMessage("§aTimeZone will now format by UTC-2.");
                        break;
                    case "utc-1":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC-01:00");
                        player.sendMessage("§aTimeZone will now format by UTC-1.");
                        break;
                    case "utc":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC");
                        player.sendMessage("§aTimeZone will now format by UTC.");
                        break;
                    case "utc+1":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+01:00");
                        player.sendMessage("§aTimeZone will now format by UTC+1.");
                        break;
                    case "utc+2":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+02:00");
                        player.sendMessage("§aTimeZone will now format by UTC+2.");
                        break;
                    case "utc+3":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+03:00");
                        player.sendMessage("§aTimeZone will now format by UTC+3.");
                        break;
                    case "utc+3:30":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+03:30");
                        player.sendMessage("§aTimeZone will now format by UTC+3:30.");
                        break;
                    case "utc+4":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+04:00");
                        player.sendMessage("§aTimeZone will now format by UTC+4.");
                        break;
                    case "utc+4:30":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+04:30");
                        player.sendMessage("§aTimeZone will now format by UTC+4:30.");
                        break;
                    case "utc+5":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+05:00");
                        player.sendMessage("§aTimeZone will now format by UTC+5.");
                        break;
                    case "utc+5:30":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+05:30");
                        player.sendMessage("§aTimeZone will now format by UTC+5:30.");
                        break;
                    case "utc+5:45":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+05:45");
                        player.sendMessage("§aTimeZone will now format by UTC+5:45.");
                        break;
                    case "utc+6":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+06:00");
                        player.sendMessage("§aTimeZone will now format by UTC+6.");
                        break;
                    case "utc+6:30":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+06:30");
                        player.sendMessage("§aTimeZone will now format by UTC+6:30.");
                        break;
                    case "utc+7":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+07:00");
                        player.sendMessage("§aTimeZone will now format by UTC+7.");
                        break;
                    case "utc+8":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+08:00");
                        player.sendMessage("§aTimeZone will now format by UTC+8.");
                        break;
                    case "utc+8:45":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+08:45");
                        player.sendMessage("§aTimeZone will now format by UTC+8:45.");
                        break;
                    case "utc+9":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+09:00");
                        player.sendMessage("§aTimeZone will now format by UTC+9.");
                        break;
                    case "utc+9:30":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+09:30");
                        player.sendMessage("§aTimeZone will now format by UTC+9:30.");
                        break;
                    case "utc+10":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+10:00");
                        player.sendMessage("§aTimeZone will now format by UTC+10.");
                        break;
                    case "utc+10:30":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+10:30");
                        player.sendMessage("§aTimeZone will now format by UTC+10:30.");
                        break;
                    case "utc+11":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+11:00");
                        player.sendMessage("§aTimeZone will now format by UTC+11.");
                        break;
                    case "utc+12":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+12:00");
                        player.sendMessage("§aTimeZone will now format by UTC+12.");
                        break;
                    case "utc+12:45":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+12:45");
                        player.sendMessage("§aTimeZone will now format by UTC+12:45.");
                        break;
                    case "utc+13":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+13:00");
                        player.sendMessage("§aTimeZone will now format by UTC+13.");
                        break;
                    case "utc+14":
                        plugin.playerDataHandler.updatePlayerTimeZone(player, "UTC+14:00");
                        player.sendMessage("§aTimeZone will now format by UTC+14.");
                        break;
                    default:
                        player.sendMessage("§cInvalid time zone provided!");
                }
            }
            else {
                sender.sendMessage("Command can only be executed by a player!");
                return true;
            }
        }

        return true;
    }
}


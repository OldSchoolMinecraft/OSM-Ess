package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandWarps implements CommandExecutor {

    private final OSMEss plugin;

    public CommandWarps(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("warps").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("warps")) {

            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0 || args.length == 1) {
                    int page;

                    try {
                        page = args.length == 0 ? 0 : Integer.valueOf(args[0]) - 1;

                        if (!plugin.essentials.getWarps().isEmpty()) {
                            List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();

                            if (warps.isEmpty()) {
                                player.sendMessage(plugin.warpNotDefined);
                                return true;
                            }

                            int linesPerPage = 10;
                            int totalPages = (int)Math.ceil((double)warps.size() / (double)linesPerPage);
                            int startingRecord = page * linesPerPage;
                            int titlePageNum = page+1;

                            if (page < 0 || page > totalPages - 1) {
                                player.sendMessage(plugin.invalidPageNum);
                                return true;
                            }

                            player.sendMessage("§7Warps (§3" + warps.size() + "§7) Page §8" + titlePageNum + " §7of §8" + totalPages + "§7:");

                            for (int i = startingRecord; i < warps.size() && i < startingRecord + linesPerPage; i++) {
                                player.sendMessage("§7- §8" + warps.get(i) + " §7(§b/warp §3" + warps.get(i) + "§7)");
                            }

                            return true;
                        }
                        else {
                            player.sendMessage(plugin.warpNotDefined);
                            return true;
                        }

                    } catch (NumberFormatException ex) {
                        player.sendMessage(plugin.invalidNumPara);
                    }

                    return true;
                }
                else {
                    player.sendMessage("§cUsage: /warps [page]");
                    return true;
                }
            }
            else {
                if (args.length == 0 || args.length == 1) {
                    int page;

                    try {
                        page = args.length == 0 ? 0 : Integer.valueOf(args[0]) - 1;

                        if (!plugin.essentials.getWarps().isEmpty()) {
                            List<String> warps = (List<String>) plugin.essentials.getWarps().getWarpNames();

                            if (warps.isEmpty()) {
                                sender.sendMessage("Error: No warps defined.");
                                return true;
                            }

                            int linesPerPage = 10;
                            int totalPages = (int)Math.ceil((double)warps.size() / (double)linesPerPage);
                            int startingRecord = page * linesPerPage;
                            int titlePageNum = page+1;

                            if (page < 0 || page > totalPages - 1) {
                                sender.sendMessage("Error: Invalid integer provided.");
                                return true;
                            }

                            sender.sendMessage("Warps (" + warps.size() + ") Page " + titlePageNum + " of " + totalPages + ":");

                            for (int i = startingRecord; i < warps.size() && i < startingRecord + linesPerPage; i++) {
                                sender.sendMessage("- " + warps.get(i) + " (/warp " + warps.get(i) + ")");
                            }

                            return true;
                        }
                        else {
                            sender.sendMessage("Error: No warps defined.");
                            return true;
                        }

                    } catch (NumberFormatException ex) {
                        sender.sendMessage("Error: Invalid integer provided.");
                    }

                    return true;
                }
                else {
                    sender.sendMessage("Usage: /warps [page]");
                    return true;
                }
            }
        }

        return true;
    }
}

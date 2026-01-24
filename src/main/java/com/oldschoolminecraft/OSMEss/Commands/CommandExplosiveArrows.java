package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandExplosiveArrows implements CommandExecutor {

    private final OSMEss plugin;

    public CommandExplosiveArrows(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("explosivearrows").setExecutor(this);
    }

    public static ArrayList<String> explodeArrow = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("explosivearrows") || cmd.getName().equalsIgnoreCase("explodingarrows") || cmd.getName().equalsIgnoreCase("ea")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.explosivearrows")) {
                    if (!plugin.isExplosiveArrowsEnabled()) {
                        player.sendMessage("§cExplosive arrows are currently disabled!");
                        return true;
                    }

                    if (args.length == 0) {
                        if (plugin.isOnEABlacklist(player)) {
                            player.sendMessage("§cYou are blacklisted from using exploding arrows!");
                            return true;
                        }
                        else {
                            if (explodeArrow.contains(player.getName().toLowerCase())) {
                                explodeArrow.remove(player.getName().toLowerCase());

                                player.sendMessage("§7Your arrows will §4no longer §fEXPLODE§7!");
                                return true;
                            } else {
                                explodeArrow.add(player.getName().toLowerCase());

                                player.sendMessage("§7Your arrows will §anow §fEXPLODE§7!");
                                return true;
                            }
                        }
                    }
                }
                else {
                    player.sendMessage(plugin.noPermission);
                    return true;
                }
            }
            else {
                if (args.length != 1) {
                    sender.sendMessage("Usage: /explosivearrows <player>");
                    return true;
                }

                Player other = Bukkit.getServer().getPlayer(args[0]);

                if (other == null) {
                    OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(args[0]);

                    if (explodeArrow.contains(offline.getName().toLowerCase())) {
                        explodeArrow.remove(offline.getName().toLowerCase());

                        sender.sendMessage(offline.getName() + "'s arrows will no longer explode.");
                        return true;
                    }
                    else {
                        sender.sendMessage("Error: Player doesn't have exploding arrows on.");
                        return true;
                    }

                }

                if (explodeArrow.contains(other.getName().toLowerCase())) {
                    explodeArrow.remove(other.getName().toLowerCase());

                    sender.sendMessage(other.getName() + "'s arrows will no longer explode.");
                    other.sendMessage("§7Your arrows will §4no longer §fEXPLODE§7!");
                    return true;
                }
                else {
                    sender.sendMessage("Error: Player doesn't have exploding arrows on.");
                    return true;
                }
            }

            return true;
        }

        return true;
    }
}

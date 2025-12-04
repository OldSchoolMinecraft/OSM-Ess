package com.oldschoolminecraft.OSMEss.Commands;

import com.Acrobot.ChestShop.Permission;
import com.oldschoolminecraft.OSMEss.OSMEss;
import com.sk89q.worldguard.bukkit.ConfigurationManager;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.yi.acru.bukkit.Lockette.Lockette;

public class CommandEditSign implements CommandExecutor {

    private final OSMEss plugin;

    public CommandEditSign(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("editsign").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("editsign")) {
            if (sender instanceof Player)  {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.editsign")) {
                    if (args.length < 2) {
                        player.sendMessage("§cUsage: /editsign <0:1:2:3> <text>");
                        return true;
                    }

                    StringBuilder msg = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        msg.append(args[i]).append(" ");
                    }

                    Block targetBlock = getTargetBlock(player).getBlock();

                    if (args[0].equalsIgnoreCase("0")) {
                        if (targetBlock.getType() == Material.SIGN || targetBlock.getType() == Material.WALL_SIGN || targetBlock.getType() == Material.SIGN_POST) {
                            Sign sign = (Sign) targetBlock.getState();

                            //Todo: ChestShop, Lockette, & WorldGuard check.

                            if (plugin.isChestShopEnabled()) { // Passed
                                if (com.Acrobot.ChestShop.Utils.uSign.isValidPreparedSign(sign.getLines())) {
                                    player.sendMessage("§cYou may not edit a ChestShop sign!");
                                    return true;
                                }
                                else {
                                    sign.setLine(0, ChatColor.translateAlternateColorCodes('&', msg.toString()));
                                    sign.update();
                                    return true;
                                }
                            }

                            if (plugin.isLandmarksEnabled()) {
                                if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Landmark]")) {
                                    player.sendMessage("§cYou may not edit a Landmark tp sign!");
                                    return true;
                                }
                                else {
                                    sign.setLine(0, ChatColor.translateAlternateColorCodes('&', msg.toString()));
                                    sign.update();
                                    return true;
                                }
                            }

                            if (plugin.isLocketteEnabled()) {
                                if (Lockette.isProtected(sign.getBlock())) {
                                    player.sendMessage("§cYou may not edit a Lockette sign!");
                                    return true;
                                }
                                else {
                                    sign.setLine(0, ChatColor.translateAlternateColorCodes('&', msg.toString()));
                                    sign.update();
                                    return true;
                                }
                            }

                            if (plugin.isWorldGuardEnabled()) {
                                ConfigurationManager cfg = plugin.worldGuard.getGlobalStateManager();
                                WorldConfiguration wcfg = cfg.get(sign.getWorld());

                                if (wcfg.useRegions && !plugin.worldGuard.getGlobalRegionManager().canBuild(player, sign.getBlock())) {
                                    player.sendMessage("§cYou may not edit a WorldGuard sign!");
                                    return true;
                                }
                                else {
                                    sign.setLine(0, ChatColor.translateAlternateColorCodes('&', msg.toString()));
                                    sign.update();
                                    return true;
                                }
                            }

                            return true;
                        }
                        else {
                            player.sendMessage("§cYou're not looking at a sign to edit!");
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("1")) {}
                    if (args[0].equalsIgnoreCase("2")) {}
                    if (args[0].equalsIgnoreCase("3")) {}
                    else {
                        player.sendMessage("§cInvalid line inputted!");
                        player.sendMessage("§cUsage: /editsign <0:1:2:3> <text>");
                        return true;
                    }
                }
                else {
                    player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
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

    public Location getTargetBlock(Player player) {
        BlockIterator iter = new BlockIterator(player);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock.getLocation();
    }
}

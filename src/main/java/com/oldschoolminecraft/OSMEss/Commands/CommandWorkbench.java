package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import net.minecraft.server.Packet100OpenWindow;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CommandWorkbench implements CommandExecutor {

    private final OSMEss plugin;

    public CommandWorkbench(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("workbench").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] strings) {
        if (cmd.getName().equalsIgnoreCase("workbench")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.workbench")) {
                    Packet100OpenWindow packet = new Packet100OpenWindow(1, 1, "Workbench", 9);
                    ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(packet);
                    return true;
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
}

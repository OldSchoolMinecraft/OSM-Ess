package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRainbow implements CommandExecutor {

    private final OSMEss plugin;

    public CommandRainbow(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("rainbow").setExecutor(this);
    }

    private static final List<ChatColor> colors = Arrays.asList(
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.BLUE,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_RED
    );

    private static final List<String> colorful = new ArrayList<>();

    public static String applyRainbow(String displayName) {
        String name = ChatColor.stripColor(displayName);

        int colorIndex = -1;
        StringBuilder newName = new StringBuilder();

        for (char c : name.toCharArray()) {
            colorIndex++;

            if (colorIndex >= colors.size())
                colorIndex = 0;

            newName.append(colors.get(colorIndex)).append(c);
        }

        return newName.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] strings) {
        if (cmd.getName().equalsIgnoreCase("rainbow")) {
           if (sender instanceof Player) {
               Player player = (Player) sender;

               if (player.isOp() || player.hasPermission("osmess.rainbow")) {
                   if (colorful.contains(player.getName())) {
                       colorful.remove(player.getName());
                       plugin.essentials.getUser(player).setNickname(player.getName());
                       player.sendMessage("§aRainbow name removed!");
                   } else {
                       colorful.add(player.getName());
                       plugin.essentials.getUser(player).setNickname(applyRainbow(player.getName()));
                       player.sendMessage("§aRainbow name applied!");
                   }
                   return true;
               }
               else {
                   player.sendMessage("§cI'm sorry, Dave. I'm afraid I can't do that.");
                   return true;
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

package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandDiscord implements CommandExecutor {

    private final OSMEss plugin;

    public CommandDiscord(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("discord").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] strings) {

        if (cmd.getName().equalsIgnoreCase("discord")) {
            sender.sendMessage("§7https://os-mc.net/discord");
            return true;
        }

        return true;
    }
}

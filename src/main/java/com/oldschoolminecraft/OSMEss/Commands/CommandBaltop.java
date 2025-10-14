package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBaltop implements CommandExecutor {

    private final OSMEss plugin;

    public CommandBaltop(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("baltop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("baltop")) {

        }
        return true;
    }
}

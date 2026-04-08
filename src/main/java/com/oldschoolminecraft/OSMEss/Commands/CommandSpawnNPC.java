package com.oldschoolminecraft.OSMEss.Commands;

import com.oldschoolminecraft.OSMEss.Handlers.EntityIdAllocator;
import com.oldschoolminecraft.OSMEss.HerobrineStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import net.minecraft.server.Packet20NamedEntitySpawn;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static com.oldschoolminecraft.OSMEss.HerobrineThread.getPlayersInRadius;

public class CommandSpawnNPC implements CommandExecutor {

    private final OSMEss plugin;

    public CommandSpawnNPC(OSMEss plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("spawnnpc").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spawnnpc")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp() || player.hasPermission("osmess.spawnnpc")) {
                    if (args.length == 0) {
                        player.sendMessage("§cUsage: /spawnnpc <name> [item-id]");
                        return true;
                    }

                    if (args.length == 1) {
                        if (plugin.herobrineStatus == HerobrineStatus.ACTIVE) {
                            player.sendMessage("§cError: Please wait for Herobrine to vanish.");
                            return true;
                        }

                        // Build packet
                        Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn();
                        packet.a = 20;
                        packet.b = args[0];

                        packet.c = (int) Math.floor(player.getLocation().getX() * 32.0);
                        packet.d = (int) Math.floor(player.getLocation().getY() * 32.0);
                        packet.e = (int) Math.floor(player.getLocation().getZ() * 32.0);
                        packet.f = (byte) player.getLocation().getYaw();
                        packet.g = (byte) player.getLocation().getPitch();

                        ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(packet);
                        double range = 100.0; // the desired radius

                        List<Player> nearbyPlayers = getPlayersInRadius(player, range);

                        for (Player p : nearbyPlayers) {
                            CraftPlayer np = (CraftPlayer) p;

                            np.getHandle().netServerHandler.sendPacket(packet);
                        }

                        player.sendMessage("§aNPC named " + args[0].toUpperCase() + " summoned.");
                        return true;
                    }
                    if (args.length == 2) {
                        if (plugin.herobrineStatus == HerobrineStatus.ACTIVE) {
                            player.sendMessage("§cError: Please wait for Herobrine to vanish.");
                            return true;
                        }

                        try {
                            // Build packet
                            Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn();
                            packet.a = 20;
                            packet.b = args[0];

                            packet.c = (int) Math.floor(player.getLocation().getX() * 32.0);
                            packet.d = (int) Math.floor(player.getLocation().getY() * 32.0);
                            packet.e = (int) Math.floor(player.getLocation().getZ() * 32.0);
                            packet.f = (byte) player.getLocation().getYaw();
                            packet.g = (byte) player.getLocation().getPitch();

                            Material material = Material.matchMaterial(args[1]);
                            if (material == null) {player.sendMessage("§cError: Item with that id doesn't exist."); return true;}
                            else {packet.h = Integer.parseInt(args[1]);}

                            ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(packet);
                            double range = 30.0; // the desired radius

                            List<Player> nearbyPlayers = getPlayersInRadius(player, range);

                            for (Player p : nearbyPlayers) {
                                CraftPlayer np = (CraftPlayer) p;

                                np.getHandle().netServerHandler.sendPacket(packet);
                            }

                            player.sendMessage("§aNPC named " + args[0].toUpperCase() + " summoned with " + material.name().toUpperCase().replaceAll("_", " ") + ".");
                            return true;

                        } catch (NumberFormatException ex) {
                            player.sendMessage(plugin.invalidNumPara);
                        }

                        return true;
                    }
                    else {
                        player.sendMessage("§cUsage: /spawnnpc <name> [item-id]");
                        return true;
                    }
                }
                else {
                    player.sendMessage(plugin.noPermission);
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

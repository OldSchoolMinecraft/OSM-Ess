package com.oldschoolminecraft.OSMEss.Listeners;

import com.oldschoolminecraft.OSMEss.OSMEss;
import net.oldschoolminecraft.lmk.LandmarkData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LMKSignListener implements Listener {

    public OSMEss plugin;

    public LMKSignListener(OSMEss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() || player.hasPermission("osmess.landmarksigns.create")) {
            if (event.getLine(0).equals("[Landmark]")) {
                if (plugin.isLandmarksEnabled()) {
                    if (event.getLine(1).isEmpty()) {
                        player.sendMessage("§cPlease fill line 1 with a valid landmark name!");
                        event.setLine(0, "§4[Landmark]");
                        event.setLine(1, "§c???");
                        if (!event.getLine(2).isEmpty()) {event.setLine(2, " ");}
                        if (!event.getLine(3).isEmpty()) {event.setLine(3, " ");}
                        return;
                    }
                    String lmkNameInputed = event.getLine(1);

                    if (plugin.landmarks.getLmkManager().findLandmark(event.getLine(1)) != null) {
                        player.sendMessage("§aLandmark sign created for " + lmkNameInputed + "!");
                        event.setLine(0, "§1[Landmark]");
                        event.setLine(1, lmkNameInputed);
                        if (!event.getLine(2).isEmpty()) {event.setLine(2, event.getLine(2));}
                        if (!event.getLine(3).isEmpty()) {event.setLine(3, event.getLine(3));}
                    }
                    else {
                        player.sendMessage("§cLandmark " + event.getLine(1) + " does not exist!");
                        event.setLine(0, "§4[Landmark]");
                        event.setLine(1, "§c???");
                        if (!event.getLine(2).isEmpty()) {event.setLine(2, " ");}
                        if (!event.getLine(3).isEmpty()) {event.setLine(3, " ");}
                    }
                }
                else {
                    player.sendMessage("§cPlugin 'Landmarks' is missing to create a landmark sign!");
                }
            }
        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();

            if (plugin.isLandmarksEnabled()) {
                if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "[Landmark]")) { // && !sign.getLine(1).isEmpty() && sign.getLine(2).isEmpty() && sign.getLine(3).isEmpty() | Removed to allow old created lmk signs with extra lines below to work.
                    try {
                        String name = sign.getLine(1);
                        LandmarkData landmark = plugin.landmarks.getLmkManager().findLandmark(name);

                        if (landmark == null) player.sendMessage("§cLandmark " + name + " does not exist!");
                        else Bukkit.getServer().dispatchCommand(player, "lmk " + sign.getLine(1));
                    } catch (NullPointerException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }
    }
}

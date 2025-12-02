package com.oldschoolminecraft.OSMEss.Listeners;

import com.oldschoolminecraft.OSMEss.OSMEss;
import net.oldschoolminecraft.lmk.LandmarkData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerWorldListener implements Listener {
    public OSMEss plugin;

    public PlayerWorldListener(OSMEss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() || player.hasPermission("osmess.chatcolor")) {
            if (!plugin.hasChatColorMessageSet(player)) return;

//            event.setMessage(plugin.getChatColorMessageSetting(player) + event.getMessage());

            if (plugin.getChatColorMessageSetting(player).equals("&0")) {
                event.setMessage(ChatColor.BLACK + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&1")) {
                event.setMessage(ChatColor.DARK_BLUE + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&2")) {
                event.setMessage(ChatColor.DARK_GREEN + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&3")) {
                event.setMessage(ChatColor.DARK_AQUA + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&4")) {
                event.setMessage(ChatColor.DARK_RED + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&5")) {
                event.setMessage(ChatColor.DARK_PURPLE + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&6")) {
                event.setMessage(ChatColor.GOLD + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&7")) {
                event.setMessage(ChatColor.GRAY + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&8")) {
                event.setMessage(ChatColor.DARK_GRAY + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&9")) {
                event.setMessage(ChatColor.BLUE + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&a")) {
                event.setMessage(ChatColor.GREEN + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&b")) {
                event.setMessage(ChatColor.AQUA + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&c")) {
                event.setMessage(ChatColor.RED + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&d")) {
                event.setMessage(ChatColor.LIGHT_PURPLE + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&e")) {
                event.setMessage(ChatColor.YELLOW + event.getMessage());
            }
            if (plugin.getChatColorMessageSetting(player).equals("&f")) {
                event.setMessage(ChatColor.WHITE + event.getMessage());
            }
            else {
                event.setMessage(event.getMessage());
//                event.setMessage(ChatColor.WHITE + event.getMessage());
            }
        }
        else {
            event.setMessage(event.getMessage());
        }
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

    @EventHandler(priority = Event.Priority.Highest)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            // Landmarks
            if (block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();

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

            //Lockette
            if (plugin.isLocketteEnabled()) {
                if (plugin.lockette.isProtected(block) && !plugin.lockette.isOwner(block, player.getName())) {
                    if (player.isOp() || player.hasPermission("osmess.lockettebypass")) {
                        event.setCancelled(false);
                    }
                }
            }
        }

        if (action == Action.LEFT_CLICK_BLOCK) {
            //Lockette
            if (plugin.isLocketteEnabled()) {
                if (plugin.lockette.isProtected(block) && !plugin.lockette.isOwner(block, player.getName())) {
                    if (player.isOp() || player.hasPermission("osmess.lockettebypass")) {
                        event.setCancelled(false);
                    }
                }
            }
        }
    }

}

package com.oldschoolminecraft.OSMEss.Listeners;

import com.oldschoolminecraft.OSMEss.Commands.CommandExplosiveArrows;
import com.oldschoolminecraft.OSMEss.Handlers.EntityIdAllocator;
import com.oldschoolminecraft.OSMEss.HerobrineStatus;
import com.oldschoolminecraft.OSMEss.HerobrineThread;
import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.Util.HerobrineUtil;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet32EntityLook;
import net.minecraft.server.Packet34EntityTeleport;
import net.oldschoolminecraft.lmk.LandmarkData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

import static com.oldschoolminecraft.OSMEss.HerobrineThread.getPlayersInRadius;

public class PlayerWorldListener implements Listener {
    public OSMEss plugin;

    public HerobrineThread herobrineThread;
    public final Object lock = new Object();

    public PlayerWorldListener(OSMEss plugin) {
        this.plugin = plugin;
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

    public static String applyRainbow(String message) {
        String msg = ChatColor.stripColor(message);

        int colorIndex = -1;
        StringBuilder newMessage = new StringBuilder();

        for (char c : msg.toCharArray()) {
            colorIndex++;

            if (colorIndex >= colors.size())
                colorIndex = 0;

            newMessage.append(colors.get(colorIndex)).append(c);
        }

        return newMessage.toString();
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

//        if (player.isOp() || player.hasPermission("osmess.chatcolor")) {
//            if (!plugin.hasChatColorMessageSet(player)) {return;}
//
//            if (Objects.equals(plugin.getChatColorMessageSetting(player), "&rgb")) {event.setMessage(applyRainbow(event.getMessage()));}
//            else {event.setMessage(plugin.getChatColorMessageSetting(player) + event.getMessage());}
//        }


        if (player.isOp() || player.hasPermission("osmess.chatcolor")) {
            if (!plugin.hasChatColorMessageSet(player)) {return;}

            switch (plugin.getChatColorMessageSetting(player)) {
                case "&0":
                    event.setMessage(ChatColor.BLACK + event.getMessage());
                    break;
                case "&1":
                    event.setMessage(ChatColor.DARK_BLUE + event.getMessage());
                    break;
                case "&2":
                    event.setMessage(ChatColor.DARK_GREEN + event.getMessage());
                    break;
                case "&3":
                    event.setMessage(ChatColor.DARK_AQUA + event.getMessage());
                    break;
                case "&4":
                    event.setMessage(ChatColor.DARK_RED + event.getMessage());
                    break;
                case "&5":
                    event.setMessage(ChatColor.DARK_PURPLE + event.getMessage());
                    break;
                case "&6":
                    event.setMessage(ChatColor.GOLD + event.getMessage());
                    break;
                case "&7":
                    event.setMessage(ChatColor.GRAY + event.getMessage());
                    break;
                case "&8":
                    event.setMessage(ChatColor.DARK_GRAY + event.getMessage());
                    break;
                case "&9":
                    event.setMessage(ChatColor.BLUE + event.getMessage());
                    break;
                case "&a":
                    event.setMessage(ChatColor.GREEN + event.getMessage());
                    break;
                case "&b":
                    event.setMessage(ChatColor.AQUA + event.getMessage());
                    break;
                case "&c":
                    event.setMessage(ChatColor.RED + event.getMessage());
                    break;
                case "&d":
                    event.setMessage(ChatColor.LIGHT_PURPLE + event.getMessage());
                    break;
                case "&e":
                    event.setMessage(ChatColor.YELLOW + event.getMessage());
                    break;
                case "&f":
                    event.setMessage(ChatColor.WHITE + event.getMessage());
                    break;
                case "&rgb":
                    event.setMessage(applyRainbow(event.getMessage()));
                    break;
            }
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

    public static final Map<UUID, Long> lastLookUpdate = new HashMap<>();

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (plugin.herobrineStatus != HerobrineStatus.ACTIVE) return;

        long now = System.currentTimeMillis();
        UUID playerId = player.getUniqueId();

        // Check if enough time has passed since last update
        if (lastLookUpdate.containsKey(playerId)) {
            long lastTime = lastLookUpdate.get(playerId);
            if (now - lastTime < 100) return; // less than 50ms → skip
        }

        // Update Herobrine look
        updateHerobrineLook(player, HerobrineUtil.getCurrentLocation());

        // Record timestamp
        lastLookUpdate.put(playerId, now);
    }

    public void updateHerobrineLook(Player player, Location fakeLoc) {
        CraftPlayer cp = (CraftPlayer) player;

        Location playerEye = player.getEyeLocation();

        // Eye positions
        // ---- Eye positions ----
        double fakeEyeY   = fakeLoc.getY() + 1.62;
        double playerEyeY = playerEye.getY();

        // ---- Eye → eye deltas ----
        double dx = playerEye.getX() - fakeLoc.getX();
        double dy = playerEyeY - fakeEyeY;
        double dz = playerEye.getZ() - fakeLoc.getZ();

        // ---- Rotation ----
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) -(Math.atan2(dy, horizontal) * 180.0 / Math.PI);

        // Convert to protocol bytes
        byte yawByte   = (byte) ((yaw   * 256.0f) / 360.0f);
        byte pitchByte = (byte) ((pitch * 256.0f) / 360.0f);

        fakeLoc.setYaw(yawByte);
        fakeLoc.setPitch(pitchByte);

        // ---- Teleport packet ----
        Packet34EntityTeleport tp = new Packet34EntityTeleport();
        tp.a = EntityIdAllocator.getHerobrineEntityID();
        tp.b = (int) Math.floor(fakeLoc.getX() * 32.0);
        tp.c = (int) Math.floor(fakeLoc.getY() * 32.0);
        tp.d = (int) Math.floor(fakeLoc.getZ() * 32.0);
        tp.e = yawByte;
        tp.f = pitchByte;

        HerobrineUtil.updateLocation(fakeLoc);

        cp.getHandle().netServerHandler.sendPacket(tp);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() == Material.OBSIDIAN) {
            Block relative1 = block.getRelative(BlockFace.EAST);
            Block relative2 = block.getRelative(BlockFace.WEST);
            Block relative3 = block.getRelative(BlockFace.NORTH);
            Block relative4 = block.getRelative(BlockFace.SOUTH);
            Block relative5 = block.getRelative(BlockFace.UP);
            Block relative6 = block.getRelative(BlockFace.DOWN);

            if (relative1.getType() == Material.PORTAL ||
                    relative2.getType() == Material.PORTAL ||
                    relative3.getType() == Material.PORTAL ||
                    relative4.getType() == Material.PORTAL ||
                    relative5.getType() == Material.PORTAL ||
                    relative6.getType() == Material.PORTAL) {

                if (player.isOp() /* ||player.hasPermission("osmess.portaldrop") */) {
                    Random random = new Random();
                    int result = random.nextInt(100) + 1;
                    int chance = 1; // 1% chance for portal block drop. (Random must fall at or below 1)

                    if (result <= chance) {
                        block.getWorld().dropItem(block.getLocation(),new ItemStack(Material.PORTAL, 1));
                        player.sendMessage("§3[Debug] §5Portal block dropped. §7(Result: " + result + ")");
                    }
                    else {
                        player.sendMessage("§3[Debug] §cNo portal block. §7(Result: " + result + ")");
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (plugin.isBlockOnPTReq(block.getType())) {
            if (plugin.playtimeHandler.getTotalPlayTimeInMillis(player) >= plugin.getMinimumRequiredPlaytimeToPlaceBlock()) { // 6 hours
                event.setCancelled(false);
            }
            else {
                event.setCancelled(true);
                player.sendMessage("§cYou do not have enough playtime to place " + block.getType().name().toUpperCase().replaceAll("_", " ") + "!");
            }
        }

        if (block.getLocation().getBlock().getType() == Material.FIRE) {
            if (plugin.isHerobrineEnabled()) {
                if (plugin.totemHandler.meetsTotemCriteriaLayer0(block) && plugin.totemHandler.meetsTotemCriteriaLayerNeg1(block) && plugin.totemHandler.meetsTotemCriteriaLayerNeg2(block)) {
                    Random random = new Random();
                    int result = random.nextInt(100) + 1;
                    int chance = plugin.getChanceForHerobrineScare(); // Chance for herobrine to scare based on config.yml.

                    if (result <= chance) {
                        synchronized (lock) {
                            if (getHerobrineStatus() == HerobrineStatus.INACTIVE) {

                                player.getWorld().strikeLightningEffect(block.getLocation());

                                Location fakeLoc = block.getLocation().add(0.5, 0, 0.5);
                                Location playerLoc = player.getLocation();

                                // Aim at eyes
                                double fakeEyeY   = fakeLoc.getY() + 1.62;
                                double playerEyeY = playerLoc.getY() + 1.62;

                                double dx = playerLoc.getX() - fakeLoc.getX();
                                double dy = playerEyeY - fakeEyeY;
                                double dz = playerLoc.getZ() - fakeLoc.getZ();


                                // Yaw: atan2(Z, X)
                                float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;

                                // Pitch: atan2(Y, horizontal distance)
                                double horizontal = Math.sqrt(dx * dx + dz * dz);
                                float pitch = (float) -(Math.atan2(dy, horizontal) * 180.0 / Math.PI);

                                // Convert to packet bytes
                                byte yawByte   = (byte) ((yaw   * 256.0f) / 360.0f);
                                byte pitchByte = (byte) ((pitch * 256.0f) / 360.0f);

                                fakeLoc.setYaw(yawByte);
                                fakeLoc.setPitch(pitchByte);

                                // Build packet
                                Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn();
                                packet.a = EntityIdAllocator.getHerobrineEntityID();
                                packet.b = "Herobrine";

                                // Fixed-point position
                                packet.c = (int) Math.floor(fakeLoc.getX() * 32.0);
                                packet.d = (int) Math.floor(fakeLoc.getY() * 32.0);
                                packet.e = (int) Math.floor(fakeLoc.getZ() * 32.0);

                                packet.f = yawByte;
                                packet.g = pitchByte;
                                packet.h = 276; // Diamond Sword

                                HerobrineUtil.updateLocation(fakeLoc);

                                ((CraftPlayer) player).getHandle().netServerHandler.sendPacket(packet);

                                double range = 30.0; // the desired radius

                                List<Player> nearbyPlayers = getPlayersInRadius(player, range);

                                for (Player p : nearbyPlayers) {
                                    CraftPlayer np = (CraftPlayer) p;

                                    np.getHandle().netServerHandler.sendPacket(packet);
                                }

                                setHerobrineStatus(HerobrineStatus.ACTIVE);
                                herobrineThread = new HerobrineThread(player, fakeLoc, 5, this::endScare);
                                herobrineThread.start();

                                player.sendMessage(chooseRandomScareMessage());
                            }
                        }

//                    Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, () -> {
//                        Packet29DestroyEntity killPacket = new Packet29DestroyEntity();
//                        killPacket.a = EntityIdAllocator.getHerobrineEntityID();
//                        ((CraftPlayer)player).getHandle().netServerHandler.sendPacket(killPacket);
//
//                    }, 100L);
                    }
                }
            }
        }
    }

    public void endScare() {
        synchronized (lock) {
            if (getHerobrineStatus() == HerobrineStatus.INACTIVE) return;

            setHerobrineStatus(HerobrineStatus.INACTIVE);
            if (herobrineThread.isAlive()) {herobrineThread.interrupt();}
        }
    }

    public String chooseRandomScareMessage() {
        List<String> scareMessages = new ArrayList<>();

        scareMessages.add("§7[Herobrine -> You] §fYou are not alone.");
        scareMessages.add("§7[Herobrine -> You] §fWhy do you summon me?");
        scareMessages.add("§7[Herobrine -> You] §fI am back from hell.");
        scareMessages.add("§7[Herobrine -> You] §fI am always watching.");
        scareMessages.add("§7[Herobrine -> You] §fYou don't know what you did.");

        Random random = new Random();
        int index = random.nextInt(scareMessages.size());

        return scareMessages.get(index);
    }

    public void setHerobrineStatus(HerobrineStatus herobrineStatus) {
        plugin.herobrineStatus = herobrineStatus;
    }
    public HerobrineStatus getHerobrineStatus() {return plugin.herobrineStatus;}

    @EventHandler
    public void on(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow && ((Arrow) event.getEntity()).getShooter() instanceof Player) {
            Player player = (Player) ((Arrow) event.getEntity()).getShooter();

            if (plugin.isExplosiveArrowsEnabled()) {
                if (CommandExplosiveArrows.explodeArrow.contains(player.getName().toLowerCase())) {
                    if (player.isOp() || player.hasPermission("osmess.explosivearrows")) {
                        event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), plugin.getEABlastRadius(), false);
                        event.getEntity().remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (plugin.isFishTreasureEnabled()) {
                if (player.isOp()) { // Operator/Admin only for now. Will be removed later when deemed ready for public use.
                    if (event.getCaught() instanceof Item) {
                        Random random = new Random();
                        double result = random.nextInt(100) + random.nextDouble();
                        double chance = plugin.getChanceForFishTreasure();

                        if (result <= chance) { // Within boundary of configured chance.
                            String resultFormatted = String.format("%.2f%%", result);
                            Item itemEntity = (Item) event.getCaught();

                            List<String> allPossibleTreasures = plugin.configSettingCFG.getStringList("Settings.FishTreasure.treasureList", new ArrayList<>());

                            Random randomTreasure = new Random();
                            int randomIndex = randomTreasure.nextInt(allPossibleTreasures.size());
                            ItemStack treasureToGive = new ItemStack(Material.getMaterial(allPossibleTreasures.get(randomIndex)), 1);

                            itemEntity.setItemStack(treasureToGive);

                            player.sendMessage("§3[Debug] §aTreasure caught! §7(Result: " + resultFormatted + ")");
                        }
                        else { // Outside configured chance.
                            String resultFormatted = String.format("%.2f%%", result);
                            Item itemEntity = (Item) event.getCaught();
                            ItemStack itemStack = new ItemStack(Material.RAW_FISH, 1);
                            itemEntity.setItemStack(itemStack);
                            player.sendMessage("§3[Debug] §cNo treasure. §7(Result: " + resultFormatted + ")");
                        }
                    }
                }
                else { // No permission. (Temporary)
                    if (event.getCaught() instanceof Item) {
                        Item itemEntity = (Item) event.getCaught();
                        ItemStack itemStack = new ItemStack(Material.RAW_FISH, 1);
                        itemEntity.setItemStack(itemStack);
                    }
                }
            }
            else { // Feature disabled.
                if (event.getCaught() instanceof Item) {
                    Item itemEntity = (Item) event.getCaught();
                    ItemStack itemStack = new ItemStack(Material.RAW_FISH, 1);
                    itemEntity.setItemStack(itemStack);
                }
            }
        }
    }
}

package com.oldschoolminecraft.OSMEss.Handlers;

import com.earth2me.essentials.User;
import com.google.gson.Gson;
import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.AuctionThread;
import com.oldschoolminecraft.OSMEss.Commands.CommandBid;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AuctionHandler {

    public OSMEss plugin;

    public AuctionThread auctionThread;
    private final Map<String, Double> bidders = Collections.synchronizedMap(new HashMap<>());
    private final ArrayList<String> auctionHoster = new ArrayList<>();
    public final Object lock = new Object();

    private long lastStartAuctionVote;

    public long lastAuctionEndTime = 0L;
    public long AUCTION_COOLDOWN_MS = 60 * 1000; // 1 minute

    public int totalBidders = 0;
    public static double startBid;

    public AuctionHandler(OSMEss plugin) {
        this.plugin = plugin;
    }

    public void setAuctionStatus(AuctionStatus auctionStatus) {
        plugin.auctionStatus = auctionStatus;
    }
    public AuctionStatus getAuctionStatus() {return plugin.auctionStatus;}

//  Start/End Auction
    public void startAuction(Player player, double startingBid) {
        synchronized (lock) {
            PlayerInventory inventory = player.getInventory();
            ItemStack item = inventory.getItemInHand();

            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage("§cYou're not holding anything to auction!");
                return;
            }

//        String name = item.getType().name();
            startBid = startingBid;
            int amount = item.getAmount();

            MaterialData materialData = item.getData();

            //Blacklist
            if (item.getType() == Material.BEDROCK) {
                player.sendMessage("§cThis item is blacklisted from being auctioned!");
                Bukkit.getServer().getLogger().warning("[OSM-Ess] " + player.getName() + " tried to auction " + amount + "x BEDROCK which is blacklisted!");
            } else if (item.getType() == Material.STEP && materialData.getData() == 4) { // Crash Slab
                player.sendMessage("§cThis item is blacklisted from being auctioned!");
                Bukkit.getServer().getLogger().warning("[OSM-Ess] " + player.getName() + " tried to auction " + amount + "x CRASH SLAB which is blacklisted!");
            } else {
                storeAuctionItem(player);
                player.setItemInHand(null);

                auctionHoster.add(player.getName());

                lastStartAuctionVote = System.currentTimeMillis();
                setAuctionStatus(AuctionStatus.ACTIVE);

                auctionThread = new AuctionThread(60, this::endAuction);
                auctionThread.start();

                Bukkit.broadcastMessage("§9Auction started by §b" + player.getName() + "§9!");
                Bukkit.broadcastMessage("§9Auction ends in §b1 minute§9!");


                Bukkit.broadcastMessage("§9Prize: §b" + amount + "x " + getAuctionItemName());
                Bukkit.broadcastMessage("§9Starting Bid: §b$" + startingBid);
            }
        }
    }

    public void offlineWinner(OfflinePlayer player) {
        plugin.essentials.getOfflineUser(player.getName()).takeMoney(getTopBidAmount());
        backupAuctionWinnerItems(player);

        if (getAuctionHost() == null) {
            plugin.essentials.getOfflineUser(getOfflineAuctionHost().getName()).giveMoney(getTopBidAmount());
        }
        else {
            plugin.essentials.getUser(getAuctionHost()).giveMoney(getTopBidAmount());
        }

        Bukkit.broadcastMessage("§b" + player.getName() + " §2won §9the auction for:");
        Bukkit.broadcastMessage("§b" + getAuctionItem().getAmount() + "x " + getAuctionItemName());

        auctionHoster.clear();
        bidders.clear();
        CommandBid.confirmBidList.clear();
        totalBidders = 0;
        wipeAuctionFile();
    }
    public void onlineWinner(Player player) {
        plugin.essentials.getUser(player).takeMoney(getTopBidAmount());
        awardAuctionItem(player);

        if (getAuctionHost() == null) {
            plugin.essentials.getOfflineUser(getOfflineAuctionHost().getName()).giveMoney(getTopBidAmount());
        }
        else {
            plugin.essentials.getUser(getAuctionHost()).giveMoney(getTopBidAmount());
        }

        if (getTopBidder() == null) {
            Bukkit.broadcastMessage("§b" + getOfflineTopBidder().getName() + " §2won §9the auction for:");
            Bukkit.broadcastMessage("§b" + getAuctionItem().getAmount() + "x " + getAuctionItemName());
        }
        Bukkit.broadcastMessage("§b" + player.getName() + " §2won §9the auction for:");
        Bukkit.broadcastMessage("§b" + getAuctionItem().getAmount() + "x " + getAuctionItemName());

        auctionHoster.clear();
        bidders.clear();
        CommandBid.confirmBidList.clear();
        totalBidders = 0;
        wipeAuctionFile();
    }


    public void runOfflineCheck2() {
        if (getTopBidder() == null) {
            if (!doesOfflineWinnerHaveTheMoney(getOfflineTopBidder())) {
                Bukkit.broadcastMessage("§b" + getOfflineTopBidder().getName() + " §9didn't have the money. Disqualified!");
                removeOfflinePlayerFromAuction(getOfflineTopBidder());
                // Offline Strike 2.


                if (totalBidders != 0) {
                    runFinalOfflineCheck();
                }
                else {
                    if (getAuctionHost() == null) {
                        backupAuctionHostItems(getOfflineAuctionHost());
                    }
                    else {
                        awardAuctionItem(getAuctionHost());
                    }

                    auctionHoster.clear();
                    CommandBid.confirmBidList.clear();
                    wipeAuctionFile();
                    Bukkit.broadcastMessage("§9Auction ended with no bidders!");
                }
            }
            else { offlineWinner(getOfflineTopBidder()); }
        }
        else {
            runOnlineCheck2();
        }
    }
    public void runOnlineCheck2() {
        if (getTopBidder() != null) {
            if (!doesOnlineWinnerHaveTheMoney(getTopBidder())) {
                Bukkit.broadcastMessage("§b" + getTopBidder().getName() + " §9didn't have the money. Disqualified!");
                removeOnlinePlayerFromAuction(getTopBidder());
                // Online Strike 2.


                if (totalBidders != 0) {
                    runFinalOnlineCheck();
                }
                else {
                    if (getAuctionHost() == null) {
                        backupAuctionHostItems(getOfflineAuctionHost());
                    }
                    else {
                        awardAuctionItem(getAuctionHost());
                    }

                    auctionHoster.clear();
                    CommandBid.confirmBidList.clear();
                    wipeAuctionFile();
                    Bukkit.broadcastMessage("§9Auction ended with no bidders!");
                }
            }
            else { onlineWinner(getTopBidder()); }
        }
        else {
            runOfflineCheck2();
        }
    }


    public void runFinalOfflineCheck() {
        if (getTopBidder() == null) {
            if (!doesOfflineWinnerHaveTheMoney(getOfflineTopBidder())) {
                Bukkit.broadcastMessage("§b" + getOfflineTopBidder().getName() + " §9didn't have the money. Disqualified!");
                removeOfflinePlayerFromAuction(getOfflineTopBidder());
                // Offline Strike 3. End Auction w/o a winner.

                if (getAuctionHost() == null) {
                    backupAuctionHostItems(getOfflineAuctionHost());
                }
                else {
                    awardAuctionItem(getAuctionHost());
                }

                auctionHoster.clear();
                bidders.clear();
                CommandBid.confirmBidList.clear();
                totalBidders = 0;
                wipeAuctionFile();

                Bukkit.broadcastMessage("§9Auction ended with too many winners without the money!");
            }
            else { offlineWinner(getOfflineTopBidder()); }
        }
        else {
            runFinalOnlineCheck();
        }
    }
    public void runFinalOnlineCheck() {
        if (getTopBidder() != null) {
            if (!doesOnlineWinnerHaveTheMoney(getTopBidder())) {
                Bukkit.broadcastMessage("§b" + getTopBidder().getName() + " §9didn't have the money. Disqualified!");
                removeOnlinePlayerFromAuction(getTopBidder());
                // Online Strike 3. End Auction w/o a winner.

                if (getAuctionHost() == null) {
                    backupAuctionHostItems(getOfflineAuctionHost());
                }
                else {
                    awardAuctionItem(getAuctionHost());
                }

                auctionHoster.clear();
                bidders.clear();
                CommandBid.confirmBidList.clear();
                totalBidders = 0;
                wipeAuctionFile();

                Bukkit.broadcastMessage("§9Auction ended with too many winners without the money!");
            }
            else { onlineWinner(getTopBidder()); }
        }
        else {
            runFinalOfflineCheck();
        }
    }


    public void endAuction() {
        synchronized (lock) {
            if (getAuctionStatus() == AuctionStatus.INACTIVE) return;

            if (totalBidders == 0) { // No one entered the auction, return the item.
                if (getAuctionHost() == null) {
                    backupAuctionHostItems(getOfflineAuctionHost());
                }
                else {
                    awardAuctionItem(getAuctionHost());
                }

                auctionHoster.clear();
                CommandBid.confirmBidList.clear();
                wipeAuctionFile();

                Bukkit.broadcastMessage("§9Auction ended with no bidders!");
            }
            else { // Auction ended with a bidder.
                if (getTopBidder() == null) { // Winner is offline.

                    if (!doesOfflineWinnerHaveTheMoney(getOfflineTopBidder())) { // Offline winner doesn't have the money.
                        Bukkit.broadcastMessage("§b" + getOfflineTopBidder().getName() + " §9didn't have the money. Disqualified!");
                        removeOfflinePlayerFromAuction(getOfflineTopBidder());
                        // Offline Strike 1.


                        if (totalBidders != 0) {
                            // Repeat at least 1 or 2 more times.
                            runOfflineCheck2();
                        }
                        else {
                            if (getAuctionHost() == null) {
                                backupAuctionHostItems(getOfflineAuctionHost());
                            }
                            else {
                                awardAuctionItem(getAuctionHost());
                            }

                            auctionHoster.clear();
                            CommandBid.confirmBidList.clear();
                            wipeAuctionFile();
                            Bukkit.broadcastMessage("§9Auction ended with no bidders!");
                        }
                    }
                    else { offlineWinner(getOfflineTopBidder()); } // Offline winner has the money.
                }
                else {
                    if (!doesOnlineWinnerHaveTheMoney(getTopBidder())) { // Online winner doesn't have the money.
                        Bukkit.broadcastMessage("§b" + getTopBidder().getName() + " §9didn't have the money. Disqualified!");
                        removeOnlinePlayerFromAuction(getTopBidder());
                        // Online Strike 1.


                        if (totalBidders != 0) {
                            // Repeat at least 1 or 2 more times.
                            runOnlineCheck2();
                        }
                        else {
                            if (getAuctionHost() == null) {
                                backupAuctionHostItems(getOfflineAuctionHost());
                            }
                            else {
                                awardAuctionItem(getAuctionHost());
                            }

                            auctionHoster.clear();
                            CommandBid.confirmBidList.clear();
                            wipeAuctionFile();
                            Bukkit.broadcastMessage("§9Auction ended with no bidders!");
                        }
                    }
                    else { onlineWinner(getTopBidder()); } // Online winner has the money.
                }

            }

            setAuctionStatus(AuctionStatus.INACTIVE);
            lastAuctionEndTime = System.currentTimeMillis();
            if (auctionThread.isAlive()) {auctionThread.interrupt();}
        }
    }

    public void forceEndAuction() {
        synchronized (lock) {
            if (getAuctionStatus() == AuctionStatus.INACTIVE) return;

            if (getAuctionHost() == null) {
                backupAuctionHostItems(getOfflineAuctionHost());
            }
            else {
                awardAuctionItem(getAuctionHost());
            }

            auctionHoster.clear();
            bidders.clear();
            CommandBid.confirmBidList.clear();
            totalBidders = 0;
            wipeAuctionFile();

            setAuctionStatus(AuctionStatus.INACTIVE);
            lastAuctionEndTime = System.currentTimeMillis();
            if (auctionThread.isAlive()) {auctionThread.interrupt();}
        }
    }
//  Start/End Auction



//  Add/Remove Players from the Auction
    public void addToAuction(Player player, double bidAmount) {
        if (auctionHoster.contains(player.getName())) { player.sendMessage("§cYou cannot bid on your own auction!"); return;}

        synchronized (bidders) {
            if (bidders.containsKey(player.getName())) { // Re-add them but with a higher bid amount.
                bidders.put(player.getName(), bidAmount);

                Bukkit.broadcastMessage("§b" + player.getName() + " §9increased their bid to §b$" + bidAmount + "§9!");


            }
            else {
                bidders.put(player.getName(), bidAmount);
                Bukkit.broadcastMessage("§b" + player.getName() + " §9placed a bid for §b$" + bidAmount + "§9!");
                totalBidders++;
            }
        }
    }

    public void removeOnlinePlayerFromAuction(Player player) {
        synchronized (bidders) {
            if (bidders.containsKey(player.getName())) {
                bidders.remove(player.getName());
                totalBidders--;
            }
        }
    }

    public void removeOfflinePlayerFromAuction(OfflinePlayer player) {
        synchronized (bidders) {
            if (bidders.containsKey(player.getName())) {
                bidders.remove(player.getName());
                totalBidders--;
            }
        }
    }

    public boolean doesOnlineWinnerHaveTheMoney(Player player) {
        if (bidders.containsKey(player.getName())) {
            if (plugin.essentials.getUser(player).getMoney() >= getTopBidAmount()) { return true; }
        }

        return false;
    }

    public boolean doesOfflineWinnerHaveTheMoney(OfflinePlayer player) {
        if (bidders.containsKey(player.getName())) {
            if (plugin.essentials.getOfflineUser(player.getName().toLowerCase()).getMoney() >= getTopBidAmount()) { return true; }
        }

        return false;
    }
//  Add/Remove Players from the Auction



//  Store/Retrieve Auctioned Items
    public void storeAuctionItem(Player player) {

        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInHand();

        if (item == null || item.getType() == Material.AIR) return;

        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auction-item.json");
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }

            Writer writer = new FileWriter(file, false);
            gson.toJson(item, writer); // Save the auction item to the json file.
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    public void awardAuctionItem(Player winner) {
        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auction-item.json");
            if (file.exists()) { // Know if the file exits.
                Reader reader = new FileReader(file);
                PlayerInventory inventory = winner.getInventory();
                ItemStack item = gson.fromJson(reader, ItemStack.class); // Grab whatever item is saved.

                if (winner.getInventory().firstEmpty() == -1) { // Inventory is full.
                    winner.getWorld().dropItem(winner.getLocation(), item);
                }
                else {
                    inventory.addItem(item); // Add the item to the player's inventory if it isn't full.
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    public void wipeAuctionFile() {
        try {
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auction-item.json");
            if (file.exists()) {
                Writer writer = new FileWriter(file, false);
                // Write nothing, effectively clearing the json file, this is how to determine if players are in staff mode or not.
                writer.flush();
                writer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    public ItemStack getAuctionItem() {
        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auction-item.json");
            if (file.exists()) { // Know if the file exits.
                Reader reader = new FileReader(file);
                return gson.fromJson(reader, ItemStack.class);

            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

        return null;
    }

    public String getAuctionItemName() {
        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auction-item.json");
            if (file.exists()) { // Know if the file exits.
                Reader reader = new FileReader(file);
                ItemStack item = gson.fromJson(reader, ItemStack.class);

                Material material = item.getType();

                if (material == null) {return null;}

                if (material.isBlock()) { // Wool, Logs, etc.
                    if (material == Material.WOOL) {
                        Wool wool = (Wool) item.getData();

                        return wool.getColor().name() + " WOOL";
                    }

                    if (material == Material.LOG) {
                        MaterialData materialData = item.getData();

                        if (materialData.getData() == 0) {return "OAK LOG";}
                        if (materialData.getData() == 1) {return "SPRUCE LOG";}
                        if (materialData.getData() == 2) {return "BIRCH LOG";}
                        else {return "OAK LOG";}
                    }

                    if (material == Material.LEAVES) {
                        MaterialData materialData = item.getData();

                        if (materialData.getData() == 0) {return "OAK LEAVES";}
                        if (materialData.getData() == 1) {return "SPRUCE LEAVES";}
                        if (materialData.getData() == 2) {return "BIRCH LEAVES";}
                        else {return "OAK LEAVES";}
                    }

                    if (material == Material.STEP) {
                        MaterialData materialData = item.getData();

                        if (materialData.getData() == 0) {return "STONE SLAB";}
                        if (materialData.getData() == 1) {return "SANDSTONE SLAB";}
                        if (materialData.getData() == 2) {return "WOODEN SLAB";}
                        if (materialData.getData() == 3) {return "COBBLESTONE SLAB";}
                        if (materialData.getData() == 4) {return "CRASH SLAB";}
                        else {return "STONE SLAB";}
                    }

                    if (material == Material.SAPLING) {
                        MaterialData materialData = item.getData();

                        if (materialData.getData() == 0) {return "OAK SAPLING";}
                        if (materialData.getData() == 1) {return "SPRUCE SAPLING";}
                        if (materialData.getData() == 2) {return "BIRCH SAPLING";}
                        else {return "OAK SAPLING";}
                    }

                    if (material == Material.LONG_GRASS) {return "WEED";}

                    if (material == Material.GRASS) {return "GRASS BLOCK";}

                    if (material == Material.CLAY) {return "CLAY BLOCK";}

                    if (material == Material.REDSTONE_TORCH_OFF || material == Material.REDSTONE_TORCH_ON) {return "REDSTONE TORCH";}
                    else {
                        return item.getType().name().replaceAll("_", " ");
                    }
                }
                else { // Not a block.
                    if (material == Material.CLAY_BALL) {return "CLAY";}

                    if (material == Material.SULPHUR) {return "GUNPOWDER";}

                    if (material == Material.INK_SACK) {
                        MaterialData materialData = item.getData();

                        if (materialData.getData() == 0) {return "INK SAC";}
                        if (materialData.getData() == 1) {return "ROSE RED";}
                        if (materialData.getData() == 2) {return "CACTUS GREEN";}
                        if (materialData.getData() == 3) {return "COCOA BEANS";}
                        if (materialData.getData() == 4) {return "LAPIS LAZULI";}
                        if (materialData.getData() == 5) {return "PURPLE DYE";}
                        if (materialData.getData() == 6) {return "CYAN DYE";}
                        if (materialData.getData() == 7) {return "LIGHT GRAY BEANS";}
                        if (materialData.getData() == 8) {return "GRAY DYE";}
                        if (materialData.getData() == 9) {return "PINK DYE";}
                        if (materialData.getData() == 10) {return "LIME DYE";}
                        if (materialData.getData() == 11) {return "DANDELION YELLOW";}
                        if (materialData.getData() == 12) {return "LIGHT BLUE DYE";}
                        if (materialData.getData() == 13) {return "MAGENTA DYE";}
                        if (materialData.getData() == 14) {return "ORANGE DYE";}
                        if (materialData.getData() == 15) {return "BONE MEAL";}
                        else {return "INK SAC";}
                    }

                    if (material == Material.FLINT_AND_STEEL) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bFLINT & STEEL"; }
                        else {return "FLINT & STEEL";}
                    }

                    if (material == Material.FISHING_ROD) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bFISHING ROD"; }
                        else {return "FISHING ROD";}
                    }

                    if (material == Material.SHEARS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bSHEARS"; }
                        else {return "SHEARS";}
                    }

                    if (material == Material.DIAMOND_HELMET) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND HELMET"; }
                        else {return "DIAMOND HELMET";}
                    }
                    if (material == Material.IRON_HELMET) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON HELMET"; }
                        else {return "IRON HELMET";}
                    }
                    if (material == Material.CHAINMAIL_HELMET) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bCHAIN HELMET"; }
                        else {return "CHAIN HELMET";}
                    }
                    if (material == Material.GOLD_HELMET) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD HELMET"; }
                        else {return "GOLD HELMET";}
                    }
                    if (material == Material.LEATHER_HELMET) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bLEATHER HELMET"; }
                        else {return "LEATHER HELMET";}
                    }

                    if (material == Material.DIAMOND_CHESTPLATE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND CHESTPLATE"; }
                        else {return "DIAMOND CHESTPLATE";}
                    }
                    if (material == Material.IRON_CHESTPLATE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON CHESTPLATE"; }
                        else {return "IRON CHESTPLATE";}
                    }
                    if (material == Material.CHAINMAIL_CHESTPLATE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bCHAIN CHESTPLATE"; }
                        else {return "CHAIN CHESTPLATE";}
                    }
                    if (material == Material.GOLD_CHESTPLATE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD CHESTPLATE"; }
                        else {return "GOLD CHESTPLATE";}
                    }
                    if (material == Material.LEATHER_CHESTPLATE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bLEATHER CHESTPLATE"; }
                        else {return "LEATHER CHESTPLATE";}
                    }

                    if (material == Material.DIAMOND_LEGGINGS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND LEGGINGS"; }
                        else {return "DIAMOND LEGGINGS";}
                    }
                    if (material == Material.IRON_LEGGINGS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON LEGGINGS"; }
                        else {return "IRON LEGGINGS";}
                    }
                    if (material == Material.CHAINMAIL_LEGGINGS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bCHAIN LEGGINGS"; }
                        else {return "CHAIN LEGGINGS";}
                    }
                    if (material == Material.GOLD_LEGGINGS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD LEGGINGS"; }
                        else {return "GOLD LEGGINGS";}
                    }
                    if (material == Material.LEATHER_LEGGINGS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bLEATHER LEGGINGS"; }
                        else {return "LEATHER LEGGINGS";}
                    }

                    if (material == Material.DIAMOND_BOOTS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND BOOTS"; }
                        else {return "DIAMOND BOOTS";}
                    }
                    if (material == Material.IRON_BOOTS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON BOOTS"; }
                        else {return "IRON BOOTS";}
                    }
                    if (material == Material.CHAINMAIL_BOOTS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bCHAIN BOOTS"; }
                        else {return "CHAIN BOOTS";}
                    }
                    if (material == Material.GOLD_BOOTS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD BOOTS"; }
                        else {return "GOLD BOOTS";}
                    }
                    if (material == Material.LEATHER_BOOTS) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bLEATHER BOOTS"; }
                        else {return "LEATHER BOOTS";}
                    }

                    if (material == Material.DIAMOND_SWORD) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND SWORD"; }
                        else {return "DIAMOND SWORD";}
                    }
                    if (material == Material.IRON_SWORD) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON SWORD"; }
                        else {return "IRON SWORD";}
                    }
                    if (material == Material.GOLD_SWORD) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD SWORD"; }
                        else {return "GOLD SWORD";}
                    }
                    if (material == Material.STONE_SWORD) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bSTONE SWORD"; }
                        else {return "STONE SWORD";}
                    }
                    if (material == Material.WOOD_SWORD) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bWOOD SWORD"; }
                        else {return "WOOD SWORD";}
                    }

                    if (material == Material.DIAMOND_PICKAXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND PICKAXE"; }
                        else {return "DIAMOND PICKAXE";}
                    }
                    if (material == Material.IRON_PICKAXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON PICKAXE"; }
                        else {return "IRON PICKAXE";}
                    }
                    if (material == Material.GOLD_PICKAXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD PICKAXE"; }
                        else {return "GOLD PICKAXE";}
                    }
                    if (material == Material.STONE_PICKAXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bSTONE PICKAXE"; }
                        else {return "STONE PICKAXE";}
                    }
                    if (material == Material.WOOD_PICKAXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bWOOD PICKAXE"; }
                        else {return "WOOD PICKAXE";}
                    }

                    if (material == Material.DIAMOND_AXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND AXE"; }
                        else {return "DIAMOND AXE";}
                    }
                    if (material == Material.IRON_AXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON AXE"; }
                        else {return "IRON AXE";}
                    }
                    if (material == Material.GOLD_AXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD AXE"; }
                        else {return "GOLD AXE";}
                    }
                    if (material == Material.STONE_AXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bSTONE AXE"; }
                        else {return "STONE AXE";}
                    }
                    if (material == Material.WOOD_AXE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bWOOD AXE"; }
                        else {return "WOOD AXE";}
                    }

                    if (material == Material.DIAMOND_SPADE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND SHOVEL"; }
                        else {return "DIAMOND SHOVEL";}
                    }
                    if (material == Material.IRON_SPADE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON SHOVEL"; }
                        else {return "IRON SHOVEL";}
                    }
                    if (material == Material.GOLD_SPADE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD SHOVEL"; }
                        else {return "GOLD SHOVEL";}
                    }
                    if (material == Material.STONE_SPADE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bSTONE SHOVEL"; }
                        else {return "STONE SHOVEL";}
                    }
                    if (material == Material.WOOD_SPADE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bWOOD SHOVEL"; }
                        else {return "WOOD SHOVEL";}
                    }

                    if (material == Material.DIAMOND_HOE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bDIAMOND HOE"; }
                        else {return "DIAMOND HOE";}
                    }
                    if (material == Material.IRON_HOE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bIRON HOE"; }
                        else {return "IRON HOE";}
                    }
                    if (material == Material.GOLD_HOE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bGOLD HOE"; }
                        else {return "GOLD HOE";}
                    }
                    if (material == Material.STONE_HOE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bSTONE HOE"; }
                        else {return "STONE HOE";}
                    }
                    if (material == Material.WOOD_HOE) {
                        double total = material.getMaxDurability();
                        double percentValue = total == 0 ? 0 : (item.getDurability() * 100) / total;
                        String percent = String.format("%.2f%%", percentValue);
                        if (item.getDurability() != 0) { return "§4[§c" + percent + " §4USED]§bWOOD HOE"; }
                        else {return "WOOD HOE";}
                    }

                    return item.getType().name().replaceAll("_", " ");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

        return null;
    }

    public void backupAuctionHostItems(OfflinePlayer player) {
        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auctionhost-backup/" + player.getName().toLowerCase() + ".json");
            File fromLiveFile = new File(plugin.getDataFolder().getAbsolutePath() + "/auction-item.json");

            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }

            if (fromLiveFile.exists()) {
                Reader reader = new FileReader(fromLiveFile);
                Writer writer = new FileWriter(file, false);

                gson.toJson(gson.fromJson(reader, ItemStack.class), writer); // Save the auction item to the json file.
                writer.flush();
                writer.close();
                Bukkit.getServer().getLogger().info("[OSM-Ess] Auction host was offline! The items have been saved and will be returned upon login.");
            }

        } catch (IOException ex) {
            Bukkit.getServer().getLogger().warning("[OSM-Ess] Error whilst saving auction host's items!");
            ex.printStackTrace(System.err);
        }
    }
    public boolean hasHostItemsToReturn(Player player) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auctionhost-backup/" + player.getName().toLowerCase() + ".json");

        if (file.exists()) {
            if (file.length() != 0) { // Determine is there is any sizeable data to read, if not its assumed there's nothing to load, thus they don't have auction hosted items.
                return true;
            }
            else return true;
        }

        return false;
    }
    public void returnAuctionHostItems(Player player) {
        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auctionhost-backup/" + player.getName().toLowerCase() + ".json");
            if (file.exists()) { // Know if the file exits.
                Reader reader = new FileReader(file);
                PlayerInventory inventory = player.getInventory();
                ItemStack savedAuctionItem = gson.fromJson(reader, ItemStack.class); // Grab whatever items are saved to now load.

                if (player.getInventory().firstEmpty() == -1) { // Inventory is full.
                    player.getWorld().dropItem(player.getLocation(), savedAuctionItem);
                }
                else {
                    inventory.addItem(savedAuctionItem);
                }

                reader.close();
                Files.delete(file.toPath());
                Bukkit.getServer().getLogger().info("[OSM-Ess] " + player.getName() + "'s items from hosting a previous auction were returned. File deleted!");

            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    public void backupAuctionWinnerItems(OfflinePlayer player) {
        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auctionwinner-backup/" + player.getName().toLowerCase() + ".json");
            File fromLiveFile = new File(plugin.getDataFolder().getAbsolutePath() + "/auction-item.json");

            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }

            if (fromLiveFile.exists()) {
                Reader reader = new FileReader(fromLiveFile);
                Writer writer = new FileWriter(file, false);

                gson.toJson(gson.fromJson(reader, ItemStack.class), writer); // Save the auction item to the json file.
                writer.flush();
                writer.close();
                Bukkit.getServer().getLogger().info("[OSM-Ess] Auction winner was offline! The items have been saved and will be returned upon login.");
            }

        } catch (IOException ex) {
            Bukkit.getServer().getLogger().warning("[OSM-Ess] Error whilst saving auction winner's items!");
            ex.printStackTrace(System.err);
        }
    }
    public boolean hasAuctionWonItemsToGive(Player player) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auctionwinner-backup/" + player.getName().toLowerCase() + ".json");

        if (file.exists()) {
            if (file.length() != 0) { // Determine is there is any sizeable data to read, if not its assumed there's nothing to load, thus they don't have auction won items.
                return true;
            }
            else return true;
        }

        return false;
    }
    public void giveAuctionWonItems(Player player) {
        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/auctionwinner-backup/" + player.getName().toLowerCase() + ".json");
            if (file.exists()) { // Know if the file exits.
                Reader reader = new FileReader(file);
                PlayerInventory inventory = player.getInventory();
                ItemStack savedAuctionItem = gson.fromJson(reader, ItemStack.class); // Grab whatever items are saved to now load.

                if (player.getInventory().firstEmpty() == -1) { // Inventory is full.
                    player.getWorld().dropItem(player.getLocation(), savedAuctionItem);
                }
                else {
                    inventory.addItem(savedAuctionItem);
                }

                reader.close();
                Files.delete(file.toPath());
                Bukkit.getServer().getLogger().info("[OSM-Ess] " + player.getName() + "'s items from winning a previous auction were given. File deleted!");

            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
//  Store/Retrieve Auctioned Items



//  Getters
    public Double getStartingBid() {
        return startBid;
    }

    public Player getAuctionHost() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (auctionHoster.contains(player.getName())) {
                return Bukkit.getPlayer(player.getName());
            }
        }

        return null;
    }
    public OfflinePlayer getOfflineAuctionHost() {
        for (User user : plugin.essentials.getUserMap().getAllUsers())  {
            if (plugin.playerDataHandler.hasData(user) && auctionHoster.contains(user.getName())) {
                return Bukkit.getOfflinePlayer(user.getName());
            }
        }

        return null;
    }
    public Double getTopBidAmount() {
        if (bidders.isEmpty()) {return (double) 0;}

        Double maxValue = Collections.max(bidders.values());

        return maxValue;
    }

    public Player getTopBidder() {
        if (bidders.isEmpty()) {return null;}

        if (Bukkit.getPlayer(getKeyWithHighestValue(bidders)) != null && Bukkit.getPlayer(getKeyWithHighestValue(bidders)).isOnline()) {
            return Bukkit.getPlayer(getKeyWithHighestValue(bidders));
        }

        return null;
    }

    public OfflinePlayer getOfflineTopBidder() {
        if (bidders.isEmpty()) {return null;}

        return Bukkit.getOfflinePlayer(getKeyWithHighestValue(bidders));
    }


    public static <K, V extends Comparable<V>> K getKeyWithHighestValue(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return null; // Handle empty or null map
        }

        K keyWithHighestValue = null;
        V highestValue = null;

        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (keyWithHighestValue == null || entry.getValue().compareTo(highestValue) > 0) {
                highestValue = entry.getValue();
                keyWithHighestValue = entry.getKey();
            }
        }
        return keyWithHighestValue;
    }

//  Getters

//  Time Util
    public int getAuctionTimeLeft() {
        long timeSinceLastAuctionStart = now() - this.lastStartAuctionVote / 1000;
        int voteDurationSeconds = 60;
        return (int)Math.max(0L, (long)voteDurationSeconds - timeSinceLastAuctionStart);
    }

    public static long now() {
        return System.currentTimeMillis() / 1000L;
    }
    public String formatTime(long seconds) {
        long minute = TimeUnit.SECONDS.toMinutes(seconds);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
        return minute + "m" + second + "s";
    }
//  Time Util
}

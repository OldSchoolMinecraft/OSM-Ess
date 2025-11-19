package com.oldschoolminecraft.OSMEss.Handlers;

import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.google.gson.Gson;
import com.oldschoolminecraft.OSMEss.AuctionStatus;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AuctionHandler {

    public OSMEss plugin;

    private final Map<String, Integer> bidders = Collections.synchronizedMap(new HashMap<>());
    private final ArrayList<String> auctionHoster = new ArrayList<>();

    private long lastStartAuctionVote;

    public int AUCTION_DURATION_TICKS = 20 * 60;
    public int totalBidders = 0;
    public static int startBid;

    public AuctionHandler(OSMEss plugin) {
        this.plugin = plugin;
    }

    public void setAuctionStatus(AuctionStatus auctionStatus) {
        plugin.auctionStatus = auctionStatus;
    }
    public AuctionStatus getAuctionStatus() {return plugin.auctionStatus;}

//  Start/End Auction
    public void startAuction(Player player, int startingBid) {
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("§cYou're not holding anything to auction!");
            return;
        }

        String name = item.getType().name();
        startBid = startingBid;
        int amount = item.getAmount();

        storeAuctionItem(player);
        player.setItemInHand(null);

        auctionHoster.add(player.getName());

        lastStartAuctionVote = System.currentTimeMillis();
        setAuctionStatus(AuctionStatus.ACTIVE);
        plugin.auctionTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::endVote, AUCTION_DURATION_TICKS);

        Bukkit.broadcastMessage("§9Auction started by §b" + player.getName() + "§9!");
        Bukkit.broadcastMessage("§9Auction ends in §b1 minute§9!");
        Bukkit.broadcastMessage("§9Prize: §b" + amount + "x " + name.toUpperCase() + "§9, Starting Bid: §b$" + startingBid);
    }

    public void endVote() {

        if (getAuctionStatus() == AuctionStatus.INACTIVE) return;

        if (totalBidders == 0) { // No one entered the auction, return the item.
            if (getAuctionHost() == null) {
                backupAuctionHostItems(getOfflineAuctionHost());
            }
            else {
                awardAuctionItem(getAuctionHost());
            }

            auctionHoster.clear();
            wipeAuctionFile();

            Bukkit.broadcastMessage("§9Auction ended with no bidders!");
        }
        else { // Auction ended with a bidder.
            //Todo: Check who bid the most and award them the item.

            if (getTopBidder() == null) {
                plugin.essentials.getOfflineUser(getOfflineTopBidder().getName()).takeMoney(getTopBidAmount());
                backupAuctionWinnerItems(getOfflineTopBidder());
            }
            else {
                plugin.essentials.getUser(getTopBidder()).takeMoney(getTopBidAmount());
                awardAuctionItem(getTopBidder());
            }

            if (getAuctionHost() == null) {
                plugin.essentials.getOfflineUser(getOfflineAuctionHost().getName()).giveMoney(getTopBidAmount());
            }
            else {
                plugin.essentials.getUser(getAuctionHost()).giveMoney(getTopBidAmount());
            }

            if (getTopBidder() == null) Bukkit.broadcastMessage("§9Player §b" + getOfflineTopBidder().getName() + " §awon §9the auction for §b" + getAuctionItem().getAmount() + "x " + getAuctionItem().getType().name() + "§9!");
            else Bukkit.broadcastMessage("§9Player §b" + getTopBidder().getName() + " §awon §9the auction for §b" + getAuctionItem().getAmount() + "x " + getAuctionItem().getType().name() + "§9!");
            auctionHoster.clear();
            bidders.clear();
            totalBidders = 0;
            wipeAuctionFile();
        }

        setAuctionStatus(AuctionStatus.INACTIVE);
        if (plugin.auctionTaskId != -1) {Bukkit.getScheduler().cancelTask(plugin.auctionTaskId); plugin.auctionTaskId = -1;}
    }
//  Start/End Auction



//  Add/Remove Players from the Auction
    public void addToAuction(Player player, int bidAmount) {
        if (auctionHoster.contains(player.getName())) { player.sendMessage("§cYou cannot bid on your own auction!"); return;}

        synchronized (bidders) {
            if (bidders.containsKey(player.getName())) { // Re-add them but with a higher bid amount.
//                Integer currentAmount = bidders.get(player.getName());
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

    public void removeFromAuction(Player player) { // Called if they leave the game with a bid placed on an active auction.
        synchronized (bidders) {
            if (bidders.containsKey(player.getName())) {
                bidders.remove(player.getName());
                totalBidders--;
//                Bukkit.broadcastMessage("§b" + player.getName() + " §9left during an active auction!"); // Debug.
            }
        }
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

                wipeAuctionFile();
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
    public Integer getStartingBid() {
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
    public Integer getTopBidAmount() {
        if (bidders.isEmpty()) {return 0;}

        Integer maxValue = Collections.max(bidders.values());

        return maxValue;
    }
    public Player getTopBidder() {
        if (bidders.isEmpty()) {return null;}

        return Bukkit.getPlayer(getKeyWithHighestValue(bidders));
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

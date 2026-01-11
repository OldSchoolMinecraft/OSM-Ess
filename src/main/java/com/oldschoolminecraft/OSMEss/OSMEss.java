package com.oldschoolminecraft.OSMEss;

import com.earth2me.essentials.Essentials;
import com.oldschoolminecraft.OSMEss.Commands.*;
import com.oldschoolminecraft.OSMEss.Handlers.AuctionHandler;
import com.oldschoolminecraft.OSMEss.Handlers.InventoryHandler;
import com.oldschoolminecraft.OSMEss.Handlers.PlayerDataHandler;
import com.oldschoolminecraft.OSMEss.Handlers.PlaytimeHandler;
import com.oldschoolminecraft.OSMEss.Listeners.CommandPreProcessListener;
import com.oldschoolminecraft.OSMEss.Listeners.OSASPoseidonListener;
import com.oldschoolminecraft.OSMEss.Listeners.PlayerConnectionListener;
import com.oldschoolminecraft.OSMEss.Listeners.PlayerWorldListener;
import com.oldschoolminecraft.OSMEss.Util.ColorMessageCFG;
import com.oldschoolminecraft.OSMEss.Util.ConfigSettingCFG;
import com.oldschoolminecraft.OSMEss.Util.WarningsCFG;
import com.oldschoolminecraft.osas.OSAS;
import com.oldschoolminecraft.vanish.Invisiman;
import net.oldschoolminecraft.lmk.Landmarks;
import net.oldschoolminecraft.sd.ScheduledDeath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yi.acru.bukkit.Lockette.Lockette;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OSMEss extends JavaPlugin {

    public Essentials essentials;
    public Invisiman invisiman;
    public Landmarks landmarks;
    public Lockette lockette;
    public OSAS osas;
    public PermissionsEx permissionsEx;
    public ScheduledDeath scheduledDeath;

    public ColorMessageCFG colorMessageCFG;
    public ConfigSettingCFG configSettingCFG;
    public WarningsCFG warningsCFG;

    public AuctionHandler auctionHandler;
    public InventoryHandler inventoryHandler;
    public PlaytimeHandler playtimeHandler;
    public PlayerDataHandler playerDataHandler;

    public java.util.List<java.util.Map.Entry<String, Integer>> cachedTopBalances;
    public java.util.List<java.util.Map.Entry<String, Long>> cachedTopPlaytimes;

    public AuctionStatus auctionStatus;

    public int cacheTaskId = -1;
    public long lastCacheRefreshBalTime = 0;
    public long lastCacheRefreshPTTTime = 0;

    private int index = 0; // For the auto broadcast sequence.

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getPluginManager();

        if (pm.getPlugin("Essentials") != null && pm.isPluginEnabled("Essentials")) {
            essentials = (Essentials) pm.getPlugin("Essentials");
            Bukkit.getServer().getLogger().info("[OSM-Ess] Essentials v" + essentials.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] OSM-Ess requires Essentials to operate!");
            pm.disablePlugin(this);

        }

        if (pm.getPlugin("Invisiman") != null && pm.isPluginEnabled("Invisiman")) {
            invisiman = (Invisiman) pm.getPlugin("Invisiman");
            Bukkit.getServer().getLogger().info("[OSM-Ess] Invisiman v" + invisiman.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Invisiman not found, thus will not hide players from list!");
        }

        if (pm.getPlugin("Landmarks") != null && pm.isPluginEnabled("Landmarks")) {
            landmarks = (Landmarks) pm.getPlugin("Landmarks");
            Bukkit.getServer().getLogger().info("[OSM-Ess] Landmarks v" + landmarks.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Landmarks not found, thus will not listen for landmark signs!");
        }

        if (pm.getPlugin("Lockette") != null && pm.isPluginEnabled("Lockette")) {
            lockette = (Lockette) pm.getPlugin("Lockette");
            Bukkit.getServer().getLogger().info("[OSM-Ess] Lockette v" + lockette.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Lockette not found, thus will not listen for landmark signs!");
        }

        if (pm.getPlugin("OSAS") != null && pm.isPluginEnabled("OSAS")) {
            osas = (OSAS) pm.getPlugin("OSAS");
            Bukkit.getServer().getLogger().info("[OSM-Ess] OSAS v" + osas.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] OSAS not found, thus its features are disabled!");
        }

        if (pm.getPlugin("PermissionsEx") != null && pm.isPluginEnabled("PermissionsEx")) {
            permissionsEx = (PermissionsEx) pm.getPlugin("PermissionsEx");
            Bukkit.getServer().getLogger().info("[OSM-Ess] PermissionsEx v" + permissionsEx.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] PermissionsEx not found, thus its features are disabled!");
        }

        if (pm.getPlugin("ScheduledDeath") != null && pm.isPluginEnabled("ScheduledDeath")) {
            scheduledDeath = (ScheduledDeath) pm.getPlugin("ScheduledDeath");
            Bukkit.getServer().getLogger().info("[OSM-Ess] ScheduledDeath v" + scheduledDeath.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] ScheduledDeath not found, thus its features are disabled!");
        }

        pm.registerEvents(new CommandPreProcessListener(this), this);
        pm.registerEvents(new PlayerConnectionListener(this), this);
        pm.registerEvents(new PlayerWorldListener(this), this);
        pm.registerEvent(Event.Type.CUSTOM_EVENT, new OSASPoseidonListener(this), Event.Priority.Normal, this);

        auctionHandler = new AuctionHandler(this);
        playerDataHandler = new PlayerDataHandler(this);
        playtimeHandler = new PlaytimeHandler(this);
        inventoryHandler = new InventoryHandler(this);
        colorMessageCFG = new ColorMessageCFG(new File(this.getDataFolder().getAbsolutePath(), "color-message-settings.yml"));
        configSettingCFG = new ConfigSettingCFG(new File(this.getDataFolder().getAbsolutePath(), "config.yml"));
        warningsCFG = new WarningsCFG(new File(this.getDataFolder().getAbsolutePath(), "warning-logs.yml"));

        new CommandAuction(this);
        new CommandBaltop(this);
        new CommandBid(this);
        new CommandChatColor(this);
        new CommandConfirmBid(this);
        new CommandDenyBid(this);
        new CommandDiscord(this);
        new CommandForecast(this);
        new CommandHighlightWarp(this);
        new CommandHome(this);
        new CommandHomes(this);
        new CommandIgnoreBC(this);
        new CommandList(this);
        new CommandOSMEss(this);
        new CommandPTT(this);
        new CommandRainbow(this);
        new CommandSeen(this);
        new CommandStaff(this);
        new CommandWarn(this);
        new CommandWarnings(this);
        new CommandWarp(this);
        new CommandWarps(this);

        if (!isAuctionSystemEnabled()) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Auction System: Disabled");
        }
        else {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Auction System: Enabled");
        }

        auctionStatus = AuctionStatus.INACTIVE;

//      Refresh Balance Top 10 & Playtime Top 10
        updateTop10Lists();

        initAutoBC();
    }

    @Override
    public void onDisable() {
        cancelUpdateTop10Lists();
        auctionHandler.endAuction();
    }

    public boolean isInvisimanEnabled() {
        if (Bukkit.getPluginManager().getPlugin("Invisiman") != null && Bukkit.getPluginManager().isPluginEnabled("Invisiman")) return true;
        else return false;
    }

    public boolean isLandmarksEnabled() {
        if (Bukkit.getPluginManager().getPlugin("Landmarks") != null && Bukkit.getPluginManager().isPluginEnabled("Landmarks")) return true;
        else return false;
    }

    public boolean isLocketteEnabled() {
        if (Bukkit.getPluginManager().getPlugin("Lockette") != null && Bukkit.getPluginManager().isPluginEnabled("Lockette")) return true;
        else return false;
    }

    public boolean isOSASEnabled() {
        if (Bukkit.getPluginManager().getPlugin("OSAS") != null && Bukkit.getPluginManager().isPluginEnabled("OSAS")) return true;
        else return false;
    }

    public boolean isPermissionsExEnabled() {
        if (Bukkit.getPluginManager().getPlugin("PermissionsEx") != null && Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) return true;
        else return false;
    }

    public boolean isScheduledDeathEnabled() {
        if (Bukkit.getPluginManager().getPlugin("ScheduledDeath") != null && Bukkit.getPluginManager().isPluginEnabled("ScheduledDeath")) return true;
        else return false;
    }


//  Warning Methods
    public boolean isPlayerInWarningLogs(OfflinePlayer player) {
        if (this.warningsCFG.getConfigOption("Players." + player.getName().toLowerCase()) != null) return true;
        else return false;
    }

    public void addWarning(Player player, String message) {
        try {
            List<String> warnings = this.warningsCFG.getStringList("Players." + player.getName().toLowerCase() + ".Warnings", new ArrayList<>());

            warnings.add(message);
            this.warningsCFG.setProperty("Players." + player.getName().toLowerCase() + ".Warnings", warnings);
            this.warningsCFG.save();
            Bukkit.getServer().getLogger().info("[OSM-Ess] New warning added to " + player.getName() + "'s records!");
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating warning-logs.yml!");
            ex.printStackTrace(System.err);
        }
    }

    public void clearWarnings(OfflinePlayer player) {
        if (isPlayerInWarningLogs(player)) {
            try {
                List<String> warnings = this.warningsCFG.getStringList("Players." + player.getName().toLowerCase() + ".Warnings", new ArrayList<>());
                warnings.clear();
                this.warningsCFG.removeProperty("Players." + player.getName().toLowerCase());
                this.warningsCFG.save();
                Bukkit.getServer().getLogger().info("[OSM-Ess] All warnings cleared from " + player.getName() + "'s records!");
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating warning-logs.yml!");
                ex.printStackTrace(System.err);
            }
        }
    }
//  Warning Methods


//  ChatColor Methods
    public void updateChatColorMessage(Player player, String chatColorCode) {
        try {
            this.colorMessageCFG.setProperty("Players." + player.getName().toLowerCase() + ".Color", chatColorCode);
            this.colorMessageCFG.save();
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating color-message-settings.yml!");
            ex.printStackTrace(System.err);
        }
    }

    public void removeChatColorSetting(Player player) {
        try {
            this.colorMessageCFG.removeProperty("Players." + player.getName().toLowerCase());
            this.colorMessageCFG.save();
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating color-message-settings.yml!");
            ex.printStackTrace(System.err);
        }
    }

    public boolean hasChatColorMessageSet(Player player) {
        if (this.colorMessageCFG.getConfigOption("Players." + player.getName().toLowerCase() + ".Color") != null) return true;
        else return false;
    }

    public String getChatColorMessageSetting(Player player) {
        return this.colorMessageCFG.getString("Players." + player.getName().toLowerCase() + ".Color");
    }
//  ChatColor Methods


// Auction Setting Methods
    public boolean isAuctionSystemEnabled() {
        if (this.configSettingCFG.getConfigOption("Settings.Auction.enabled").equals(true)) return true;
        else return false;
    }

    public void setAllowAuctionSystem(boolean option) {
        try {
            this.configSettingCFG.setProperty("Settings.Auction.enabled", option);
            configSettingCFG.save();
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating config.yml!");
            ex.printStackTrace(System.err);
        }
    }

    public Integer getMinimumRequiredPlaytimeToAuction() {
        return (int) this.configSettingCFG.getConfigOption("Settings.Auction.minPlaytimeToAuction");
    }

    public Integer getMaxAllowedStartingBid() {
        return (int) this.configSettingCFG.getConfigOption("Settings.Auction.maxStartingBid");
    }

    public Integer getPercentageToRequireConfirmation() {
        return (int) this.configSettingCFG.getConfigOption("Settings.Auction.percentageToRequireConfirmation");
    }
// Auction Setting Methods


//  Warp Highlight Methods
    public boolean isWarpNameHighlighted(String warpName) {
        if (this.configSettingCFG.getConfigOption("Warps.Highlight." + warpName.toLowerCase()) != null) return true;
        else return false;
    }

    public boolean isWarpNameHighlightedInRGB1(String warpName) {
        if (isWarpNameHighlighted(warpName) && getWarpNameHighlightColor(warpName).equals("&rgb1")) return true;
        else return false;
    }

    public boolean isWarpNameHighlightedInRGB2(String warpName) {
        if (isWarpNameHighlighted(warpName) && getWarpNameHighlightColor(warpName).equals("&rgb2")) return true;
        else return false;
    }

    public String getWarpNameHighlightColor(String warpName) {
        return this.configSettingCFG.getString("Warps.Highlight." + warpName.toLowerCase());
    }

    public void setWarpNameHighlightColor(String warpName, String chatColorCode) {
        try {
            if (essentials.getWarps().getWarp(warpName) != null) {
                this.configSettingCFG.setProperty("Warps.Highlight." + warpName.toLowerCase(), chatColorCode);
                this.configSettingCFG.save();
            }
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating config.yml!");
            ex.printStackTrace(System.err);
        }
    }

    public void delWarpNameHighlighted(String warpName) {
        if (isWarpNameHighlighted(warpName)) {
            try {
                this.configSettingCFG.removeProperty("Warps.Highlight." + warpName.toLowerCase());
                this.configSettingCFG.save();
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().severe("[OSM-Ess] Error whilst updating config.yml!");
                ex.printStackTrace(System.err);
            }
        }
    }
//  Warp Highlight Methods


    public void initAutoBC() {

        List<String> autoBCMessages = new ArrayList<>();

        autoBCMessages.add("&f[&aOSM&f] &bWe have a ZERO tolerance griefing policy. If it isn't yours, don't touch it!");
        autoBCMessages.add("&f[&aOSM&f] &bYou can do /landmarks to see all the landmarks on the server.");
        autoBCMessages.add("&f[&aOSM&f] &bToggle seeing these messages with /ignorebroadcast!");
        autoBCMessages.add("&f[&aOSM&f] &bUsing /vote day is easier than using beds!");
        autoBCMessages.add("&f[&aOSM&f] &bHave a question? Join our discord or ask a staff member!");
        autoBCMessages.add("&f[&aOSM&f] &bCreepers don't do block damage!");
        autoBCMessages.add("&f[&aOSM&f] &bIf you want to join our discord, do /discord!");
        autoBCMessages.add("&f[&aOSM&f] &bDid you know you can also join with &aoldschoolminecraft.net&b!");
        autoBCMessages.add("&f[&aOSM&f] &bYou should join our subreddit r/OldSchoolMinecraft");

        int perMinute = 5; // 5 Minutes
        long perMinTicks = perMinute * 60 * 20L; // Convert minutes to ticks (20 ticks = 1 second)

        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                if (index >= autoBCMessages.size()) index = 0;

                String autoBCMessage = autoBCMessages.get(index);

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (!playerDataHandler.hasIgnoreBroadcast(all)) {
                        all.sendMessage(ChatColor.translateAlternateColorCodes('&', autoBCMessage));
                    }
                }
                index++;
            }
        }, perMinute, perMinTicks);
    }

    public void updateTop10Lists() {
        refreshBalanceTop();
        refreshPlaytimeTop();

        int refreshMinutes = 60; //1 hour
        long refreshTicks = refreshMinutes * 60 * 20L; // Convert minutes to ticks (20 ticks = 1 second)

        cacheTaskId = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                refreshBalanceTop();
                refreshPlaytimeTop();
            }
        }, refreshTicks, refreshTicks);

        Bukkit.getServer().getLogger().info("[OSM-Ess] Cache refresh task started (every " + refreshMinutes + " minutes)");
    }

    public void cancelUpdateTop10Lists() {
        if (cacheTaskId != -1) {
            this.getServer().getScheduler().cancelTask(cacheTaskId);
            cacheTaskId = -1;
            Bukkit.getServer().getLogger().info("[OSM-Ess] Cache refresh task canceled!");
        }
    }

    public void refreshBalanceTop() {
        java.util.List<java.util.Map.Entry<String, Integer>> topBalances = new java.util.ArrayList<java.util.Map.Entry<String, Integer>>();

        // Get all player data files
        java.io.File essentialsPlayerDataDir = new java.io.File(essentials.getDataFolder().getAbsolutePath(), "userdata");
        if (!essentialsPlayerDataDir.exists()) {
            cachedTopBalances = topBalances;
            lastCacheRefreshBalTime = System.currentTimeMillis();
            return;
        }

        java.io.File[] playerFiles = essentialsPlayerDataDir.listFiles();
        if (playerFiles == null) {
            cachedTopBalances = topBalances;
            lastCacheRefreshBalTime = System.currentTimeMillis();
            return;
        }

        // Read each player's longest streak
        for (java.io.File playerFile : playerFiles) {
            if (playerFile.getName().endsWith(".yml")) {
                String playerName = playerFile.getName().substring(0, playerFile.getName().length() - 4);
                int mostMoney = (int) essentials.getUser(playerName).getMoney();
                if (mostMoney > 0) {
                    topBalances.add(new java.util.AbstractMap.SimpleEntry<>(playerName, mostMoney));
                }
            }
        }

        // Sort by longest streak descending
        java.util.Collections.sort(topBalances, new java.util.Comparator<java.util.Map.Entry<String, Integer>>() {
            public int compare(java.util.Map.Entry<String, Integer> a, java.util.Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        // Update cache
        cachedTopBalances = topBalances;
        lastCacheRefreshBalTime = System.currentTimeMillis();

        Bukkit.getServer().getLogger().info("[OSM-Ess] Balance top cache updated ! (" + topBalances.size() + " players)");
    }

    public void refreshPlaytimeTop() {
        java.util.List<java.util.Map.Entry<String, Long>> topPlaytimes = new java.util.ArrayList<>();

        // Get all player data files
        java.io.File playerDataDir = new java.io.File(getDataFolder().getAbsolutePath(), "player-logs");
        if (!playerDataDir.exists()) {
            cachedTopPlaytimes = topPlaytimes;
            lastCacheRefreshPTTTime = System.currentTimeMillis();
            return;
        }

        java.io.File[] playerFiles = playerDataDir.listFiles();
        if (playerFiles == null) {
            cachedTopPlaytimes = topPlaytimes;
            lastCacheRefreshPTTTime = System.currentTimeMillis();
            return;
        }

        // Read each player's longest streak
        for (java.io.File playerFile : playerFiles) {
            if (playerFile.getName().endsWith(".json")) {
                String playerName = playerFile.getName().substring(0, playerFile.getName().length() - 5);
                long longestPlaytime = playtimeHandler.getTotalPlayTimeInMillis(Bukkit.getOfflinePlayer(playerName));
                if (longestPlaytime > 0) {
                    topPlaytimes.add(new java.util.AbstractMap.SimpleEntry<>(playerName, longestPlaytime));
                }
            }
        }

        // Sort by longest streak descending
        java.util.Collections.sort(topPlaytimes, new java.util.Comparator<java.util.Map.Entry<String, Long>>() {
            public int compare(java.util.Map.Entry<String, Long> a, java.util.Map.Entry<String, Long> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        // Update cache
        cachedTopPlaytimes = topPlaytimes;
        lastCacheRefreshPTTTime = System.currentTimeMillis();

        Bukkit.getServer().getLogger().info("[OSM-Ess] Playtme top cache updated ! (" + topPlaytimes.size() + " players)");
    }


    public String auctionNotEnabled = "§cThe auction system is currently disabled!";
    public String cmdDisabledRestart = "§cCommand is disabled as the server is about to restart!";
    public String errorNeverJoinedEss = "§cError: Player never logged in before. (no Essentials data)";
    public String errorNeverJoinedNoData = "§cError: Player never logged in before.";
    public String invalidPageNum = "§cError: Invalid page number provided.";
    public String invalidNumPara = "§cError: Invalid integer provided.";
    public String noPermission = "§cYou do not have access to that command.";
    public String playerNotFound = "§cError: Player not found.";
    public String warpNotDefined = "§cError: No warps defined.";
}

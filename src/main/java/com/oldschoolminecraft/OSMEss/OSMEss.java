package com.oldschoolminecraft.OSMEss;

import com.earth2me.essentials.Essentials;
import com.oldschoolminecraft.OSMEss.Commands.*;
import com.oldschoolminecraft.OSMEss.Handlers.InventoryHandler;
import com.oldschoolminecraft.OSMEss.Handlers.PlayerDataHandler;
import com.oldschoolminecraft.OSMEss.Handlers.PlaytimeHandler;
import com.oldschoolminecraft.OSMEss.Listeners.CommandPreProcessListener;
import com.oldschoolminecraft.OSMEss.Listeners.LMKSignListener;
import com.oldschoolminecraft.OSMEss.Listeners.PlayerConnectionListener;
import com.oldschoolminecraft.OSMEss.Util.StaffToolsCFG;
import com.oldschoolminecraft.vanish.Invisiman;
import net.oldschoolminecraft.lmk.Landmarks;
import net.oldschoolminecraft.sd.ScheduledDeath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OSMEss extends JavaPlugin {

    public Essentials essentials;
    public Invisiman invisiman;
    public Landmarks landmarks;
    public PermissionsEx permissionsEx;
    public ScheduledDeath scheduledDeath;

    public StaffToolsCFG staffToolsCFG;

    public InventoryHandler inventoryHandler;
    public PlaytimeHandler playtimeHandler;
    public PlayerDataHandler playerDataHandler;

    public java.util.List<java.util.Map.Entry<String, Integer>> cachedTopBalances;
    public java.util.List<java.util.Map.Entry<String, Integer>> cachedTopPlaytimes;

    public int cacheTaskId = -1;
    public long lastCacheRefreshTime = 0;


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
        pm.registerEvents(new LMKSignListener(this), this);
        pm.registerEvents(new PlayerConnectionListener(this), this);

        playerDataHandler = new PlayerDataHandler(this);
        playtimeHandler = new PlaytimeHandler(this);
        inventoryHandler = new InventoryHandler(this);
        staffToolsCFG = new StaffToolsCFG(new File(this.getDataFolder().getAbsolutePath(), "staff-tools.yml"));

        new CommandBaltop(this);
        new CommandIgnoreBC(this);
        new CommandList(this);
        new CommandPing(this);
        new CommandPTT(this);
        new CommandSeen(this);
        new CommandStaff(this);

//      Refresh Balance Top 10 & Playtime Top 10
        updateTop10Lists();

        initAutoBC();
    }

    @Override
    public void onDisable() {
        cancelUpdateTop10Lists();
    }

    public boolean isInvisimanEnabled() {
        if (Bukkit.getPluginManager().getPlugin("Invisiman") != null && Bukkit.getPluginManager().isPluginEnabled("Invisiman")) return true;
        else return false;
    }

    public boolean isLandmarksEnabled() {
        if (Bukkit.getPluginManager().getPlugin("Landmarks") != null && Bukkit.getPluginManager().isPluginEnabled("Landmarks")) return true;
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

    public void initAutoBC() {

        List<String> autoBCMessages = new ArrayList<>();

        autoBCMessages.add("&f[&aOSM&f] &bWe have a ZERO tolerance griefing policy. If it isn't yours, don't touch it!");
        autoBCMessages.add("&f[&aOSM&f] &bYou can do /landmarks to see all the landmarks on the server.");
        autoBCMessages.add("&f[&aOSM&f] &bToggle seeing these messages with /ignorebroadcast!");
        autoBCMessages.add("&f[&aOSM&f] &bUsing /vote day is easier than using beds!");
        autoBCMessages.add("&f[&aOSM&f] &bHave a question? Join our discord or do /warp info!");
        autoBCMessages.add("&f[&aOSM&f] &bCreepers don't do block damage!");
        autoBCMessages.add("&f[&aOSM&f] &bIf you want to join our discord, do /discord!");
        autoBCMessages.add("&f[&aOSM&f] &bDid you know you can also join with &aoldschoolminecraft.net&b!");

        int perMinute = 5; //5 Minutes
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
            lastCacheRefreshTime = System.currentTimeMillis();
            return;
        }

        java.io.File[] playerFiles = essentialsPlayerDataDir.listFiles();
        if (playerFiles == null) {
            cachedTopBalances = topBalances;
            lastCacheRefreshTime = System.currentTimeMillis();
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
        lastCacheRefreshTime = System.currentTimeMillis();

        Bukkit.getServer().getLogger().info("[OSM-Ess] Balance top cache updated ! (" + topBalances.size() + " players)");
    }

    public void refreshPlaytimeTop() {
        java.util.List<java.util.Map.Entry<String, Integer>> topPlaytimes = new java.util.ArrayList<>();

        // Get all player data files
        java.io.File playerDataDir = new java.io.File(this.getDataFolder().getAbsolutePath(), "player-logs");
        if (!playerDataDir.exists()) {
            cachedTopPlaytimes = topPlaytimes;
            lastCacheRefreshTime = System.currentTimeMillis();
            return;
        }

        java.io.File[] playerFiles = playerDataDir.listFiles();
        if (playerFiles == null) {
            cachedTopPlaytimes = topPlaytimes;
            lastCacheRefreshTime = System.currentTimeMillis();
            return;
        }

        // Read each player's longest streak
        for (java.io.File playerFile : playerFiles) {
            if (playerFile.getName().endsWith(".json")) {
                String playerName = playerFile.getName().substring(0, playerFile.getName().length() - 5);
                int longestPlaytime = (int) playtimeHandler.getTotalPlayTimeInMillis(Bukkit.getOfflinePlayer(playerName));
                if (longestPlaytime > 0) {
                    topPlaytimes.add(new java.util.AbstractMap.SimpleEntry<>(playerName, longestPlaytime));
                }
            }
        }

        // Sort by longest streak descending
        java.util.Collections.sort(topPlaytimes, new java.util.Comparator<java.util.Map.Entry<String, Integer>>() {
            public int compare(java.util.Map.Entry<String, Integer> a, java.util.Map.Entry<String, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        // Update cache
        cachedTopPlaytimes = topPlaytimes;
        lastCacheRefreshTime = System.currentTimeMillis();

        Bukkit.getServer().getLogger().info("[OSM-Ess] Playtme top cache updated ! (" + topPlaytimes.size() + " players)");
    }
}


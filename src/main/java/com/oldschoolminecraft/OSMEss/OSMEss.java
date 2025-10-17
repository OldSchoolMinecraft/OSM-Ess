package com.oldschoolminecraft.OSMEss;

import Landmarks.Landmarks;
import com.earth2me.essentials.Essentials;
import com.oldschoolminecraft.OSMEss.Commands.*;
import com.oldschoolminecraft.OSMEss.Handlers.InventoryHandler;
import com.oldschoolminecraft.OSMEss.Handlers.PlayerDataHandler;
import com.oldschoolminecraft.OSMEss.Handlers.PlaytimeHandler;
import com.oldschoolminecraft.OSMEss.Listeners.LMKSignListener;
import com.oldschoolminecraft.OSMEss.Listeners.PlayerBedListener;
import com.oldschoolminecraft.OSMEss.Listeners.PlayerConnectionListener;
import com.oldschoolminecraft.OSMEss.Util.StaffToolsCFG;
import com.oldschoolminecraft.vanish.Invisiman;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;

public class OSMEss extends JavaPlugin {

    public Essentials essentials;
    public Invisiman invisiman;
    public Landmarks landmarks;
    public PermissionsEx permissionsEx;

    public StaffToolsCFG staffToolsCFG;

    public InventoryHandler inventoryHandler;
    public PlaytimeHandler playtimeHandler;
    public PlayerDataHandler playerDataHandler;

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

        pm.registerEvents(new LMKSignListener(this), this);
        pm.registerEvents(new PlayerBedListener(this), this);
        pm.registerEvents(new PlayerConnectionListener(this), this);

        playerDataHandler = new PlayerDataHandler(this);
        playtimeHandler = new PlaytimeHandler(this);
        inventoryHandler = new InventoryHandler(this);
        staffToolsCFG = new StaffToolsCFG(new File(this.getDataFolder().getAbsolutePath(), "staff-tools.yml"));

        new CommandBaltop(this);
        new CommandList(this);
        new CommandPTT(this);
        new CommandSeen(this);
        new CommandStaff(this);
    }

    @Override
    public void onDisable() {}

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
}

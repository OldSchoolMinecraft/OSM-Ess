package com.oldschoolminecraft.OSMEss;

import com.earth2me.essentials.Essentials;
import com.oldschoolminecraft.OSMEss.Commands.*;
import com.oldschoolminecraft.OSMEss.Handlers.InventoryHandler;
import com.oldschoolminecraft.OSMEss.Handlers.PlaytimeHandler;
import com.oldschoolminecraft.OSMEss.Util.StaffToolsCFG;
import com.oldschoolminecraft.vanish.Invisiman;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;

public class OSMEss extends JavaPlugin {

    public Essentials essentials;
    public Invisiman invisiman;
    public PermissionsEx permissionsEx;

    public StaffToolsCFG staffToolsCFG;

    public InventoryHandler inventoryHandler;
    public PlaytimeHandler playtimeHandler;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null && Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            Bukkit.getServer().getLogger().info("[OSM-Ess] Essentials v" + essentials.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] OSM-Ess requires Essentials to operate!");
            Bukkit.getPluginManager().disablePlugin(this);

        }

        if (Bukkit.getPluginManager().getPlugin("PermissionsEx") != null && Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
            permissionsEx = (PermissionsEx) Bukkit.getPluginManager().getPlugin("PermissionsEx");
            Bukkit.getServer().getLogger().info("[OSM-Ess] PermissionsEx v" + permissionsEx.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] PermissionsEx not found, thus its features are disabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("Invisiman") != null && Bukkit.getPluginManager().isPluginEnabled("Invisiman")) {
            invisiman = (Invisiman) Bukkit.getPluginManager().getPlugin("Invisiman");
            Bukkit.getServer().getLogger().info("[OSM-Ess] Invisiman v" + invisiman.getDescription().getVersion() + " found!");
        }
        else {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Invisiman not found, thus will not hide players from list!");
        }

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

    public boolean isPermissionsExEnabled() {
        if (Bukkit.getPluginManager().getPlugin("PermissionsEx") != null && Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) return true;
        else return false;
    }
}

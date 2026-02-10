package com.oldschoolminecraft.OSMEss.Util;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class ConfigSettingCFG extends Configuration {
    public ConfigSettingCFG(File file) {
        super(file);
        this.reload();
    }

    public void reload() {
        this.load();
        this.write();
        this.save();
    }

    public void write() {
        // Auction Settings
        generateConfigOption("Settings.Auction.enabled", true);
        generateConfigOption("Settings.Auction.minPlaytimeToAuction", 43200000);
        generateConfigOption("Settings.Auction.maxStartingBid", 10000);
        generateConfigOption("Settings.Auction.percentageToRequireConfirmation", 5); // 5%

        // Fish Treasure Settings
        generateConfigOption("Settings.FishTreasure.enabled", true);
        generateConfigOption("Settings.FishTreasure.chanceForTreasure", 2.5); // 2.5%

        // Staff Tool Settings
        generateConfigOption("Settings.StaffTools.slot1", 271);
        generateConfigOption("Settings.StaffTools.slot2", 345);
        generateConfigOption("Settings.StaffTools.slot3", 7);
        generateConfigOption("Settings.StaffTools.slot4", 287);

        //Warp Highlight Settings
        generateConfigOption("Warps.Highlight", null);

        generateConfigOption("ExplosiveArrows.enabled", true);
        generateConfigOption("ExplosiveArrows.blastRadius", 3);
        generateConfigOption("ExplosiveArrows.disallowedPlayers", null);

    }

    private void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }

        Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public Object getConfigOption(String key, Object defaultValue) {
        Object value = this.getConfigOption(key);
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }
}

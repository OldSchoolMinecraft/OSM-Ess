package com.oldschoolminecraft.OSMEss.Handlers;

import com.google.gson.Gson;
import com.oldschoolminecraft.OSMEss.OSMEss;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class InventoryHandler {

    public OSMEss plugin;

    public InventoryHandler(OSMEss plugin) {
        this.plugin = plugin;
    }

    public void saveInventory(Player player) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();

        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        items.addAll(Arrays.asList(contents));

        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/staff-inv-log/" + player.getName().toLowerCase() + ".json");
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }

            Writer writer = new FileWriter(file, false);
            gson.toJson(items, writer); // Save player's inventory to the json file.
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadSavedInventory(Player player) {
        try {
            Gson gson = new Gson();
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/staff-inv-log/" + player.getName().toLowerCase() + ".json");
            if (file.exists()) { // Know if the file exits.
                Reader reader = new FileReader(file);
                PlayerInventory inventory = player.getInventory();
                ItemStack[] contents = gson.fromJson(reader, ItemStack[].class); // Grab whatever items are saved to now load.

                inventory.clear();
                inventory.setContents(contents); // Load the last saved inventory data from the json file.
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean hasSavedInventory(Player player) {
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/staff-inv-log/" + player.getName().toLowerCase() + ".json");

        if (file.exists()) {
            if (file.length() != 0) { // Determine is there is any sizeable data to read, if not its assumed there's nothing to load, thus they're not in staff mode.
                return true;
            }
        }

        return false;
    }

    public void wipeSavedInventory(Player player) {
        try {
            File file = new File(plugin.getDataFolder().getAbsolutePath() + "/staff-inv-log/" + player.getName().toLowerCase() + ".json");
            if (file.exists()) {
                Writer writer = new FileWriter(file, false);
                // Write nothing, effectively clearing the json file, this is how to determine if players are in staff mode or not.
                writer.flush();
                writer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void giveStaffTools(Player player) {
        PlayerInventory inventory = player.getInventory();


        Material materialSlot1 = Material.matchMaterial(plugin.configSettingCFG.getString("Settings.StaffTools.slot1"));
        Material materialSlot2 = Material.matchMaterial(plugin.configSettingCFG.getString("Settings.StaffTools.slot2"));
        Material materialSlot3 = Material.matchMaterial(plugin.configSettingCFG.getString("Settings.StaffTools.slot3"));
        Material materialSlot4 = Material.matchMaterial(plugin.configSettingCFG.getString("Settings.StaffTools.slot4"));

        if (materialSlot1 == null || materialSlot1 == Material.AIR) return;
        else inventory.addItem(new ItemStack(materialSlot1, 1));
        if (materialSlot2 == null || materialSlot2 == Material.AIR) return;
        else inventory.addItem(new ItemStack(materialSlot2, 1));
        if (materialSlot3 == null || materialSlot3 == Material.AIR) return;
        else inventory.addItem(new ItemStack(materialSlot3, 1));
        if (materialSlot4 == null || materialSlot4 == Material.AIR) return;
        else inventory.addItem(new ItemStack(materialSlot4, 1));
    }
}

package com.oldschoolminecraft.OSMEss.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.oldschoolminecraft.OSMEss.OSMEss;
import com.oldschoolminecraft.OSMEss.compat.PartyUserData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.oldschoolminecraft.OSMEss.Commands.CommandSeen.getPlayerTimeZone;
import static net.oldschoolminecraft.OSMSG.Core.TAG;

public class PartyDataHandler {
    public OSMEss plugin;
    public static File PARTY_DATA_DIR;

    public PartyDataHandler(OSMEss plugin) {
        this.plugin = plugin;
        PARTY_DATA_DIR = new File(plugin.getDataFolder().getAbsolutePath(), "party-logs");
    }

    public void createNewParty(Player owner, String partyName) {
        try {
            if (!PARTY_DATA_DIR.exists()) {PARTY_DATA_DIR.mkdirs();}

            File partyFile = new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("partyName", partyName);
            jsonObject.put("partyOwner", owner.getName().toLowerCase());
            jsonObject.put("dateCreated", System.currentTimeMillis());
            jsonObject.put("partyHomeLocation", null);
            jsonObject.put("partyMembers", null);

            Writer writer = new FileWriter(partyFile, false);
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

            Bukkit.getServer().getLogger().info("[OSM-Ess] Created & saved data for '" + owner.getName() + "'s party! ' (Filename: " + partyName.toLowerCase() + ".json)");
        } catch (IOException ex) {
            Bukkit.getServer().getLogger().info("[OSM-Ess] Error creating party '" + partyName.toLowerCase() + "' for " + owner.getName() + ": " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }
    public void nukeParty(Player owner, String partyName) {
        if (doesPartyExist(partyName) && isPartyOwner(owner, partyName)) {
            try {
                File partyFile = new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json");

                Writer writer = new FileWriter(partyFile, false);
                // Write nothing, effectively clearing the json file.
                writer.flush();
                writer.close();

                Bukkit.getServer().getLogger().info("[OSM-Ess] Deleted '" + owner.getName() + "'s party data titled '" + partyName.toLowerCase() + "'!");
            } catch (IOException ex) {
                Bukkit.getServer().getLogger().info("[OSM-Ess] Error deleting party '" + partyName.toLowerCase() + "' for " + owner.getName() + ": " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
    }
    public boolean doesPartyExist(String partyName) {
        File partyFile = new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json");

        if (partyFile.exists()) {return true;}
        else {return false;}
    }
    public boolean isPartyOwner(Player owner, String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);

                if (data.partyOwner.equals(owner.getName().toLowerCase())) {return true;}
                else {return false;}
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return false;
            }
        }

        return false;
    }
    public String getPartyPlayerIsIn(Player player) {
        if (isInParty(player)) {
            if (!PARTY_DATA_DIR.exists() || !PARTY_DATA_DIR.isDirectory()) return null;

            Gson gson = new Gson();

            String filePartyName = getFileNameContainingPlayer(PARTY_DATA_DIR, player.getName().toLowerCase(), "partyMembers");

            if (filePartyName != null) {
                String partyName = filePartyName.replace(".json", "");

                return partyName;
            }

            return "N/A";
        }

        return null;
    }
    public String getPartyName(String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                return data.partyName;
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return "N/A";
            }
        }

        return null;
    }
    public String getPartyOwner(String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                return data.partyOwner;
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return "N/A";
            }
        }

        return null;
    }
    public String getPartyDatedCreated(String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                long dateCreatedMillis = data.dateCreated;
                ZoneId zone = ZoneOffset.UTC;
                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateCreatedMillis), zone);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
                return dateTime.atZone(zone).format(formatter);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return "N/A";
            }
        }

        return null;
    }
    public String getPartyDatedCreatedByTimeZone(String partyName, CommandSender sender) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                long dateCreatedMillis = data.dateCreated;

                ZoneId zone = ZoneId.of(getPlayerTimeZone(sender));
                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateCreatedMillis), zone);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").withZone(zone);
                return dateTime.atZone(zone).format(formatter);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return "N/A";
            }
        }

        return null;
    }

    public void inviteToParty(String partyName, Player playerToInvite) {}

    public void addToParty(String partyName, Player playerToAdd) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                JSONObject jsonObject = new JSONObject();

                //Retain
                String partyNameRETAIN = data.partyName;
                String partyOwnerRETAIN = data.partyOwner;
                long dateCreatedRETAIN = data.dateCreated;
                Location partyHomeLocationRETAIN = data.partyHomeLocation;

                //Values to calculate updates
                List<String> partyMembers = data.partyMembers;
                if (!partyMembers.contains(playerToAdd.getName().toLowerCase())) {partyMembers.add(playerToAdd.getName().toLowerCase());}

                //Update
                jsonObject.put("partyName", partyNameRETAIN);
                jsonObject.put("partyOwner", partyOwnerRETAIN);
                jsonObject.put("dateCreated", dateCreatedRETAIN);
                jsonObject.put("partyHomeLocation", partyHomeLocationRETAIN);
                jsonObject.put("partyMembers", partyMembers);

                FileWriter writer = new FileWriter(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"));
                writer.write(jsonObject.toJSONString());
                writer.close();

                sendPartyChatMessage(partyName, "§d" + playerToAdd.getName() + " joined.");
                Bukkit.getServer().getLogger().info("[OSM-Ess] Added '" + playerToAdd.getName() + " to party '" + partyName.toLowerCase() + "'!");
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().info("[OSM-Ess] Error adding '" + playerToAdd.getName() + " to party '" + partyName.toLowerCase() + "': " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
    }
    public void removeFromParty(String partyName, Player playerToRemove) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                JSONObject jsonObject = new JSONObject();

                //Retain
                String partyNameRETAIN = data.partyName;
                String partyOwnerRETAIN = data.partyOwner;
                long dateCreatedRETAIN = data.dateCreated;
                Location partyHomeLocationRETAIN = data.partyHomeLocation;

                //Values to calculate updates
                List<String> partyMembers = data.partyMembers;
                if (partyMembers.contains(playerToRemove.getName().toLowerCase())) {partyMembers.remove(playerToRemove.getName().toLowerCase());}

                //Update
                jsonObject.put("partyName", partyNameRETAIN);
                jsonObject.put("partyOwner", partyOwnerRETAIN);
                jsonObject.put("dateCreated", dateCreatedRETAIN);
                jsonObject.put("partyHomeLocation", partyHomeLocationRETAIN);
                jsonObject.put("partyMembers", partyMembers);

                FileWriter writer = new FileWriter(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"));
                writer.write(jsonObject.toJSONString());
                writer.close();

                sendPartyChatMessage(partyName, "§d" + playerToRemove.getName() + " left.");
                Bukkit.getServer().getLogger().info("[OSM-Ess] Removed '" + playerToRemove.getName() + " from party '" + partyName.toLowerCase() + "'!");

            } catch (Exception ex) {
                Bukkit.getServer().getLogger().info("[OSM-Ess] Error removing '" + playerToRemove.getName() + " from party '" + partyName.toLowerCase() + "': " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
    }
    public boolean isInParty(Player playerToCheck) {
        if (!PARTY_DATA_DIR.exists() || !PARTY_DATA_DIR.isDirectory()) return false;

        boolean found = searchForKeyInList(PARTY_DATA_DIR, playerToCheck.getName().toLowerCase(), "partyMembers");

        if (found) {return true;}
        else {return false;}
    }
    public boolean isInParty(Player playerToCheck, String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);

                List<String> partyMembers = data.partyMembers;

                if (partyMembers.contains(playerToCheck.getName().toLowerCase())) {return true;}
                else {return false;}
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        return false;
    }
    public void sendPartyChatMessage(String partyName, CommandSender sender, String message) {
        if (doesPartyExist(partyName)) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isInParty(all, partyName)) {
                    all.sendMessage("§5[PARTY] §b" + sender.getName() + "§8: §f" + message);
                    all.playEffect(all.getLocation(), Effect.CLICK2, 1);
                }
            }
        }
    }
    public void sendPartyChatMessage(String partyName, String message) {
        if (doesPartyExist(partyName)) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isInParty(all, partyName)) {
                    all.sendMessage("§5[PARTY] " + message);
                    all.playEffect(all.getLocation(), Effect.CLICK2, 1);
                }
            }
        }
    }

    public void teleportPartyMemberToAnother(String partyName, Player from, Player to) {if (doesPartyExist(partyName)) {}}

    public void setPartyHome(String partyName, Player player) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                JSONObject jsonObject = new JSONObject();

                //Retain
                String partyNameRETAIN = data.partyName;
                String partyOwnerRETAIN = data.partyOwner;
                long dateCreatedRETAIN = data.dateCreated;
                List<String> partyMembersRETAIN = data.partyMembers;

                //Values to calculate updates
                World world = player.getWorld();
                int x = player.getLocation().getBlockX();
                int y = player.getLocation().getBlockY();
                int z = player.getLocation().getBlockZ();

                Location location = new Location(world, x, y, z);

                //Update
                jsonObject.put("partyName", partyNameRETAIN);
                jsonObject.put("partyOwner", partyOwnerRETAIN);
                jsonObject.put("dateCreated", dateCreatedRETAIN);
                jsonObject.put("partyHomeLocation", location);
                jsonObject.put("partyMembers", partyMembersRETAIN);

                FileWriter writer = new FileWriter(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"));
                writer.write(jsonObject.toJSONString());
                writer.close();

                Bukkit.getServer().getLogger().info("[OSM-Ess] Home location for party '" + partyName.toLowerCase() + " set @ " + x + y + z + ".");
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().info("[OSM-Ess] Error saving home location for party '" + partyName.toLowerCase() + ": " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
    }
    public void delPartyHome(String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                JSONObject jsonObject = new JSONObject();

                //Retain
                String partyNameRETAIN = data.partyName;
                String partyOwnerRETAIN = data.partyOwner;
                long dateCreatedRETAIN = data.dateCreated;
                List<String> partyMembersRETAIN = data.partyMembers;

                //Update
                jsonObject.put("partyName", partyNameRETAIN);
                jsonObject.put("partyOwner", partyOwnerRETAIN);
                jsonObject.put("dateCreated", dateCreatedRETAIN);
                jsonObject.put("partyHomeLocation", null);
                jsonObject.put("partyMembers", partyMembersRETAIN);

                FileWriter writer = new FileWriter(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"));
                writer.write(jsonObject.toJSONString());
                writer.close();

                Bukkit.getServer().getLogger().info("[OSM-Ess] Home location for party '" + partyName.toLowerCase() + " deleted.");
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().info("[OSM-Ess] Error deleting home location for party '" + partyName.toLowerCase() + ": " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
    }
    public void teleportToPartyHome(String partyName, Player playerToTeleport) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);

                Location partyHomeLocation = data.partyHomeLocation;

                playerToTeleport.teleport(getSafeDestination(partyHomeLocation));
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }


    /* String Search Handling Stuff */
    public boolean searchForKeyInList(File directory, String searchString, String listKey) {
        if (!directory.exists() || !directory.isDirectory()) {
            return false;
        }

        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .anyMatch(path -> fileContainsStringInList(path.toFile(), searchString, listKey));
        } catch (IOException e) {
            Bukkit.getLogger().severe("[OSM-Ess] Error walking through JSON files: " + e.getMessage());
        }
        return false;
    }
    private boolean fileContainsStringInList(File file, String searchString, String listKey) {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(file)) {
            // Parse JSON into a generic Map
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> jsonMap = gson.fromJson(reader, type);

            if (jsonMap != null && jsonMap.containsKey(listKey)) {
                // Gson parses JSON arrays as List<Object> or List<String>
                Object listObject = jsonMap.get(listKey);

                if (listObject instanceof List) {
                    List<?> stringList = (List<?>) listObject;

                    // Check if the list contains your target string
                    for (Object item : stringList) {
                        if (searchString.equals(item)) { // or item.toString().equals(...)
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[OSM-Ess] Failed to read/parse JSON file: " + file.getName());
        }
        return false;
    }
    public String getFileNameContainingPlayer(File directory, String searchString, String listKey) {
        if (!directory.exists() || !directory.isDirectory()) {
            return null;
        }

        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            // Find the first file that contains the target string
            Optional<Path> foundFile = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .filter(path -> fileContainsStringInList(path.toFile(), searchString, listKey))
                    .findFirst(); // Stops processing subsequent files immediately upon match

            // If a file was found, return its name. Otherwise, return null.
            return foundFile.map(path -> path.getFileName().toString()).orElse(null);

        } catch (IOException e) {
            Bukkit.getLogger().severe("Error scanning party files: " + e.getMessage());
        }
        return null;
    }
    /* String Search Handling Stuff */


    /* Teleportation Handling Stuff */
    public Location getSafeDestination(Location loc) throws Exception {
        if ((loc == null) || (loc.getWorld() == null)) {
            throw new Exception(TAG + "§cError: Destination unknown.");
        }
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        while (isBlockAboveAir(world, x, y, z)) {
            y--;
            if (y < 0) {
                break;
            }
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y++;
            if (y >= 127) {
                x++;
            }
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y--;
            if (y <= 1) {
                y = 127;
                x++;
                if (x - 32 > loc.getBlockX()) {
                    throw new Exception("§cError: Hole in floor.");
                }
            }
        }
        return new Location(world, x + 0.5D, y, z + 0.5D, loc.getYaw(), loc.getPitch());
    }

    private boolean isBlockAboveAir(World world, int x, int y, int z) {
        return world.getBlockAt(x, y - 1, z).getType() == Material.AIR;
    }

    public boolean isBlockUnsafe(World world, int x, int y, int z) {
        Block below = world.getBlockAt(x, y - 1, z);
        if ((below.getType() == Material.LAVA) || (below.getType() == Material.STATIONARY_LAVA)) {
            return true;
        }
        if (below.getType() == Material.FIRE) {
            return true;
        }

        return isBlockAboveAir(world, x, y, z);
    }
    /* Teleportation Handling Stuff */
}

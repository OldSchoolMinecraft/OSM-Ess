package com.oldschoolminecraft.OSMEss.Handlers;

import com.google.gson.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            jsonObject.put("partyHomeLocation", "none");
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
    public boolean isPartyOwner(OfflinePlayer owner, String partyName) {
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
    public String getPartyPlayerIsIn(OfflinePlayer player) {
        if (!PARTY_DATA_DIR.exists() || !PARTY_DATA_DIR.isDirectory()) {return null;}

        if (isInParty(player)) {

            String filePartyName1 = getPartyPlayerIsInAsOwner(PARTY_DATA_DIR, player.getName().toLowerCase());
            String filePartyName2 = getPartyPlayerIsInAsMember(PARTY_DATA_DIR, player.getName().toLowerCase());

            if (filePartyName1 != null) {
                String partyName = filePartyName1.replace(".json", "");
                return partyName;
            }

            else if (filePartyName2 != null) {
                String partyName = filePartyName2.replace(".json", "");
                return partyName;
            }

            return "NtApl";
        }

        return null;
    }
    public String getPartyPlayerIsInAsOwner(File directory, String playerName) {
        if (!directory.exists() || !directory.isDirectory()) {
            return null;
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                    if (jsonObject.has("partyOwner") && !jsonObject.get("partyOwner").isJsonNull()) {
                        String owner = jsonObject.get("partyOwner").getAsString();

                        if (owner.equalsIgnoreCase(playerName)) {
                            return file.getName(); // Returns the file name (e.g., "alpha_party.json")
                        }
                    }
                } catch (IOException | JsonSyntaxException e) {
                    Bukkit.getServer().getLogger().warning("[OSM-Ess] Could not read party file: " + file.getName());
                }
            }
        }

        return "NULL"; // Return null if no files contain this party owner
    }
    public String getPartyPlayerIsInAsMember(File directory, String playerName) {
        if (!directory.exists() || !directory.isDirectory()) {
            return null;
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                    // Check if "partyMembers" exists and is a JSON array (String list)
                    if (jsonObject.has("partyMembers") && jsonObject.get("partyMembers").isJsonArray()) {
                        JsonArray membersArray = jsonObject.getAsJsonArray("partyMembers");

                        // Loop through the list of members in this specific file
                        for (JsonElement element : membersArray) {
                            if (!element.isJsonNull() && element.getAsString().equalsIgnoreCase(playerName)) {
                                return file.getName(); // Found the member, return the file name immediately
                            }
                        }
                    }
                } catch (IOException | JsonSyntaxException e) {
                    Bukkit.getServer().getLogger().warning("[OSM-Ess] Could not read party file: " + file.getName());
                }
            }
        }

        return "NULL"; // Return null if the player is not a member of any party
    }

    public String getPartyName(String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                return data.partyName;
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return "NA";
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
                return "NA";
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
                return "NA";
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
                return "NA";
            }
        }

        return null;
    }
    public List<String> getPartyMembers(File directory, String fileName) {
        List<String> members = new ArrayList<>();
        File file = new File(directory, fileName);

        // Verify the file exists before attempting to read it
        if (!file.exists()) {
            return members;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            // Ensure the key exists and is actually a JSON Array
            if (jsonObject.has("partyMembers") && jsonObject.get("partyMembers").isJsonArray()) {
                JsonArray membersArray = jsonObject.getAsJsonArray("partyMembers");

                // Convert Gson JsonArray elements to Java Strings
                for (JsonElement element : membersArray) {
                    if (!element.isJsonNull()) {
                        members.add(element.getAsString());
                    }
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            Bukkit.getServer().getLogger().severe("[OSM-Ess] Could not read party members from file: " + fileName);
        }

        return members;
    }

    public void inviteToParty(String partyName, Player playerToInvite) {}

    public void addToParty(String partyName, OfflinePlayer playerToAdd) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                JSONObject jsonObject = new JSONObject();

                //Retain
                String partyNameRETAIN = data.partyName;
                String partyOwnerRETAIN = data.partyOwner;
                long dateCreatedRETAIN = data.dateCreated;
                String partyHomeLocationRETAIN = data.partyHomeLocation;

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
    public void removeFromParty(String partyName, OfflinePlayer playerToRemove) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);
                JSONObject jsonObject = new JSONObject();

                //Retain
                String partyNameRETAIN = data.partyName;
                String partyOwnerRETAIN = data.partyOwner;
                long dateCreatedRETAIN = data.dateCreated;
                String partyHomeLocationRETAIN = data.partyHomeLocation;

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
    public boolean isInParty(OfflinePlayer playerToCheck) {
        if (!PARTY_DATA_DIR.exists() || !PARTY_DATA_DIR.isDirectory()) return false;

        boolean foundAsMember = isPlayerPartyMember(PARTY_DATA_DIR, playerToCheck.getName().toLowerCase(), "partyMembers");
        boolean foundAsOwner = isPlayerPartyOwner(PARTY_DATA_DIR, playerToCheck.getName().toLowerCase());

        if (foundAsMember) {return true;}
        else if (foundAsOwner) {return true;}
        else {return false;}
    }
    public boolean isInParty(OfflinePlayer playerToCheck, String partyName) {
        if (doesPartyExist(partyName)) {
            String partyOwner = getPartyOwner(partyName);
            List<String> partyMembers = getPartyMembers(PARTY_DATA_DIR, partyName);

            if (partyMembers.contains(playerToCheck.getName().toLowerCase()) || partyOwner.contains(playerToCheck.getName().toLowerCase())) {return true;}
            else {return false;}
        }

        return false;
    }
    public void sendPartyChatMessage(String partyName, CommandSender sender, String message) {
        if (doesPartyExist(partyName)) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isInParty(all, partyName)) {
                    all.sendMessage("§5[PARTY] §b" + sender.getName() + "§8: §f" + message);
                    all.playEffect(all.getLocation(), Effect.CLICK1, 1);
                }
            }
        }
    }
    public void sendPartyChatMessage(String partyName, String message) {
        if (doesPartyExist(partyName)) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isInParty(all, partyName)) {
                    all.sendMessage("§5[PARTY] " + message);
                    all.playEffect(all.getLocation(), Effect.CLICK1, 1);
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
                String formattedCoords = player.getWorld().getName() + ":" +
                        player.getLocation().getBlockX() + ":" +
                        player.getLocation().getBlockY() + ":" +
                        player.getLocation().getBlockZ() + ":" +
                        player.getLocation().getYaw() + ":" +
                        player.getLocation().getPitch();

                data.partyHomeLocation = formattedCoords;

                //Update
                jsonObject.put("partyName", partyNameRETAIN);
                jsonObject.put("partyOwner", partyOwnerRETAIN);
                jsonObject.put("dateCreated", dateCreatedRETAIN);
                jsonObject.put("partyHomeLocation", data.partyHomeLocation);
                jsonObject.put("partyMembers", partyMembersRETAIN);


                FileWriter writer = new FileWriter(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"));
                PartyUserData.gson.toJson(jsonObject, writer);
                writer.close();

                Bukkit.getServer().getLogger().info("[OSM-Ess] Home location for party '" + partyName.toLowerCase() + "' set in " + player.getWorld().getName() + " @ " + player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + " " + player.getLocation().getBlockZ() + ".");
                sendPartyChatMessage(partyName, "§d" + player.getName() + " has set/updated the party home location.");
                sendPartyChatMessage(partyName, "§dNew Location in §b" + player.getWorld().getName() + " §d@ §b" + player.getLocation().getBlockX() + "§d, §b" + player.getLocation().getBlockY() + "§d, §b" + player.getLocation().getBlockZ() + "§d.");
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().severe("[OSM-Ess] Error saving home location for party '" + partyName.toLowerCase() + ": " + ex.getMessage());
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

                data.partyHomeLocation = "none";

                //Update
                jsonObject.put("partyName", partyNameRETAIN);
                jsonObject.put("partyOwner", partyOwnerRETAIN);
                jsonObject.put("dateCreated", dateCreatedRETAIN);
                jsonObject.put("partyHomeLocation", data.partyHomeLocation);
                jsonObject.put("partyMembers", partyMembersRETAIN);

                FileWriter writer = new FileWriter(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"));
                writer.write(jsonObject.toJSONString());
                writer.close();

                Bukkit.getServer().getLogger().info("[OSM-Ess] Home location for party '" + partyName.toLowerCase() + " deleted.");
                sendPartyChatMessage(partyName, "§dThe party's home location has been §cdeleted§d.");
            } catch (Exception ex) {
                Bukkit.getServer().getLogger().severe("[OSM-Ess] Error deleting home location for party '" + partyName.toLowerCase() + ": " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
    }
    public boolean isPartyHomeDeleted(String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                PartyUserData data = PartyUserData.gson.fromJson(reader, PartyUserData.class);

                if (data.partyHomeLocation.equals("none")) {return true;}
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        return false;
    }
    public String getPartyHomeLocation(String partyName) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> dataMap = PartyUserData.gson.fromJson(reader, type);

                if (dataMap == null || !dataMap.containsKey("partyHomeLocation")) {
                    return "§4(§cError: Location data null§4)";
                }

                if (dataMap.get("partyHomeLocation").equals("none")) {
                    return "§4(§cLocation Not Set Yet§4)";
                }

                String rawLocation = dataMap.get("partyHomeLocation");
                String[] parts = rawLocation.split(":");

                if (parts.length < 6) {
                    Bukkit.getServer().getLogger().severe("[OSM-Ess] Malformed location string in JSON!");
                    return "§4(§cError: Malformed location string§4)";
                }

                String worldName = parts[0];
                int x         = Integer.parseInt(parts[1]);
                int y         = Integer.parseInt(parts[2]);
                int z         = Integer.parseInt(parts[3]);
                float yaw     = Float.parseFloat(parts[4]);
                float pitch   = Float.parseFloat(parts[5]);

                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    Bukkit.getServer().getLogger().severe("[OSM-Ess] World '" + worldName + "' is not loaded!");
                    return "§4(§cError: World is null§4)";
                }

                Location location = new Location(world, x, y, z, yaw, pitch);

                return "§6(§e" + location.getWorld().getName() + "§6, §e" + location.getBlockX() + "§6, §e" + location.getBlockY() + "§6, §e" + location.getBlockZ() + "§6)";
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                return "§4(§cError: Reader failed.§4)";
            }
        }

        return "§4(§cError: Party is null.§4)";
    }
    public void teleportToPartyHome(String partyName, Player playerToTeleport) {
        if (doesPartyExist(partyName)) {
            try (FileReader reader = new FileReader(new File(PARTY_DATA_DIR, partyName.toLowerCase() + ".json"))) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> dataMap = PartyUserData.gson.fromJson(reader, type);

                if (dataMap == null || !dataMap.containsKey("partyHomeLocation")) {
                    playerToTeleport.sendMessage("§5[PARTY] §cTeleport failed: Location data missing/null.");
                    return;
                }

                if (dataMap.get("partyHomeLocation").equals("none")) {
                    playerToTeleport.sendMessage("§5[PARTY] §cTeleport failed: Location not set yet.");
                    return;
                }

                String rawLocation = dataMap.get("partyHomeLocation");
                String[] parts = rawLocation.split(":");

                if (parts.length < 6) {
                    Bukkit.getServer().getLogger().severe("[OSM-Ess] Malformed location string in JSON!");
                    playerToTeleport.sendMessage("§5[PARTY] §cTeleport failed: Malformed location string.");
                    return;
                }

                String worldName = parts[0];
                int x         = Integer.parseInt(parts[1]);
                int y         = Integer.parseInt(parts[2]);
                int z         = Integer.parseInt(parts[3]);
                float yaw     = Float.parseFloat(parts[4]);
                float pitch   = Float.parseFloat(parts[5]);

                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    Bukkit.getServer().getLogger().severe("[OSM-Ess] World '" + worldName + "' is not loaded!");
                    playerToTeleport.sendMessage("§5[PARTY] §cTeleport failed: World is null.");
                    return;
                }

                Location location = new Location(world, x + 0.5D, y, z + 0.5D, yaw, pitch);

                playerToTeleport.teleport(getSafeDestination(location));
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
    /* String Search Handling Stuff */
    public boolean isPlayerPartyOwner(File directory, String playerName) {
        if (!directory.exists() || !directory.isDirectory()) {
            return false;
        }

        // Filter to only grab .json files
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                    // Check if the "partyOwner" key exists and matches the name
                    if (jsonObject.has("partyOwner") && !jsonObject.get("partyOwner").isJsonNull()) {
                        String owner = jsonObject.get("partyOwner").getAsString();

                        // Case-insensitive check handles Minecraft name formatting safety
                        if (owner.equalsIgnoreCase(playerName)) {
                            return true;
                        }
                    }
                } catch (IOException | JsonSyntaxException e) {
                    // Skips broken files and continues searching valid ones
                    Bukkit.getLogger().severe("[OSM-Ess] Could not read party file: " + file.getName());
                }
            }
        }

        return false;
    }
    public boolean isPlayerPartyMember(File directory, String searchString, String listKey) {
        if (!directory.exists() || !directory.isDirectory()) {return false;}

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
    public List<String> fetchStringListFromJson(Path filePath, String keyName) {
        List<String> stringList = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath.toFile())) {
            // Parse the JSON file into a JsonObject
            JsonElement rootElement = JsonParser.parseReader(reader);

            if (rootElement.isJsonObject()) {
                JsonArray jsonArray = rootElement.getAsJsonObject().getAsJsonArray(keyName);

                if (jsonArray != null) {
                    // Iterate through the JSON array and convert each element to a String
                    for (JsonElement element : jsonArray) {
                        stringList.add(element.getAsString());
                    }
                }
            }
        } catch (IOException e) {
            // Handle file missing or read errors
            e.printStackTrace();
        }

        return stringList;
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

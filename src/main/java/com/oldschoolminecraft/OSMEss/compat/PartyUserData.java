package com.oldschoolminecraft.OSMEss.compat;

import com.google.gson.Gson;
import org.bukkit.Location;

import java.util.List;

public class PartyUserData {

    public static final Gson gson = new Gson();
    public String partyName;
    public String partyOwner;
    public int dateCreated;
    public SerializableLocation partyHomeLocation;
    public List<String> partyMembers;
}

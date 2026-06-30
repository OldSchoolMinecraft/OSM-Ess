package com.oldschoolminecraft.OSMEss.compat;

import com.google.gson.Gson;

import java.util.List;

public class PartyUserData {

    public static final Gson gson = new Gson();
    public String partyName;
    public String partyOwner;
    public long dateCreated;
    public String partyHomeLocation;
    public List<String> partyMembers;
}

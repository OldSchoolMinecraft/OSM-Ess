package com.oldschoolminecraft.OSMEss.compat;

import java.util.ArrayList;

public class OSMPLUserData
{
    public static final Gson gson = new Gson();
    public String name;
    public String ip;
    public long lastLogIn; // all timestamps are epoch millis
    public long lastLogOut;
    public long playTime; // total playtime in millis
    public long firstJoin;
    public ArrayList<Punishment> punishHistory; // we aren't using this but it needs to be here i'm pretty sure in order to read correctly
    public Punishment currentBan;
    public Punishment currentMute;
    public int kills, deaths; // these integers will probably be imported and used by another plugin down the line
    public long lastWild; // we don't use this anymore either since the creation of OpenRTP
    public boolean ignoreBroadcast; // this is used for ignoring auto broadcasts, but will be unused now that OSMPL is dead.

    public class Punishment
    {
        public long time;
        public String reason;
        public String type;
        public long expire;
    }
}

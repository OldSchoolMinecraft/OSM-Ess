package com.oldschoolminecraft.OSMEss;

import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class AuctionThread extends Thread {
    private int secondsRemaining;
    private final Runnable auctionEndCallback;
    private boolean running;

    public AuctionThread(int durationInSeconds, Runnable auctionEndCallback) {
        this.secondsRemaining = durationInSeconds;
        this.auctionEndCallback = auctionEndCallback;
    }

    public void run() {
        running = true;

        while (running) {
            if (secondsRemaining == 0) {
                auctionEndCallback.run();
                running = false;
            }

            else if (secondsRemaining == 1) {Bukkit.broadcastMessage("§9Auction ends in §b1 second§9!");}
            else if (secondsRemaining == 2) {Bukkit.broadcastMessage("§9Auction ends in §b2 seconds§9!");}
            else if (secondsRemaining == 3) {Bukkit.broadcastMessage("§9Auction ends in §b3 seconds§9!");}
            else if (secondsRemaining == 15) {Bukkit.broadcastMessage("§9Auction ends in §b15 seconds§9!");}
            else if (secondsRemaining == 30) {Bukkit.broadcastMessage("§9Auction ends in §b30 seconds§9!");}
            else if (secondsRemaining == 45) {Bukkit.broadcastMessage("§9Auction ends in §b45 seconds§9!");}

            secondsRemaining--;
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        }
    }

    public void addTime(int seconds) {
        this.secondsRemaining += seconds;
    }
}

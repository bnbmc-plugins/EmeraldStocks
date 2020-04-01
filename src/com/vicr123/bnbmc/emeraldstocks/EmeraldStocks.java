package com.vicr123.bnbmc.emeraldstocks;

import com.vicr123.bnbmc.emeraldstocks.commands.StocksCommand;
import com.vicr123.bnbmc.emeraldstocks.events.TimeSchedulerEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import tech.cheating.chaireco.IEconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EmeraldStocks extends JavaPlugin {
    IEconomy economy;
    Connection connection;

    Float currentSeed = null;

    @Override
    public void onDisable() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        economy = this.getServer().getServicesManager().getRegistration(IEconomy.class).getProvider();
        prepareDatabase();

        this.getServer().getScheduler().runTaskTimer(this, new TimeSchedulerEvent(this), 0, 200);
        this.getCommand("stocks").setExecutor(new StocksCommand(this));
    }

    private void prepareDatabase() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:emeraldstocks.db");
            this.connection.createStatement().execute("PRAGMA foreign_keys=ON");
            this.connection.createStatement().execute("CREATE TABLE IF NOT EXISTS lastDay(day INTEGER PRIMARY KEY)");
            this.connection.createStatement().execute("CREATE TABLE IF NOT EXISTS stockPrices(id INTEGER PRIMARY KEY AUTOINCREMENT, seed FLOAT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getDatabase() {
        return connection;
    }

    public IEconomy getEconomy() {
        return economy;
    }

    public void setCurrentSeed(float seed) {
        this.currentSeed = seed;
    }

    public Float currentSeed() {
        return this.currentSeed;
    }

    public int getBuyPrice() {
        return getBuyPrice(this.currentSeed);
    }

    public int getSellPrice() {
        return getSellPrice(this.currentSeed);
    }

    public int getBuyPrice(float seed) {
        return (int) (Math.pow(seed, 3) * 15000 + (seed * 100));
    }

    public int getSellPrice(float seed) {
        return (int) (Math.pow(seed, 3) * 15000 - (seed * 100));
    }
}

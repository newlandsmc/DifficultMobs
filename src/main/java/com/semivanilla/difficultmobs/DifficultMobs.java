package com.semivanilla.difficultmobs;

import com.semivanilla.difficultmobs.listeners.MobListener;
import com.semivanilla.difficultmobs.manager.DifficultyManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class DifficultMobs extends JavaPlugin {

    private static DifficultMobs instance;

    private DifficultyManager difficultyManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        difficultyManager = new DifficultyManager();
        difficultyManager.init(this);

        getServer().getPluginManager().registerEvents(new MobListener(), this);
    }

    @Override
    public void onDisable() {

    }

    public static DifficultMobs getInstance() {
        return instance;
    }

    public DifficultyManager getDifficultyManager() {
        return difficultyManager;
    }
}

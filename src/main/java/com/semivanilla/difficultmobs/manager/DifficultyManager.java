package com.semivanilla.difficultmobs.manager;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.semivanilla.difficultmobs.DifficultMobs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

public class DifficultyManager {
    private static double DIVIDE_BY = 100.0;
    public static boolean hostileOnly = true, naturalOnly = true;
    public static double playerDistance = 512;
    public void init(DifficultMobs plugin) {
        DIVIDE_BY = plugin.getConfig().getDouble("difficulty.divide-by", 100.0);
        hostileOnly = plugin.getConfig().getBoolean("difficulty.hostile-only", true);
        playerDistance = plugin.getConfig().getDouble("difficulty.player-distance", 512);
        naturalOnly = plugin.getConfig().getBoolean("natural-reason-only", true);
    }

    public int getPowerLevel(Player player) {
        com.archyx.aureliumskills.data.PlayerData aurPdata = AureliumAPI.getPlugin().getPlayerManager().getPlayerData(player);
        if (aurPdata == null) {
            return 1;
        }
        return aurPdata.getPowerLevel();
    }

    public double getMobHealth(double baseHealth, Player player) {
        int powerLevel = getPowerLevel(player);
        int tens = powerLevel / 10;

        double d = tens / DIVIDE_BY;
        double extraHealth = baseHealth * d;
        return extraHealth + baseHealth;
    }
}

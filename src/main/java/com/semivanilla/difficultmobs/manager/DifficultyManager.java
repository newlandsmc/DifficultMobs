package com.semivanilla.difficultmobs.manager;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skills;
import com.semivanilla.difficultmobs.DifficultMobs;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.Random;

public class DifficultyManager {
    private static double DIVIDE_BY = 92.0d;
    public static boolean hostileOnly = true, naturalOnly = true, usePower = false, randomEnabled;
    public static double playerDistance = 512, randomAmount = 0.1;

    private static final Random random = new Random();

    public void init(DifficultMobs plugin) {
        DIVIDE_BY = plugin.getConfig().getDouble("difficulty.divide-by", 92.0);
        hostileOnly = plugin.getConfig().getBoolean("difficulty.hostile-only", true);
        playerDistance = plugin.getConfig().getDouble("difficulty.player-distance", 512);
        usePower = plugin.getConfig().getBoolean("difficulty.use-power-level", false);
        naturalOnly = plugin.getConfig().getBoolean("natural-reason-only", true);

        randomEnabled = plugin.getConfig().getBoolean("difficulty.random.enabled", false);
        randomAmount = plugin.getConfig().getDouble("difficulty.random.amount", 0.1); // Randomly vary the player's power level by this percentage
    }

    public int getPowerLevel(Player player) {
        if (!usePower) {
            return AureliumAPI.getSkillLevel(player, Skills.DEFENSE);
        }
        com.archyx.aureliumskills.data.PlayerData aurPdata = AureliumAPI.getPlugin().getPlayerManager().getPlayerData(player);
        if (aurPdata == null) {
            return 1;
        }
        int power = aurPdata.getPowerLevel();
        if (power < 1) {
            return 1;
        }
        if (randomEnabled) {
            // Randomly vary the player's power level by percentage
            double randomAmount = random.nextDouble() * DifficultyManager.randomAmount;
            if (random.nextBoolean()) {
                power += randomAmount * power;
            } else {
                power -= randomAmount * power;
            }
        }
        return power;
    }

    public double getMobHealth(double baseHealth, Player player) {
        double powerLevel = getPowerLevel(player);
        //mob health = ( ( defense-5 ) / divide-by ) * base_health + base_health

        return ((powerLevel - 5d) / DIVIDE_BY) * baseHealth + baseHealth;
        /* //Old Algorithm
        int tens = powerLevel / 10;

        double d = tens / DIVIDE_BY;
        double extraHealth = baseHealth * d;
        return extraHealth + baseHealth;
         */
    }

    public boolean ignorePlayer(Player player) {
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) return true;
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}

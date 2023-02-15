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
    public static boolean hostileOnly = true, naturalOnly = true, usePower = false, randomEnabled = true,
            extraHealthEnabled = true;
    public static double playerDistance = 512, randomAmount = 0.1, extraHealthChance = 0.02, extraHealthAmount = 0.1;

    private static final Random random = new Random();

    public void init(DifficultMobs plugin) {
        DIVIDE_BY = plugin.getConfig().getDouble("difficulty.divide-by", 92.0);
        hostileOnly = plugin.getConfig().getBoolean("difficulty.hostile-only", true);
        playerDistance = plugin.getConfig().getDouble("difficulty.player-distance", 512);
        usePower = plugin.getConfig().getBoolean("difficulty.use-power-level", false);
        naturalOnly = plugin.getConfig().getBoolean("natural-reason-only", true);

        randomEnabled = plugin.getConfig().getBoolean("difficulty.random.enabled", false);
        randomAmount = plugin.getConfig().getDouble("difficulty.random.amount", 0.1); // Randomly vary the player's power level by this percentage

        extraHealthEnabled = plugin.getConfig().getBoolean("difficulty.extra-health.enabled", false);
        extraHealthChance = plugin.getConfig().getDouble("difficulty.extra-health.chance", 0.02);
        extraHealthAmount = plugin.getConfig().getDouble("difficulty.extra-health.amount", 0.1);
    }

    public int getPowerLevel(Player player) {
        if (!usePower) {
            return randomVary(AureliumAPI.getSkillLevel(player, Skills.DEFENSE));
        }
        com.archyx.aureliumskills.data.PlayerData aurPdata = AureliumAPI.getPlugin().getPlayerManager().getPlayerData(player);
        if (aurPdata == null) {
            return 1;
        }
        int power = aurPdata.getPowerLevel();
        if (power < 1) {
            return 1;
        }
        return randomVary(power);
    }

    public int randomVary(int in) {
        if (randomEnabled) {
            // Randomly vary the player's power level by percentage
            double randomAmount = random.nextDouble() * DifficultyManager.randomAmount;
            if (random.nextBoolean()) {
                in += randomAmount * in;
            } else {
                in -= randomAmount * in;
            }
        }
        return in;
    }


    public double getMobHealth(double baseHealth, Player player) {
        double powerLevel = getPowerLevel(player);
        //mob health = ( ( defense-5 ) / divide-by ) * base_health + base_health

        double health = ((powerLevel - 5d) / DIVIDE_BY) * baseHealth + baseHealth;
        if (extraHealthEnabled && random.nextDouble() < extraHealthChance) {
            health += extraHealthAmount * health;
        }
        return health;
    }

      /* //Old Algorithm
        int tens = powerLevel / 10;

        double d = tens / DIVIDE_BY;
        double extraHealth = baseHealth * d;
        return extraHealth + baseHealth;
         */

    public boolean ignorePlayer(Player player) {
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) return true;
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}

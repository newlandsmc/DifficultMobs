package com.semivanilla.difficultmobs.listeners;

import com.semivanilla.difficultmobs.DifficultMobs;
import com.semivanilla.difficultmobs.manager.DifficultyManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class MobListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (DifficultyManager.hostileOnly && ! (event.getEntity() instanceof Monster)) return;

        if (DifficultyManager.naturalOnly && (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DEFAULT)) return;

        Player closestPlayer = null;
        double distance = DifficultyManager.playerDistance;
        List<Player> onlinePlayers = Bukkit.getWorld(entity.getWorld().getUID()).getPlayers();
        for(Player pl : onlinePlayers)
            if(entity.getLocation().distance(pl.getLocation()) < distance) {
                distance = entity.getLocation().distance(pl.getLocation());
                closestPlayer = pl;
            }
        if(closestPlayer == null) return;

        double newHealth = DifficultMobs.getInstance().getDifficultyManager().getMobHealth(
                entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(),
                closestPlayer
        );
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newHealth);
    }
}

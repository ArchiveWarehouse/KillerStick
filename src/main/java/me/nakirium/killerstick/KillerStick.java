package me.nakirium.killerstick;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class KillerStick extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("KillerStickPlugin has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("KillerStickPlugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // You can implement any commands you want here, but for this example, we won't use any.
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Check if the player is holding a stick and right-clicked
        if (player.getItemInHand().getType() == Material.STICK && event.getAction().toString().contains("RIGHT")) {
            Map<String, Integer> killedMobs = new HashMap<>(); // Map to store killed mobs and their counts
            // Loop through nearby entities (mobs)
            for (Entity entity : player.getNearbyEntities(30, 30, 30)) {
                // Check if the entity is not a player
                if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    // Inflict damage on the entity to simulate natural death
                    livingEntity.damage(livingEntity.getMaxHealth() * 2);
                    // Drop loot (You can remove this if you don't want to drop loot)
                    for (ItemStack drop : livingEntity.getEquipment().getArmorContents()) {
                        if (drop != null && drop.getType() != Material.AIR) {
                            entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
                        }
                    }
                    ItemStack heldItem = livingEntity.getEquipment().getItemInMainHand();
                    if (heldItem != null && heldItem.getType() != Material.AIR) {
                        entity.getWorld().dropItemNaturally(entity.getLocation(), heldItem);
                    }
                    // Store the type of mob killed and increment the count
                    String mobName = livingEntity.getType().name(); // Get the name of the mob
                    killedMobs.put(mobName, killedMobs.getOrDefault(mobName, 0) + 1);
                    // Remove the entity
                    entity.remove();
                }
            }
            // Construct the message with the list of killed mobs and their counts
            StringBuilder message = new StringBuilder("Killed mobs within a 30 block radius:");
            for (Map.Entry<String, Integer> entry : killedMobs.entrySet()) {
                message.append(" ").append(entry.getValue()).append(" ").append(entry.getKey()).append(",");
            }
            // Remove the trailing comma
            if (killedMobs.size() > 0) {
                message.deleteCharAt(message.length() - 1);
            } else {
                message.append(" None");
            }
            player.sendMessage(message.toString());
        }
    }
}
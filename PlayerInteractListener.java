package com.lupino.customweapons.listeners;

import com.lupino.customweapons.CustomWeapons;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.UUID;

public class PlayerInteractListener implements Listener {
    private static CustomWeapons plugin = null;

    private final HashMap<UUID, Long> shadowdaggercooldown = new HashMap<>();
    private final  HashMap<UUID, Integer> shadowdaggerdistance = new HashMap<>();

    public PlayerInteractListener(CustomWeapons plugin){
        this.plugin=plugin;
    }


@EventHandler
public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
    if (event.getHand()!=EquipmentSlot.HAND||event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK)return;
    ItemStack hand = player.getItemInHand();
 int distance=100;

    if (hand.hasItemMeta() && hand.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Shadow dagger")) {

      if(!shadowdaggerdistance.containsKey(player.getUniqueId())){
            shadowdaggerdistance.put(player.getUniqueId(),60);
        }

        RayTraceResult result = player.rayTraceBlocks(shadowdaggerdistance.get(player.getUniqueId()));
     // player.sendMessage("to co je před checkem <4-"+shadowdaggerdistance.get(player.getUniqueId()));
       // if (shadowdaggerdistance.get(player.getUniqueId())<10){
           // player.sendMessage("ted checkneš jestli jsi v colldownu");
           // if(isOnCooldown(player,shadowdaggercooldown))return;
            //player.sendMessage(ChatColor.GREEN+"už nejsi v colldownu dalším kliknutím pokud máš manu aktivuješ schopnost");
            //shadowdaggerdistance.put(player.getUniqueId(),60);
       // }
        if (result != null && result.getHitBlock() != null ){
            Location loc = result.getHitBlock().getLocation();
            loc.add(0.5,1,0.5);
            loc.setDirection(player.getLocation().getDirection());
            if(loc.getBlock().getType()!=Material.AIR)return;
            if(shadowdaggerdistance.get(player.getUniqueId()) == 60){
            if(!consumeMana(player,ChatColor.BLUE+"Mana"))return;
            setCooldown(player,shadowdaggercooldown);
            }
           Location playerlocbefore = player.getLocation();
            createShrinkingParticleEffect(player,playerlocbefore.add(0,0.5,0) , Particle.SOUL);
            new BukkitRunnable() {
                @Override
                public void run() {

                    createExpandingParticleEffect(player, loc.add(0,0.5,0), Particle.SOUL);
                    player.teleport(loc);
                }
            }.runTaskLater(plugin, 4L);




          /*  createShrinkingParticleEffect(player,playerlocbefore.add(0,0.5,0) , Particle.SOUL);
            player.teleport(loc);
            createShrinkingParticleEffect(player, loc.add(0,0.5,0), Particle.SOUL);*/






            //player.teleport(loc);



            double doubledistancetraveled = loc.distance(playerlocbefore);
            int distancetraveled = (int) Math.ceil(doubledistancetraveled);
            //player.sendMessage(distancetraveled+"");

            shadowdaggerdistance.put(player.getUniqueId(),shadowdaggerdistance.get(player.getUniqueId())-distancetraveled);
            if(isOnCooldown(player,shadowdaggercooldown))return;
            player.sendMessage(ChatColor.GREEN+"už nejsi v colldownu dalším kliknutím pokud máš manu aktivuješ schopnost");
            shadowdaggerdistance.put(player.getUniqueId(),60);

        } else {
            if(isOnCooldown(player,shadowdaggercooldown))return;
            player.sendMessage(ChatColor.GREEN+"už nejsi v colldownu dalším kliknutím pokud máš manu aktivuješ schopnost");
            shadowdaggerdistance.put(player.getUniqueId(),60);

            if(shadowdaggerdistance.get(player.getUniqueId()) == 60) return;
            player.sendMessage(ChatColor.RED+"too far");
        }


    }
    if (hand.hasItemMeta() && hand.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Rail gun")) {
        RayTraceResult result = player.rayTraceBlocks(distance);
        if (result != null && result.getHitBlock() != null ){

            Location loc = result.getHitBlock().getLocation();
            loc.add(0,1,0);
            Integer intpower = plugin.getConfig().getInt("PowerOfExplosions");
            Float power = intpower.floatValue();
            for(int i=0;i<plugin.getConfig().getInt("NumberOfExplosions");i++) {
                player.getWorld().createExplosion(loc,power);
            }

        }else {
            player.sendMessage("to far");
        }


    }

}

    public boolean consumeMana(Player player,String itemName) {
        ItemStack[] inventory = player.getInventory().getContents();

        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];

            // Kontrola, zda položka není prázdná a je správného typu
            if (item != null && item.getType() == Material.LIGHT_BLUE_DYE) {
                ItemMeta meta = item.getItemMeta();

                // Kontrola, zda má předmět požadované jméno
                if (meta != null && itemName.equalsIgnoreCase(meta.getDisplayName())) {
                   // player.sendMessage("Odebrání jednoho kusu předmětu");
                    item.setAmount(item.getAmount() - 1);

                    // Pokud už není žádný kus, odstraníme item z inventáře
                    if (item.getAmount() <= 0) {
                        inventory[i] = null;
                    }

                    // Nastavení aktualizovaného inventáře zpět hráči
                    player.getInventory().setContents(inventory);

                    return true; // Předmět byl nalezen a odebrán
                }
            }
        }
       // player.sendMessage("nic se nenašlo");
        return false; // Předmět s požadovaným názvem nebyl nalezen
    }
    private void setCooldown(Player player, HashMap<UUID, Long> cooldowns) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (10 * 1000));
    }
    private boolean isOnCooldown(Player player, HashMap<UUID, Long> cooldowns) {
        UUID playerUUID = player.getUniqueId();
        if (cooldowns.containsKey(playerUUID)) {
            long timeLeft = (cooldowns.get(playerUUID) - System.currentTimeMillis()) / 1000;

            if(timeLeft>0) {
                player.sendMessage("Wait for " + timeLeft+" seconds then try again to refil");
            }
            return timeLeft > 0;
        }
        return false;
    }
    private void createShrinkingParticleEffect(Player player, Location location, Particle particle) {
        new BukkitRunnable() {
            double radius = 1.0;
            @Override
            public void run() {
                if (radius <= 0.1) {
                    this.cancel();
                    return;
                }
                spawnParticleSphere(location, radius, particle);
                radius -= 0.1; // Zmenšuje poloměr koule
            }
        }.runTaskTimer( plugin, 0L, 2L); // Každé 2 ticky (0.1s)
    }
    private void createExpandingParticleEffect(Player player, Location location, Particle particle) {
        new BukkitRunnable() {
            double radius = 0.1;
            @Override
            public void run() {
                if (radius >= 1.0) {
                    this.cancel();
                    return;
                }
                spawnParticleSphere(location, radius, particle);
                radius += 0.1; // Zvětšuje poloměr koule
            }
        }.runTaskTimer(plugin, 0L, 2L); // Každé 2 ticky (0.1s)
    }

    private void spawnParticleSphere(Location center, double radius, Particle particle) {
        for (double theta = 0; theta <= Math.PI; theta += Math.PI / 10) {
            for (double phi = 0; phi <= 2 * Math.PI; phi += Math.PI / 10) {
                double x = radius * Math.sin(theta) * Math.cos(phi);
                double y = radius * Math.cos(theta);
                double z = radius * Math.sin(theta) * Math.sin(phi);

                center.getWorld().spawnParticle(particle, center.clone().add(x, y, z), 1, 0, 0, 0, 0);
            }
        }
    }













}





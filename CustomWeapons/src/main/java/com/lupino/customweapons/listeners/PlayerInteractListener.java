package com.lupino.customweapons.listeners;


import com.lupino.customweapons.CustomWeapons;
import com.lupino.customweapons.Keys;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.K;

import java.util.*;

public class PlayerInteractListener implements Listener {
    private static CustomWeapons plugin = null;

    private final HashMap<UUID, Long> vampirebladecooldown = new HashMap<>();

    private final HashMap<UUID, Long> shadowdaggercooldown = new HashMap<>();
    private final  HashMap<UUID, Integer> shadowdaggerdistance = new HashMap<>();
    private final HashMap<UUID, Long> flamingaxecooldown = new HashMap<>();
    private final HashMap<UUID, Long> coldstaffcooldown = new HashMap<>();
    private final HashMap<UUID, Long> staffofhealingcooldown = new HashMap<>();
    private final HashMap<UUID, Long> shieldofeternitycooldown = new HashMap<>();




    public PlayerInteractListener(CustomWeapons plugin){
        this.plugin=plugin;
    }


@EventHandler
public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
    if (event.getHand()!=EquipmentSlot.HAND||event.getAction()== Action.LEFT_CLICK_AIR||event.getAction()==Action.LEFT_CLICK_BLOCK)return;
    ItemStack hand = player.getItemInHand();
    if(World.Environment.NETHER==player.getWorld().getEnvironment()&& !plugin.getConfig().getBoolean("EnableWeaponsInNether"))return;
    if(World.Environment.NORMAL==player.getWorld().getEnvironment()&& !plugin.getConfig().getBoolean("EnableWeaponsInOverworld"))return;
    if(World.Environment.THE_END==player.getWorld().getEnvironment()&& !plugin.getConfig().getBoolean("EnableWeaponsInEnd"))return;
    if(!isNotInCPVP(player, Objects.requireNonNull(plugin.getConfig().getLocation("Normalpvp.loc1")), Objects.requireNonNull(plugin.getConfig().getLocation("Normalpvp.loc2")))&&!plugin.getConfig().getBoolean("EnableWeaponsInCPVP"))return;


//Shadow dagger
//Shadow dagger
//Shadow dagger
    if (hand.hasItemMeta() && Objects.requireNonNull(hand.getItemMeta()).getPersistentDataContainer().has(Keys.SHADOW_DAGGER,PersistentDataType.BOOLEAN)&&plugin.getConfig().getBoolean("Shadowdagger.Enable",true)) {

      if(!shadowdaggerdistance.containsKey(player.getUniqueId())){
            shadowdaggerdistance.put(player.getUniqueId(),plugin.getConfig().getInt("Shadowdagger.Maxdistance",60));
        }

        RayTraceResult result = player.rayTraceBlocks(shadowdaggerdistance.get(player.getUniqueId()));
        if (result != null && result.getHitBlock() != null ){
            Location loc = result.getHitBlock().getLocation();
            loc.add(0.5,1,0.5);
            loc.setDirection(player.getLocation().getDirection());
            if(loc.getBlock().getType()!=Material.AIR)return;
            if(shadowdaggerdistance.get(player.getUniqueId()) == plugin.getConfig().getInt("Shadowdagger.Maxdistance",60)){
            if(!consumeMana(player))return;
            setCooldown(player,shadowdaggercooldown,plugin.getConfig().getInt("Shadowdagger.Cooldown",60));
                new BukkitRunnable() {
                    @Override
                    public void run() {

                        player.sendMessage(ChatColor.GREEN+"Už nejsi v colldownu na Shadow dagger");
                        shadowdaggerdistance.put(player.getUniqueId(),plugin.getConfig().getInt("Shadowdagger.Maxdistance",60));
                    }
                }.runTaskLater(plugin, plugin.getConfig().getInt("Shadowdagger.Cooldown",60)* 20L);
            }
           Location playerlocbefore = player.getLocation();
            createShrinkingParticleEffect(player,playerlocbefore.add(0,0.5,0) , Particle.SOUL);
            playerlocbefore.getWorld().playSound(playerlocbefore, Sound.ENTITY_WITHER_SHOOT, 2, 0.35f);
            new BukkitRunnable() {
                @Override
                public void run() {

                    createExpandingParticleEffect(player, loc.add(0,0.5,0), Particle.SOUL);
                    player.teleport(loc);
                }
            }.runTaskLater(plugin, 4L);
            double doubledistancetraveled = loc.distance(playerlocbefore);
            int distancetraveled = (int) Math.ceil(doubledistancetraveled);
            shadowdaggerdistance.put(player.getUniqueId(),shadowdaggerdistance.get(player.getUniqueId())-distancetraveled);
        } else {
            if(shadowdaggerdistance.get(player.getUniqueId()) == plugin.getConfig().getInt("Shadowdagger.Maxdistance",60)) return;
            if (shadowdaggerdistance.get(player.getUniqueId())<6){
                player.sendMessage(ChatColor.RED+"Too far you can only teleport " +shadowdaggerdistance.get(player.getUniqueId())+" blocks or ");
                isOnCooldown(player,shadowdaggercooldown,true);

                return;
            }
            if (shadowdaggerdistance.get(player.getUniqueId())<0)return;
            player.sendMessage(ChatColor.RED+"Too far you can only teleport " +shadowdaggerdistance.get(player.getUniqueId())+" blocks");

        }


    }

//Vampire blade
//Vampire blade
//Vampire blade
    if (hand.hasItemMeta() && Objects.requireNonNull(hand.getItemMeta()).getPersistentDataContainer().has(Keys.VAMPIRE_BLADE,PersistentDataType.BOOLEAN)&&plugin.getConfig().getBoolean("Vampireblade.Enable",true)){

        if (isOnCooldown(player,vampirebladecooldown,true)) {
            return;
        }

        if (!consumeMana(player))return;
        setCooldown(player,vampirebladecooldown, plugin.getConfig().getInt("Vampireblade.Cooldown",60));
       spawnParticleSphere(player.getLocation(),plugin.getConfig().getInt("Vampireblade.Radius",7),Particle.DAMAGE_INDICATOR,15);
       SendMessageAfterSomeTime(player,ChatColor.GREEN+"Už nejsi v colldownu na Vampire blade",plugin.getConfig().getInt("Vampireblade.Cooldown",60));
        new BukkitRunnable(){
            int secondsLeft = plugin.getConfig().getInt("Vampireblade.Duration",10);

            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                for (Player targeted : Bukkit.getOnlinePlayers()) {
                    if (targeted != player) {
                        if(IsInAnySafeZone(targeted)) return;
                        if (player.getLocation().distance(targeted.getLocation()) < plugin.getConfig().getInt("Vampireblade.Radius",7)) {
                            double newHealth = targeted.getHealth() - plugin.getConfig().getInt("Vampireblade.Damageandheal",2);
                            if (newHealth <= 0) {
                                targeted.damage(15);
                            } else {
                                targeted.setHealth(newHealth);
                            }
                            if (player.getHealth() < 20) {
                                player.setHealth(Math.min(player.getHealth() + plugin.getConfig().getInt("Vampireblade.Damageandheal",2), 20));
                            }
                            Location loc = player.getLocation();
                            spawnParticleSphere(loc, plugin.getConfig().getInt("Vampireblade.Radius",7), Particle.DAMAGE_INDICATOR,15);
                            loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_BREATH, 2, 2);

                        }
                    }
                }
                secondsLeft--;

                if (secondsLeft <= 0) {
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin,0,20);
    }


//Flaming axe
//Flaming axe
//Flaming axe
    if (hand.hasItemMeta() && Objects.requireNonNull(hand.getItemMeta()).getPersistentDataContainer().has(Keys.FLAMING_AXE,PersistentDataType.BOOLEAN)&&plugin.getConfig().getBoolean("Flamingaxe.Enable",true)){

        if (isOnCooldown(player,flamingaxecooldown,true)) {
            return;
        }
        if (!consumeMana(player))return;
        setCooldown(player,flamingaxecooldown,plugin.getConfig().getInt("Flamingaxe.Cooldown",60));
        Location loc = player.getLocation();
        loc.getWorld().playSound(loc, Sound.ENTITY_GHAST_SHOOT, 2, 0.65f);
        List<String> playerNames = new ArrayList<>();
        SendMessageAfterSomeTime(player,ChatColor.GREEN+"Už nejsi v colldownu na Flaming axe",plugin.getConfig().getInt("Flamingaxe.Cooldown",60));
        new BukkitRunnable(){

            float radius = 0.0f;
            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                spawnParticleSphere(loc, radius, Particle.FLAME,15);
                for (Player targeted : Bukkit.getOnlinePlayers()) {
                    if (targeted != player) {

                        if (player.getLocation().distance(targeted.getLocation()) < radius) {
                            if(!IsInAnySafeZone(targeted)) {
                                Vector direction = targeted.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();

                                if (radius < plugin.getConfig().getInt("Flamingaxe.Smallerradius",6)) {
                                    if (!playerNames.contains(targeted.getName())) {
                                        double newHealth = targeted.getHealth() - plugin.getConfig().getInt("Flamingaxe.Smallerradiusdamage",15);
                                        if (newHealth <= 0) {
                                            targeted.damage(100);
                                        } else {
                                            targeted.setHealth(newHealth);
                                            playerNames.add(targeted.getName());
                                        }
                                        targeted.setVelocity(direction.multiply(2));
                                    }
                                }
                                //------------------------
                                if (!playerNames.contains(targeted.getName())) {
                                    double newHealth = targeted.getHealth() - plugin.getConfig().getInt("Flamingaxe.Damage",10);
                                    if (newHealth <= 0) {
                                        targeted.damage(100);
                                    } else {
                                        targeted.setHealth(newHealth);
                                        playerNames.add(targeted.getName());
                                    }

                                    targeted.setVelocity(direction.multiply(1));
                                }

                            }
                        }
                    }
                }
                radius+=0.3f;

                if (radius> plugin.getConfig().getInt("Flamingaxe.Radius",9)) {
                    playerNames.clear();
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin,0,1);



    }
//Cold staff
//Cold staff
//Cold staff
    if (hand.hasItemMeta() && Objects.requireNonNull(hand.getItemMeta()).getPersistentDataContainer().has(Keys.COLD_STAFF,PersistentDataType.BOOLEAN)&&plugin.getConfig().getBoolean("Coldstaff.Enable",true)){
        if (isOnCooldown(player,coldstaffcooldown,true)) {
            return;
        }
        if (!consumeMana(player))return;
        setCooldown(player,coldstaffcooldown,plugin.getConfig().getInt("Coldstaff.Cooldown",60));
        SendMessageAfterSomeTime(player,ChatColor.GREEN+"Už nejsi v colldownu na Cold staff",plugin.getConfig().getInt("Coldstaff.Cooldown",60));
        Location loc = player.getLocation();
        loc.getWorld().playSound(loc, Sound.BLOCK_POWDER_SNOW_FALL, 2, 0.1f);
        createExpandingCircleParticleEffect(player,loc.add(0,0.5,0),Particle.SNOWBALL,plugin.getConfig().getInt("Coldstaff.Radius",9),15.0f);
        for (Player targeted : Bukkit.getOnlinePlayers()) {
            if (targeted != player) {
                if (player.getLocation().distance(targeted.getLocation()) < plugin.getConfig().getInt("Coldstaff.Radius",9)) {
                    if(IsInAnySafeZone(targeted)) return;
                    freezePlayer(targeted,plugin.getConfig().getInt("Coldstaff.Duration",5));
                }
            }
        }
    }
//Staff of healing
//Staff of healing
//Staff of healing
    if (hand.hasItemMeta() && Objects.requireNonNull(hand.getItemMeta()).getPersistentDataContainer().has(Keys.STAFF_OF_HEALING,PersistentDataType.BOOLEAN)&&plugin.getConfig().getBoolean("Staffofhealing.Enable",true)){
        if (isOnCooldown(player,staffofhealingcooldown,true)) {
            return;
        }
        if (!consumeMana(player))return;
        setCooldown(player,staffofhealingcooldown,plugin.getConfig().getInt("Staffofhealing.Cooldown",60));
        SendMessageAfterSomeTime(player,ChatColor.GREEN+"Už nejsi v colldownu na Staff of healing",plugin.getConfig().getInt("Staffofhealing.Cooldown",60));
        Location loc = player.getLocation();
        loc.getWorld().playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2,0.8f);

        spawnParticleCircle(loc.add(0,0.5,0),1.2,Particle.HEART,10);
        spawnParticleCircle(loc.add(0,0.4,0),1.2,Particle.HEART,10);
        player.setHealth(plugin.getConfig().getInt("Staffofhealing.Heal",20));



    }
//Shiel of eternity
//Shiel of eternity
//Shiel of eternity
    if (hand.hasItemMeta() && Objects.requireNonNull(hand.getItemMeta()).getPersistentDataContainer().has(Keys.SHIELD_OF_ETERNITY,PersistentDataType.BOOLEAN)&&plugin.getConfig().getBoolean("Shieldofeternity.Enable",true)){
        if (isOnCooldown(player,shieldofeternitycooldown,true)) {
            return;
        }
        if (!consumeMana(player))return;
        setCooldown(player,shieldofeternitycooldown,plugin.getConfig().getInt("Shieldofeternity.Cooldown",60));
        SendMessageAfterSomeTime(player,ChatColor.GREEN+"Už nejsi v colldownu na Shield of eternity",plugin.getConfig().getInt("Shieldofeternity.Cooldown",60));
        Location loc = player.getLocation();
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SHOOT, 2,1.5f);
        spawnParticleSphere(loc, plugin.getConfig().getInt("Shieldofeternity.Radius",6), Particle.EGG_CRACK,15);
        new BukkitRunnable(){
            double secondsLeft = plugin.getConfig().getInt("Shieldofeternity.Duration",10);

            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                for (Player targeted : Bukkit.getOnlinePlayers()) {
                    if (targeted != player) {

                        if (player.getLocation().distance(targeted.getLocation()) <= plugin.getConfig().getInt("Shieldofeternity.Radius",6)) {
                            Vector direction = targeted.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                            if(IsInAnySafeZone(targeted)) return;
                                targeted.setVelocity(direction.multiply(2.5));
                                spawnParticleSphere(player.getLocation(), plugin.getConfig().getInt("Shieldofeternity.Radius",6), Particle.EGG_CRACK,15);
                                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SHOOT, 2,1.5f);





                        }
                    }
                }
                secondsLeft-=0.05f;

                if (secondsLeft <= 0) {
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin,0,1);




    }










}

    public boolean consumeMana(Player player) {
        if (!plugin.getConfig().getBoolean("EnableMana")){
            return true;
        }

        ItemStack[] inventory = player.getInventory().getContents();

        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];

            // Kontrola, zda položka není prázdná a je správného typu
            if (item != null && item.getType() == Material.LIGHT_BLUE_DYE) {
                ItemMeta meta = item.getItemMeta();

                // Kontrola, zda má předmět požadované jméno v persu
                if (meta != null && meta.getPersistentDataContainer().has(Keys.MANA, PersistentDataType.BOOLEAN) ) {
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
    private void setCooldown(Player player, HashMap<UUID, Long> cooldowns, int seconds) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000));
    }
    private boolean isOnCooldown(Player player, HashMap<UUID, Long> cooldowns,boolean sendtimeleft) {
        UUID playerUUID = player.getUniqueId();
        if (cooldowns.containsKey(playerUUID)) {
            long timeLeft = (cooldowns.get(playerUUID) - System.currentTimeMillis()) / 1000;

            if(timeLeft>0&&sendtimeleft) {
                player.sendMessage("Wait for " + timeLeft+" seconds then try again to use with mana ");
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
                spawnParticleSphere(location, radius, particle,10);
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
                spawnParticleSphere(location, radius, particle,10);
                radius += 0.1; // Zvětšuje poloměr koule
            }
        }.runTaskTimer(plugin, 0L, 2L); // Každé 2 ticky (0.1s)
    }

    private void spawnParticleSphere(Location center, double radius, Particle particle,double density) {
        double step = Math.PI / density;


        for (double theta = 0; theta <= Math.PI; theta += step) {
            for (double phi = 0; phi <= 2 * Math.PI; phi += step) {
                double x = radius * Math.sin(theta) * Math.cos(phi);
                double y = radius * Math.cos(theta);
                double z = radius * Math.sin(theta) * Math.sin(phi);

                center.getWorld().spawnParticle(particle, center.clone().add(x, y, z), 1, 0, 0, 0, 0);
            }
        }
    }
    private void spawnParticleCircle(Location center, double radius, Particle particle, double density) {
        double step = Math.PI / density; // Hustota particlů na kruhu

        for (double angle = 0; angle < 2 * Math.PI; angle += step) {

            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);


            center.getWorld().spawnParticle(particle, center.clone().add(x, 0, z), 1, 0, 0, 0, 0);
        }
    }
    public void freezePlayer(Player player, int seconds) {
        Location loc = player.getLocation();
        new BukkitRunnable() {
            double secondsLeft = seconds;

            @Override
            public void run() {


                if (!player.getLocation().getBlock().equals(loc.getBlock())) {
                    loc.setDirection(player.getLocation().getDirection());
                    loc.setPitch(player.getLocation().getPitch());
                    player.teleport(loc);
                }




                secondsLeft-=0.05f;

                if (secondsLeft <= 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer( plugin, 0L, 1);

    }



    private void createExpandingCircleParticleEffect(Player player, Location location, Particle particle,double maxsize,double density) {
        new BukkitRunnable() {
            double radius = 0;
            @Override
            public void run() {
                if (radius > maxsize) {
                    this.cancel();
                    return;
                }
                spawnParticleCircle(location, radius, particle,density);
                radius += 0.3; // Zvětšuje poloměr koule
            }
        }.runTaskTimer(plugin, 0L, 1L); // Každé 2 ticky (0.1s)
    }

    public boolean isInSafeZone(Player player, Location loc1, Location loc2) {
        // Získání hráčovy pozice
        Location playerLoc = player.getLocation();

        // Zjištění minimálních a maximálních souřadnic pro každou osu (X, Y, Z)
        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());

        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());

        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        // Kontrola, jestli je hráčova pozice mezi těmito hodnotami
        return playerLoc.getX() >= minX && playerLoc.getX() <= maxX &&
                playerLoc.getY() >= minY && playerLoc.getY() <= maxY &&
                playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZ;
    }
    public boolean isNotInCPVP(Player player, Location loc1, Location loc2) {
        // Získání hráčovy pozice
        Location playerLoc = player.getLocation();

        // Zjištění minimálních a maximálních souřadnic pro každou osu (X, Y, Z)
        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());

        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());

        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        // Kontrola, jestli je hráčova pozice mezi těmito hodnotami
        return playerLoc.getX() >= minX && playerLoc.getX() <= maxX &&
                playerLoc.getY() >= minY && playerLoc.getY() <= maxY &&
                playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZ;
    }
public boolean IsInAnySafeZone (Player player){
        if (isInSafeZone(player, Objects.requireNonNull(plugin.getConfig().getLocation("Safezone.Overworld.loc1")), Objects.requireNonNull(plugin.getConfig().getLocation("Safezone.Overworld.loc2")))){
            return true;
        }
        if (isInSafeZone(player, Objects.requireNonNull(plugin.getConfig().getLocation("Safezone.Nether.loc1")), Objects.requireNonNull(plugin.getConfig().getLocation("Safezone.Nether.loc2")))){
        return true;
        }
        if (isInSafeZone(player, Objects.requireNonNull(plugin.getConfig().getLocation("Safezone.End.loc1")), Objects.requireNonNull(plugin.getConfig().getLocation("Safezone.End.loc2")))){
        return true;
        }


return false;

}
public void SendMessageAfterSomeTime(Player player,String s,Integer seconds){
    new BukkitRunnable() {

        @Override
        public void run() {
         player.sendMessage(s);

        }
    }.runTaskLater(plugin, seconds*20 );


}





}





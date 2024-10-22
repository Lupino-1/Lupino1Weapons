package com.lupino.customweapons.commands;

import com.lupino.customweapons.CustomWeapons;
import com.lupino.customweapons.Keys;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.K;


public class GiveWeapons implements CommandExecutor {
    private static CustomWeapons plugin = null;
    public GiveWeapons(CustomWeapons plugin){
        this.plugin=plugin;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            ItemStack shadow_dagger = CreateWeaponAdd(player,ChatColor.DARK_PURPLE+"Shadow dagger",Material.NETHERITE_SWORD,Keys.SHADOW_DAGGER);
            ItemStack vampire_blade = CreateWeaponAdd (player,ChatColor.RED+"Vampire blade",Material.NETHERITE_SWORD,Keys.VAMPIRE_BLADE);
            ItemStack flaming_axe = CreateWeaponAdd(player,ChatColor.COLOR_CHAR+"6Flaming axe",Material.NETHERITE_AXE,Keys.FLAMING_AXE);
            ItemStack cold_staff = CreateWeaponAdd(player,ChatColor.COLOR_CHAR + "bCold staff",Material.BLAZE_ROD,Keys.COLD_STAFF);
            ItemStack staff_of_healing = CreateWeaponAdd(player,ChatColor.COLOR_CHAR+ "aStaff of healing",Material.BLAZE_ROD,Keys.STAFF_OF_HEALING);
            ItemStack shield_of_eternity = CreateWeaponAdd(player,ChatColor.GOLD+"Shield of eternity",Material.SHIELD,Keys.SHIELD_OF_ETERNITY);






        }
        return true;
    }

    public ItemStack CreateWeaponAdd (Player player,String Name, Material Item, NamespacedKey namespacedKey){
        ItemStack itemStack = new ItemStack(Item);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Name);
        meta.getPersistentDataContainer().set(namespacedKey,PersistentDataType.BOOLEAN,true);
        itemStack.setItemMeta(meta);
        player.getInventory().addItem(itemStack);
        return itemStack;



    }



}

package com.lupino.customweapons;

import com.lupino.customweapons.listeners.PlayerInteractListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class ManaRecipe {
    private final CustomWeapons plugin;

    public ManaRecipe(CustomWeapons plugin) {
        this.plugin = plugin;
    }


    public void register() {
        plugin.saveDefaultConfig();

        ItemStack Mana = new ItemStack(Material.LIGHT_BLUE_DYE);
        ItemMeta Manameta = Mana.getItemMeta();
        Manameta.setDisplayName(ChatColor.BLUE + "Mana");
        Manameta.getPersistentDataContainer().set(Keys.MANA, PersistentDataType.BOOLEAN, true);
        Mana.setItemMeta(Manameta);

        ShapedRecipe manarecipe = new ShapedRecipe(new NamespacedKey(plugin, "Mana"), Mana);
        manarecipe.shape("ABC", "DEF", "GHI");

        char[] symbols = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        for (int i = 0; i < symbols.length; i++) {
            String path = "Manarecipe." + (i + 1);
            Material material = getMaterialFromConfig(path);
            if (material != null) {
                manarecipe.setIngredient(symbols[i], material);
            }
        }
        Bukkit.addRecipe(manarecipe);
    }
    private Material getMaterialFromConfig(String path) {
        String materialName = plugin.getConfig().getString(path, "").toUpperCase();
        if (materialName.isEmpty() || Material.matchMaterial(materialName) == null) {
            plugin.getLogger().warning("Chyba: Materiál '" + materialName + "' na cestě '" + path + "' není platný!");
            return null;
        }

        return Material.matchMaterial(materialName);
    }
}

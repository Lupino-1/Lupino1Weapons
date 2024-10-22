package com.lupino.customweapons;


import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.N;

public class Keys {
    public static NamespacedKey MANA;
    public static NamespacedKey SHADOW_DAGGER;
    public static NamespacedKey VAMPIRE_BLADE;
    public static NamespacedKey FLAMING_AXE;
    public static NamespacedKey COLD_STAFF;
    public static NamespacedKey STAFF_OF_HEALING;
    public static NamespacedKey SHIELD_OF_ETERNITY;


    // Inicializační metoda, která nastaví všechny NamespacedKey
    public static void initialize(Plugin plugin) {
        MANA = new NamespacedKey(plugin, "mana");
        SHADOW_DAGGER = new NamespacedKey(plugin, "shadow_dagger");
        VAMPIRE_BLADE = new NamespacedKey(plugin, "vampire_blade");
        FLAMING_AXE = new NamespacedKey(plugin,"flaming_axe");
        COLD_STAFF = new  NamespacedKey(plugin,"cold_staff");
        STAFF_OF_HEALING = new NamespacedKey(plugin,"staff_of_healing");
        SHIELD_OF_ETERNITY = new NamespacedKey(plugin,"shield_of_eternity");
    }


}

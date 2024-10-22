package com.lupino.customweapons;

import com.lupino.customweapons.commands.GiveWeapons;


import com.lupino.customweapons.listeners.PlayerInteractListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomWeapons extends JavaPlugin {

    private static CustomWeapons instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Keys.initialize(this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this),this);
        Objects.requireNonNull(getCommand("giveweapons")).setExecutor(new GiveWeapons(this));

        CustomWeapons.getInstance();
        if (getConfig().getBoolean("EnableMana")) {
            ManaRecipe manaRecipe = new ManaRecipe(this);
            manaRecipe.register();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CustomWeapons getInstance(){
        return getPlugin(CustomWeapons.class);
    }

}

package com.lupino.kosplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
public final class KosPlugin extends JavaPlugin  {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if(command.getName().equalsIgnoreCase("kos")) {
          if (sender instanceof Player) {
              Player p = (Player) sender;
              Inventory kos = Bukkit.createInventory(p, 27, "Koš");
              p.openInventory(kos);
          }
      }
        return true;
    }
}


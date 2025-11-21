package com.shweit.expendablebackpacks;

import com.shweit.expendablebackpacks.commands.BackpackCommand;
import com.shweit.expendablebackpacks.items.BackpackItem;
import com.shweit.expendablebackpacks.listeners.BackpackBlockListener;
import com.shweit.expendablebackpacks.listeners.BackpackCraftingListener;
import com.shweit.expendablebackpacks.listeners.BackpackInteractionListener;
import com.shweit.expendablebackpacks.listeners.BackpackProtectionListener;
import com.shweit.expendablebackpacks.listeners.BackpackSmithingListener;
import com.shweit.expendablebackpacks.recipes.BackpackRecipes;
import com.shweit.expendablebackpacks.storage.BackpackManager;
import com.shweit.expendablebackpacks.util.BackpackBlockUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Expendable Backpacks.
 */
public class ExpendableBackpacks extends JavaPlugin {

    private BackpackManager backpackManager;

    @Override
    public void onEnable() {
        // Initialize BackpackItem factory
        BackpackItem.initialize(this);

        // Initialize BackpackBlockUtil for block operations
        BackpackBlockUtil.initialize(this);

        // Initialize BackpackManager (storage)
        backpackManager = new BackpackManager(this);
        backpackManager.loadAllBackpacks();

        // Register recipes
        BackpackRecipes backpackRecipes = new BackpackRecipes(this);
        backpackRecipes.registerAll();

        // Register listeners
        getServer().getPluginManager().registerEvents(
            new BackpackCraftingListener(backpackManager, this), this);
        getServer().getPluginManager().registerEvents(
            new BackpackSmithingListener(backpackManager), this);
        getServer().getPluginManager().registerEvents(
            new BackpackInteractionListener(backpackManager), this);
        getServer().getPluginManager().registerEvents(
            new BackpackProtectionListener(), this);
        getServer().getPluginManager().registerEvents(
            new BackpackBlockListener(), this);
        getServer().getPluginManager().registerEvents(
            new com.shweit.expendablebackpacks.gui.BackpackGuideGUI(), this);

        // Register commands
        BackpackCommand backpackCommand = new BackpackCommand(backpackManager);
        getCommand("backpack").setExecutor(backpackCommand);
        getCommand("backpack").setTabCompleter(backpackCommand);

        // Initialize bStats metrics
        new Metrics(this, 28070);

        getLogger().info("╔════════════════════════════════╗");
        getLogger().info("║ Expendable Backpacks enabled!  ║");
        getLogger().info("║  8 Tiers • Stack Crafting      ║");
        getLogger().info("║  Enderpack Support • Commands  ║");
        getLogger().info("╚════════════════════════════════╝");
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving all backpack inventories...");

        // Save all inventories
        if (backpackManager != null) {
            backpackManager.saveAllInventories();
        }

        getLogger().info("╔════════════════════════════════╗");
        getLogger().info("║ Expendable Backpacks disabled  ║");
        getLogger().info("║  All data saved successfully   ║");
        getLogger().info("╚════════════════════════════════╝");
    }

    /**
     * Get the BackpackManager instance.
     *
     * @return the backpack manager instance
     */
    @SuppressWarnings("EI_EXPOSE_REP")
    public BackpackManager getBackpackManager() {
        return backpackManager;
    }
}

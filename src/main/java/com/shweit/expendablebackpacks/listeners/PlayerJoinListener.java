package com.shweit.expendablebackpacks.listeners;

import com.shweit.expendablebackpacks.items.BackpackItem;
import com.shweit.expendablebackpacks.items.BackpackTier;
import com.shweit.expendablebackpacks.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Handles giving starter backpacks to new players on first join.
 */
public class PlayerJoinListener implements Listener {

    private final Plugin plugin;

    /**
     * Creates a new player join listener.
     *
     * @param plugin the plugin instance
     */
    @SuppressWarnings("EI_EXPOSE_REP2")
    public PlayerJoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles player join events to give starter backpack to new players.
     *
     * @param event the player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Check if feature is enabled in config
        if (!plugin.getConfig().getBoolean("give-backpack-on-first-join", false)) {
            return;
        }

        // Check if this is the player's first time joining
        if (player.hasPlayedBefore()) {
            return;
        }

        // Create and give the Leather Backpack
        ItemStack leatherBackpack = BackpackItem.createBackpack(BackpackTier.LEATHER);

        // Try to add to inventory, if full drop at player location
        if (!player.getInventory().addItem(leatherBackpack).isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leatherBackpack);
        }

        // Send welcome message if configured
        String message = plugin.getConfig().getString("starter-backpack-message", "");
        if (message != null && !message.trim().isEmpty()) {
            player.sendMessage(TextUtil.fromLegacyConfig(message));
        }
    }
}

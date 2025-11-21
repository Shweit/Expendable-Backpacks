package com.shweit.expendablebackpacks.listeners;

import com.shweit.expendablebackpacks.items.BackpackItem;
import com.shweit.expendablebackpacks.items.BackpackTier;
import com.shweit.expendablebackpacks.storage.BackpackManager;
import com.shweit.expendablebackpacks.util.BackpackBlockUtil;
import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Handles backpack opening and closing.
 */
public class BackpackInteractionListener implements Listener {

    private final BackpackManager backpackManager;

    /**
     * Creates a new backpack interaction listener.
     *
     * @param backpackManager the backpack manager instance
     */
    @SuppressWarnings("EI_EXPOSE_REP2")
    public BackpackInteractionListener(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    /**
     * Handles player interaction with backpacks.
     *
     * @param event the player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if right-clicking
        if (event.getAction() != Action.RIGHT_CLICK_AIR
            && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        // Check if right-clicking on a backpack block
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null && BackpackBlockUtil.isBackpackBlock(clickedBlock)) {
                event.setCancelled(true);
                openBackpackBlock(player, clickedBlock);
                return;
            }
        }

        // Handle backpack items
        ItemStack item = event.getItem();
        if (!BackpackItem.isBackpack(item)) {
            return;
        }

        // If player is sneaking, allow placement (don't cancel event)
        if (player.isSneaking()) {
            return;
        }

        // Otherwise, open the backpack inventory
        event.setCancelled(true);

        @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
        UUID backpackUUID = BackpackItem.getBackpackUUID(item);
        BackpackTier tier = BackpackItem.getBackpackTier(item);

        if (backpackUUID == null || tier == null) {
            player.sendMessage("§cError: Invalid backpack data!");
            return;
        }

        // Get or create inventory
        String title = tier.getDisplayName();
        Inventory inventory = backpackManager.getInventory(
            backpackUUID, title, tier.getSlots());

        // Open for player
        player.openInventory(inventory);

        // Send message for Enderpacks
        if (tier.isEnderpack()) {
            player.sendMessage(
                "§5§lEnderpack §7opened! All Enderpacks with this ID share storage.");
        }
    }

    /**
     * Opens a placed backpack block for a player.
     *
     * @param player the player opening the backpack
     * @param block the backpack block
     */
    private void openBackpackBlock(Player player, Block block) {
        @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
        UUID backpackUUID = BackpackBlockUtil.getBackpackUUIDFromBlock(block);
        BackpackTier tier = BackpackBlockUtil.getBackpackTierFromBlock(block);

        if (backpackUUID == null || tier == null) {
            player.sendMessage("§cError: Invalid backpack block data!");
            return;
        }

        // Get or create inventory
        String title = tier.getDisplayName();
        Inventory inventory = backpackManager.getInventory(
            backpackUUID, title, tier.getSlots());

        // Open for player
        player.openInventory(inventory);

        // Send message for Enderpacks
        if (tier.isEnderpack()) {
            player.sendMessage(
                "§5§lEnderpack §7opened! All Enderpacks with this ID share storage.");
        }
    }

    /**
     * Handles inventory close events for backpacks.
     *
     * @param event the inventory close event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();

        // Check if it's a backpack inventory
        boolean isBackpack = false;
        for (BackpackTier tier : BackpackTier.values()) {
            if (title.equals(tier.getDisplayName())) {
                isBackpack = true;
                break;
            }
        }

        if (!isBackpack) {
            return;
        }

        // Find the backpack UUID from the player's inventory or the inventory holder
        // We need to save the inventory when closed
        // For simplicity, we'll search through loaded inventories
        @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
        UUID backpackUUID = findBackpackUUID(inv);
        if (backpackUUID != null) {
            backpackManager.saveInventory(backpackUUID, inv);
        }
    }

    /**
     * Handles inventory click events for backpacks to auto-save on changes.
     *
     * @param event the inventory click event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
        UUID backpackUUID = findBackpackUUID(clickedInventory);
        if (backpackUUID != null) {
            // Save inventory after the click is processed
            backpackManager.saveInventory(backpackUUID, clickedInventory);
        }
    }

    /**
     * Handles inventory drag events for backpacks to auto-save on changes.
     *
     * @param event the inventory drag event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
        UUID backpackUUID = findBackpackUUID(inventory);
        if (backpackUUID != null) {
            // Save inventory after the drag is processed
            backpackManager.saveInventory(backpackUUID, inventory);
        }
    }

    /**
     * Find the UUID of a backpack inventory by comparing instances.
     *
     * @param inventory the inventory to find
     * @return the UUID or null
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private UUID findBackpackUUID(Inventory inventory) {
        // Check all loaded inventories in the BackpackManager
        return backpackManager.findInventoryUUID(inventory);
    }
}

package com.shweit.expendablebackpacks.listeners;

import com.shweit.expendablebackpacks.items.BackpackItem;
import com.shweit.expendablebackpacks.items.BackpackTier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Prevents backpacks from being placed inside other backpacks (inception protection).
 * Can be disabled via the {@code allow-backpack-inception} config option.
 */
public class BackpackProtectionListener implements Listener {

    private final Plugin plugin;

    /**
     * Creates a new backpack protection listener.
     *
     * @param plugin the plugin instance used to read configuration
     */
    @SuppressWarnings("EI_EXPOSE_REP2")
    public BackpackProtectionListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles inventory click events to prevent backpack inception.
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (plugin.getConfig().getBoolean("allow-backpack-inception", false)) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        Inventory topInventory = event.getView().getTopInventory();
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        // Check if top inventory is a backpack by checking the title
        String title = event.getView().getTitle();
        boolean topIsBackpack = isBackpackTitle(title);

        // Prevent placing backpacks with cursor into backpack inventories
        if (topIsBackpack && clickedInventory == topInventory) {
            if (cursor != null && BackpackItem.isBackpack(cursor)) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(
                    "§cYou cannot put a backpack inside another backpack!");
                return;
            }
        }

        // Prevent shift-clicking backpacks from player inventory into backpack inventory
        if (event.isShiftClick() && current != null
            && BackpackItem.isBackpack(current)) {
            // If clicking in player inventory while backpack is open at top
            if (topIsBackpack && clickedInventory != topInventory) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(
                    "§cYou cannot put a backpack inside another backpack!");
                return;
            }
        }
    }

    /**
     * Handles inventory drag events to prevent backpack inception.
     *
     * @param event the inventory drag event
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (plugin.getConfig().getBoolean("allow-backpack-inception", false)) {
            return;
        }

        // Check if dragging into a backpack inventory by title
        String title = event.getView().getTitle();
        if (!isBackpackTitle(title)) {
            return;
        }

        ItemStack draggedItem = event.getOldCursor();
        if (draggedItem != null && BackpackItem.isBackpack(draggedItem)) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(
                "§cYou cannot put a backpack inside another backpack!");
        }
    }

    /**
     * Check if a title matches a backpack inventory title.
     * Backpack titles contain the tier display name (e.g., "Enderpack", "Diamond Backpack").
     *
     * @param title the title to check
     * @return true if the title matches a backpack inventory
     */
    private boolean isBackpackTitle(String title) {
        if (title == null) {
            return false;
        }

        // Check if title contains any backpack tier name
        for (BackpackTier tier : BackpackTier.values()) {
            if (title.contains(tier.getDisplayName())) {
                return true;
            }
        }

        return false;
    }
}

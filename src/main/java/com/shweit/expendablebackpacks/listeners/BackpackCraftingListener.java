package com.shweit.expendablebackpacks.listeners;

import com.shweit.expendablebackpacks.items.BackpackItem;
import com.shweit.expendablebackpacks.items.BackpackTier;
import com.shweit.expendablebackpacks.storage.BackpackManager;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Handles all backpack crafting logic.
 * Supports stacked items and generates unique UUIDs.
 */
public class BackpackCraftingListener implements Listener {

    private final BackpackManager backpackManager;
    private final Plugin plugin;

    /**
     * Creates a new backpack crafting listener.
     *
     * @param backpackManager the backpack manager instance
     * @param plugin the plugin instance
     */
    @SuppressWarnings("EI_EXPOSE_REP2")
    public BackpackCraftingListener(BackpackManager backpackManager, Plugin plugin) {
        this.backpackManager = backpackManager;
        this.plugin = plugin;
    }

    /**
     * Handles the crafting preparation event for backpacks.
     *
     * @param event the prepare item craft event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCraft(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = inv.getMatrix();

        // Check for Enderpack cloning (1 Enderpack + 1 Pearl = 2 Enderpacks with same UUID)
        // This is a shapeless recipe, works in any crafting grid
        ItemStack enderpack = null;
        boolean hasEnderPearl = false;
        int totalItems = 0;

        for (int i = 0; i < matrix.length; i++) {
            ItemStack item = matrix[i];
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            totalItems++;

            if (BackpackItem.isBackpack(item)) {
                BackpackTier tier = BackpackItem.getBackpackTier(item);
                if (tier == BackpackTier.ENDERPACK && item.getAmount() == 1) {
                    enderpack = item;
                } else {
                    enderpack = null;
                    break;
                }
            } else if (item.getType() == Material.ENDER_PEARL && item.getAmount() == 1) {
                hasEnderPearl = true;
            } else {
                enderpack = null;
                break;
            }
        }

        // If valid clone pattern: 1 Enderpack + 1 Pearl = 2 Enderpacks (same UUID)
        if (enderpack != null && hasEnderPearl && totalItems == 2) {
            ItemStack clonedEnderpack = BackpackItem.cloneBackpack(enderpack);
            if (clonedEnderpack != null) {
                clonedEnderpack.setAmount(2);
                inv.setResult(clonedEnderpack);
            }
            return;
        }

        // Only check 3x3 patterns below
        if (matrix.length != 9) {
            return; // Not a 3x3 crafting grid
        }

        // Check for Leather Backpack crafting (new Leather Backpack with new UUID)
        if (isLeatherBackpackPattern(matrix)) {
            inv.setResult(BackpackItem.createBackpack(BackpackTier.LEATHER));
            return;
        }

        // Check for Enderpack crafting (new Enderpack)
        if (isEnderpackPattern(matrix)) {
            inv.setResult(BackpackItem.createBackpack(BackpackTier.ENDERPACK));
            return;
        }

        // Check for upgrade patterns (center must be a backpack)
        ItemStack center = matrix[4];
        if (!BackpackItem.isBackpack(center)) {
            return; // Not an upgrade
        }

        BackpackTier currentTier = BackpackItem.getBackpackTier(center);
        if (currentTier == null) {
            return;
        }

        // Check if center is surrounded by upgrade material
        Material surrounding = getSurroundingMaterial(matrix);
        if (surrounding == null) {
            return;
        }

        // Determine target tier based on surrounding material
        BackpackTier targetTier = null;

        if (surrounding == Material.DIRT && currentTier == BackpackTier.LEATHER) {
            targetTier = BackpackTier.DIRT;
        } else if (surrounding == Material.COPPER_INGOT && currentTier == BackpackTier.LEATHER) {
            targetTier = BackpackTier.COPPER;
        } else if (surrounding == Material.IRON_INGOT && currentTier == BackpackTier.COPPER) {
            targetTier = BackpackTier.IRON;
        } else if (surrounding == Material.GOLD_INGOT && currentTier == BackpackTier.IRON) {
            targetTier = BackpackTier.GOLD;
        } else if (surrounding == Material.DIAMOND && currentTier == BackpackTier.GOLD) {
            targetTier = BackpackTier.DIAMOND;
        }

        if (targetTier != null) {
            // Upgrade preserves UUID (same inventory)
            ItemStack upgraded = BackpackItem.upgradeBackpack(center, targetTier);
            inv.setResult(upgraded);
        }
    }

    /**
     * Handles clicks on the crafting result slot for backpack upgrades.
     * Upgrade recipes use a custom result set via PrepareItemCraftEvent without a matching
     * registered recipe, so Minecraft's default ingredient consumption does not apply.
     * This handler manually controls result distribution and consumes exactly one item
     * per surrounding slot to prevent item duplication from stacked ingredients.
     *
     * @param event the inventory click event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        if (!(event.getInventory() instanceof CraftingInventory)) {
            return;
        }
        CraftingInventory inv = (CraftingInventory) event.getInventory();

        ItemStack result = inv.getResult();
        if (result == null || !BackpackItem.isBackpack(result)) {
            return;
        }

        ItemStack[] matrix = inv.getMatrix();

        // Only handle upgrade crafts: 3x3 grid with a backpack in the center slot
        if (matrix == null || matrix.length != 9 || !BackpackItem.isBackpack(matrix[4])) {
            return;
        }

        // Cancel default behaviour to take full control of result distribution and consumption
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        // Distribute result to player based on click type
        if (event.getClick().isShiftClick()) {
            player.getInventory().addItem(result.clone());
        } else {
            ItemStack cursor = player.getItemOnCursor();
            if (cursor != null && cursor.getType() != Material.AIR) {
                return; // Cursor occupied — cannot take result
            }
            player.setItemOnCursor(result.clone());
        }

        // Consume exactly 1 item from each of the 8 surrounding slots
        int[] surrounding = {0, 1, 2, 3, 5, 6, 7, 8};
        for (int i : surrounding) {
            ItemStack slot = matrix[i];
            if (slot == null || slot.getType() == Material.AIR) {
                continue;
            }
            if (slot.getAmount() > 1) {
                ItemStack reduced = slot.clone();
                reduced.setAmount(slot.getAmount() - 1);
                inv.setItem(i + 1, reduced);
            } else {
                inv.setItem(i + 1, null);
            }
        }
        // Clear the center backpack slot (matrix index 4 = inventory slot 5)
        inv.setItem(5, null);
    }

    /**
     * Check if the pattern matches Leather Backpack crafting.
     * L S L
     * L C L
     * L L L
     *
     * @param matrix the crafting matrix
     * @return true if the pattern matches
     */
    private boolean isLeatherBackpackPattern(ItemStack[] matrix) {
        return isMaterial(matrix[0], Material.LEATHER)
               && isMaterial(matrix[1], Material.STRING)
               && isMaterial(matrix[2], Material.LEATHER)
               && isMaterial(matrix[3], Material.LEATHER)
               && isMaterial(matrix[4], Material.CHEST)
               && isMaterial(matrix[5], Material.LEATHER)
               && isMaterial(matrix[6], Material.LEATHER)
               && isMaterial(matrix[7], Material.LEATHER)
               && isMaterial(matrix[8], Material.LEATHER);
    }

    /**
     * Check if the pattern matches Enderpack crafting.
     * E P E
     * P C P
     * E I E
     *
     * @param matrix the crafting matrix
     * @return true if the pattern matches
     */
    private boolean isEnderpackPattern(ItemStack[] matrix) {
        return isMaterial(matrix[0], Material.ENDER_EYE)
               && isMaterial(matrix[1], Material.ENDER_PEARL)
               && isMaterial(matrix[2], Material.ENDER_EYE)
               && isMaterial(matrix[3], Material.ENDER_PEARL)
               && isMaterial(matrix[4], Material.CHEST)
               && isMaterial(matrix[5], Material.ENDER_PEARL)
               && isMaterial(matrix[6], Material.ENDER_EYE)
               && isMaterial(matrix[7], Material.IRON_BLOCK)
               && isMaterial(matrix[8], Material.ENDER_EYE);
    }

    /**
     * Get the material surrounding the center slot (ignoring amount).
     * Returns null if not all 8 slots are the same material.
     *
     * @param matrix the crafting matrix
     * @return the surrounding material or null
     */
    private Material getSurroundingMaterial(ItemStack[] matrix) {
        // Indices: 0 1 2
        //          3 4 5
        //          6 7 8
        // Center is 4, surrounding are: 0,1,2,3,5,6,7,8

        int[] surrounding = {0, 1, 2, 3, 5, 6, 7, 8};
        Material material = null;

        for (int i : surrounding) {
            ItemStack item = matrix[i];
            if (item == null || item.getType() == Material.AIR) {
                return null; // Empty slot
            }
            if (material == null) {
                material = item.getType();
            } else if (item.getType() != material) {
                return null; // Different materials
            }
        }

        return material;
    }

    /**
     * Check if item is of given material (ignores amount for stacking support).
     *
     * @param item the item stack to check
     * @param material the material to compare against
     * @return true if the item is of the given material
     */
    private boolean isMaterial(ItemStack item, Material material) {
        return item != null && item.getType() == material;
    }
}

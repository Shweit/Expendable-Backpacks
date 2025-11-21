package com.shweit.expendablebackpacks.util;

import com.shweit.expendablebackpacks.items.BackpackTier;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Utility class for handling backpack blocks (placed backpacks).
 */
public class BackpackBlockUtil {

    private static Plugin plugin;
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private static NamespacedKey BACKPACK_UUID_KEY;
    private static NamespacedKey BACKPACK_TIER_KEY;

    /**
     * Initialize the BackpackBlockUtil with plugin instance.
     *
     * @param pluginInstance the plugin instance.
     */
    @SuppressWarnings("EI_EXPOSE_STATIC_REP2")
    public static void initialize(Plugin pluginInstance) {
        plugin = pluginInstance;
        BACKPACK_UUID_KEY = new NamespacedKey(plugin, "backpack_uuid");
        BACKPACK_TIER_KEY = new NamespacedKey(plugin, "backpack_tier");
    }

    /**
     * Check if a block is a placed backpack.
     *
     * @param block the block to check.
     * @return true if the block is a backpack, false otherwise.
     */
    public static boolean isBackpackBlock(Block block) {
        if (block == null || block.getType() != Material.PLAYER_HEAD
            && block.getType() != Material.PLAYER_WALL_HEAD) {
            return false;
        }

        if (!(block.getState() instanceof Skull skull)) {
            return false;
        }

        PersistentDataContainer pdc = skull.getPersistentDataContainer();
        return pdc.has(BACKPACK_UUID_KEY, PersistentDataType.STRING);
    }

    /**
     * Get the UUID of a backpack block.
     *
     * @param block the backpack block.
     * @return the UUID of the backpack, or null if not a backpack block.
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public static UUID getBackpackUUIDFromBlock(Block block) {
        if (!isBackpackBlock(block)) {
            return null;
        }

        Skull skull = (Skull) block.getState();
        PersistentDataContainer pdc = skull.getPersistentDataContainer();
        String uuidString = pdc.get(BACKPACK_UUID_KEY, PersistentDataType.STRING);

        if (uuidString == null) {
            return null;
        }

        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get the tier of a backpack block.
     *
     * @param block the backpack block.
     * @return the tier of the backpack, or null if not a backpack block.
     */
    public static BackpackTier getBackpackTierFromBlock(Block block) {
        if (!isBackpackBlock(block)) {
            return null;
        }

        Skull skull = (Skull) block.getState();
        PersistentDataContainer pdc = skull.getPersistentDataContainer();
        Integer level = pdc.get(BACKPACK_TIER_KEY, PersistentDataType.INTEGER);

        if (level == null) {
            return null;
        }

        return BackpackTier.fromLevel(level);
    }

    /**
     * Set backpack data on a placed block.
     *
     * @param block the block to set data on.
     * @param uuid the backpack UUID.
     * @param tier the backpack tier.
     * @return true if successful, false otherwise.
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public static boolean setBlockData(Block block, UUID uuid, BackpackTier tier) {
        if (block == null || uuid == null || tier == null) {
            return false;
        }

        if (block.getType() != Material.PLAYER_HEAD
            && block.getType() != Material.PLAYER_WALL_HEAD) {
            return false;
        }

        if (!(block.getState() instanceof Skull skull)) {
            return false;
        }

        PersistentDataContainer pdc = skull.getPersistentDataContainer();
        pdc.set(BACKPACK_UUID_KEY, PersistentDataType.STRING, uuid.toString());
        pdc.set(BACKPACK_TIER_KEY, PersistentDataType.INTEGER, tier.getLevel());

        return skull.update();
    }
}

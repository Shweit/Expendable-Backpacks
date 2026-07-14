package com.shweit.expendablebackpacks.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.shweit.expendablebackpacks.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Factory class for creating and managing backpack items.
 */
public class BackpackItem {

    private static Plugin plugin;
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private static NamespacedKey BACKPACK_UUID_KEY;
    private static NamespacedKey BACKPACK_TIER_KEY;

    /**
     * Initialize the BackpackItem factory with plugin instance.
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
     * Create a new backpack item with a unique UUID.
     *
     * @param tier the backpack tier.
     * @return the created backpack item.
     */
    public static ItemStack createBackpack(BackpackTier tier) {
        return createBackpack(tier, UUID.randomUUID());
    }

    /**
     * Create a backpack item with a specific UUID (for cloning).
     *
     * @param tier the backpack tier.
     * @param uuid the UUID for the backpack.
     * @return the created backpack item.
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public static ItemStack createBackpack(BackpackTier tier, UUID uuid) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();

        // Set display name
        meta.displayName(TextUtil.fromLegacy(tier.getDisplayName()));

        // Set lore
        List<String> lore = new ArrayList<>();
        lore.add("§8" + tier.getRows() + " rows • " + tier.getSlots()
            + " slots");
        if (tier.isEnderpack()) {
            lore.add("§5§oShared Storage");
            lore.add("§7All Enderpacks with the same ID");
            lore.add("§7share the same inventory");
        }
        lore.add("");
        lore.add("§7Right-click to open");
        lore.add("§8ID: " + uuid.toString().substring(0, 8) + "...");
        meta.lore(TextUtil.fromLegacy(lore));

        // Set player head texture using Paper's profile API
        if (meta instanceof SkullMeta skullMeta) {
            String textureValue = tier.getTextureValue();
            if (textureValue != null
                && !textureValue.startsWith("PLACEHOLDER")) {
                try {
                    // Use the backpack UUID to ensure clones are stackable
                    PlayerProfile profile = Bukkit.createProfile(uuid);
                    profile.getProperties().add(
                        new ProfileProperty("textures", textureValue));
                    skullMeta.setPlayerProfile(profile);
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to set texture for "
                        + tier.name() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // Store NBT data
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(BACKPACK_UUID_KEY, PersistentDataType.STRING,
            uuid.toString());
        pdc.set(BACKPACK_TIER_KEY, PersistentDataType.INTEGER,
            tier.getLevel());

        if (!item.setItemMeta(meta)) {
            plugin.getLogger().warning("Failed to set item meta for backpack " + tier.name());
        }
        return item;
    }

    /**
     * Check if an item is a backpack.
     *
     * @param item the item to check.
     * @return true if the item is a backpack, false otherwise.
     */
    public static boolean isBackpack(ItemStack item) {
        if (item == null || item.getType() != Material.PLAYER_HEAD) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(BACKPACK_UUID_KEY,
            PersistentDataType.STRING);
    }

    /**
     * Get the UUID of a backpack.
     *
     * @param item the backpack item.
     * @return the UUID of the backpack, or null if not a backpack.
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public static UUID getBackpackUUID(ItemStack item) {
        if (!isBackpack(item)) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        String uuidString = meta.getPersistentDataContainer().get(
            BACKPACK_UUID_KEY, PersistentDataType.STRING);
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get the tier of a backpack.
     *
     * @param item the backpack item.
     * @return the tier of the backpack, or null if not a backpack.
     */
    public static BackpackTier getBackpackTier(ItemStack item) {
        if (!isBackpack(item)) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        Integer level = meta.getPersistentDataContainer().get(
            BACKPACK_TIER_KEY, PersistentDataType.INTEGER);
        if (level == null) {
            return null;
        }
        return BackpackTier.fromLevel(level);
    }

    /**
     * Clone a backpack with the same UUID (for Enderpack cloning).
     *
     * @param original the original backpack to clone.
     * @return the cloned backpack, or null if not a valid backpack.
     */
    public static ItemStack cloneBackpack(ItemStack original) {
        if (!isBackpack(original)) {
            return null;
        }
        UUID uuid = getBackpackUUID(original);
        BackpackTier tier = getBackpackTier(original);
        if (uuid == null || tier == null) {
            return null;
        }
        return createBackpack(tier, uuid);
    }

    /**
     * Upgrade a backpack to the next tier (preserves UUID).
     *
     * @param original the original backpack.
     * @param newTier the new tier to upgrade to.
     * @return the upgraded backpack, or null if not a valid backpack.
     */
    public static ItemStack upgradeBackpack(ItemStack original, BackpackTier newTier) {
        if (!isBackpack(original)) {
            return null;
        }
        UUID uuid = getBackpackUUID(original);
        if (uuid == null) {
            return null;
        }
        return createBackpack(newTier, uuid);
    }
}

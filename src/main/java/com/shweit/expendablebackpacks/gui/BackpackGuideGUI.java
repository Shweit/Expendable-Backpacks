package com.shweit.expendablebackpacks.gui;

import com.shweit.expendablebackpacks.items.BackpackItem;
import com.shweit.expendablebackpacks.items.BackpackTier;
import com.shweit.expendablebackpacks.util.TextUtil;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Interactive GUI guide for backpack crafting and features.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class BackpackGuideGUI implements Listener {

    private static final Component MAIN_TITLE = TextUtil.fromLegacy("§6§lBackpack Guide");
    private static final String DETAIL_TITLE_PREFIX = "§6§l";

    /**
     * Open the main guide GUI for a player.
     *
     * @param player the player to open the GUI for.
     */
    public static void openMainGuide(Player player) {
        GuideInventoryHolder holder = new GuideInventoryHolder(MAIN_TITLE, null);
        Inventory gui = holder.getInventory();

        // Title item
        ItemStack title = createGuideItem(Material.CHEST,
            "§6§lBackpack Plugin Guide",
            "§7Click on any backpack to see",
            "§7crafting recipes and features!");
        gui.setItem(4, title);

        // Show all backpack tiers
        int[] slots = {19, 20, 21, 22, 23, 24, 25, 28}; // Layout positions
        BackpackTier[] tiers = BackpackTier.values();

        for (int i = 0; i < tiers.length && i < slots.length; i++) {
            BackpackTier tier = tiers[i];
            ItemStack backpack = BackpackItem.createBackpack(tier);

            // Add lore explaining to click for details
            ItemMeta meta = backpack.getItemMeta();
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            } else {
                lore = new ArrayList<>(lore);
            }
            lore.add(Component.empty());
            lore.add(TextUtil.fromLegacy("§e§l▶ Click for details!"));
            meta.lore(lore);
            if (!backpack.setItemMeta(meta)) {
                // Fallback if meta setting fails
                backpack = BackpackItem.createBackpack(tier);
            }

            gui.setItem(slots[i], backpack);
        }

        // Info items at bottom
        ItemStack craftingInfo = createGuideItem(Material.CRAFTING_TABLE,
            "§e§lCrafting Info",
            "§7Most backpacks are upgraded by",
            "§7surrounding them with 8x material",
            "",
            "§7Netherite uses Smithing Table",
            "§7Enderpack has special recipes");
        gui.setItem(45, craftingInfo);

        ItemStack enderpackInfo = createGuideItem(Material.ENDER_PEARL,
            "§5§lEnderpack Special",
            "§7All Enderpacks with the same ID",
            "§7share the same inventory!",
            "",
            "§7Clone: 1 Enderpack + 1 Pearl = 2 Packs",
            "§7(Both share the same storage)");
        gui.setItem(49, enderpackInfo);

        ItemStack upgradeInfo = createGuideItem(Material.DIAMOND,
            "§b§lUpgrade Path",
            "§7Leather → §6Copper → §fIron",
            "§7→ §eGold → §bDiamond → §8Netherite",
            "",
            "§7Also: Leather → §7Dirt (downgrade)",
            "§7Upgrades preserve your items!");
        gui.setItem(53, upgradeInfo);

        player.openInventory(gui);
    }

    /**
     * Open detailed view for a specific tier.
     *
     * @param player the player to open the GUI for.
     * @param tier the backpack tier to show details for.
     */
    public static void openTierDetail(Player player, BackpackTier tier) {
        GuideInventoryHolder holder = new GuideInventoryHolder(detailTitle(tier), tier);
        Inventory gui = holder.getInventory();

        // The backpack itself
        ItemStack backpack = BackpackItem.createBackpack(tier);
        gui.setItem(4, backpack);

        // Features
        ItemStack features = createGuideItem(Material.BOOK,
            "§e§lFeatures",
            "§7Size: §f" + tier.getRows() + " rows (" + tier.getSlots()
                + " slots)",
            tier.isEnderpack() ? "§5§lShared Storage"
                : "§7Unique inventory per backpack",
            "",
            tier.isEnderpack() ? "§7All Enderpacks with same ID"
                : "§7Right-click to open",
            tier.isEnderpack() ? "§7share the same storage"
                : "§7Items saved automatically");
        gui.setItem(20, features);

        // Crafting recipe
        addCraftingRecipe(gui, tier);

        // Upgrade info
        addUpgradeInfo(gui, tier);

        // Back button
        ItemStack back = createGuideItem(Material.ARROW,
            "§e§l← Back to Guide",
            "§7Return to main guide");
        gui.setItem(45, back);

        player.openInventory(gui);
    }

    /**
     * Add crafting recipe display to GUI.
     *
     * @param gui the inventory GUI.
     * @param tier the backpack tier.
     */
    private static void addCraftingRecipe(Inventory gui, BackpackTier tier) {
        switch (tier) {
            default:
                break;

            case LEATHER:
                // Pattern: L S L / L C L / L L L
                gui.setItem(10, new ItemStack(Material.LEATHER));
                gui.setItem(11, new ItemStack(Material.STRING));
                gui.setItem(12, new ItemStack(Material.LEATHER));
                gui.setItem(19, new ItemStack(Material.LEATHER));
                gui.setItem(20, new ItemStack(Material.CHEST));
                gui.setItem(21, new ItemStack(Material.LEATHER));
                gui.setItem(28, new ItemStack(Material.LEATHER));
                gui.setItem(29, new ItemStack(Material.LEATHER));
                gui.setItem(30, new ItemStack(Material.LEATHER));

                ItemStack leatherInfo = createGuideItem(Material.CRAFTING_TABLE,
                    "§e§lCrafting Recipe",
                    "§7Place in Crafting Table:",
                    "§7Pattern: L S L",
                    "§7         L C L",
                    "§7         L L L",
                    "§7(L=Leather, S=String, C=Chest)");
                gui.setItem(24, leatherInfo);
                break;

            case DIRT:
                // Show surrounding pattern
                Material[] dirt = {Material.DIRT, Material.DIRT, Material.DIRT,
                    Material.DIRT, null, Material.DIRT,
                    Material.DIRT, Material.DIRT, Material.DIRT};
                int[] dirtSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                for (int i = 0; i < dirtSlots.length; i++) {
                    if (dirt[i] != null) {
                        gui.setItem(dirtSlots[i], new ItemStack(dirt[i]));
                    } else {
                        gui.setItem(dirtSlots[i],
                            BackpackItem.createBackpack(BackpackTier.LEATHER));
                    }
                }

                ItemStack dirtInfo = createGuideItem(Material.CRAFTING_TABLE,
                    "§e§lCrafting Recipe",
                    "§7Surround Leather Backpack",
                    "§7with 8x Dirt",
                    "",
                    "§7This is a downgrade from Leather",
                    "§7(smaller storage)");
                gui.setItem(24, dirtInfo);
                break;

            case COPPER:
            case IRON:
            case GOLD:
            case DIAMOND:
                // Show upgrade pattern
                BackpackTier previous =
                    BackpackTier.fromLevel(tier.getLevel() - 1);
                Material upgradeMat = tier.getUpgradeMaterial();

                Material[] pattern = {upgradeMat, upgradeMat, upgradeMat,
                    upgradeMat, null, upgradeMat,
                    upgradeMat, upgradeMat, upgradeMat};
                int[] patternSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                for (int i = 0; i < patternSlots.length; i++) {
                    if (pattern[i] != null) {
                        gui.setItem(patternSlots[i], new ItemStack(pattern[i]));
                    } else {
                        gui.setItem(patternSlots[i],
                            BackpackItem.createBackpack(previous));
                    }
                }

                ItemStack upgradeInfo = createGuideItem(Material.CRAFTING_TABLE,
                    "§e§lUpgrade Recipe",
                    "§7Surround " + previous.getDisplayName(),
                    "§7with 8x " + getDisplayName(upgradeMat),
                    "",
                    "§7Your items are preserved!",
                    "§7UUID stays the same");
                gui.setItem(24, upgradeInfo);
                break;

            case NETHERITE:
                // Smithing table recipe
                gui.setItem(11, new ItemStack(
                    Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
                gui.setItem(20,
                    BackpackItem.createBackpack(BackpackTier.DIAMOND));
                gui.setItem(29, new ItemStack(Material.NETHERITE_INGOT));

                ItemStack netheriteInfo = createGuideItem(Material.SMITHING_TABLE,
                    "§e§lSmithing Table Recipe",
                    "§7Template: §8Netherite Upgrade",
                    "§7Base: §bDiamond Backpack",
                    "§7Material: §8Netherite Ingot",
                    "",
                    "§7Use Smithing Table to upgrade",
                    "§7Your items are preserved!");
                gui.setItem(24, netheriteInfo);
                break;

            case ENDERPACK:
                // Pattern: E P E / P C P / E I E
                gui.setItem(10, new ItemStack(Material.ENDER_EYE));
                gui.setItem(11, new ItemStack(Material.ENDER_PEARL));
                gui.setItem(12, new ItemStack(Material.ENDER_EYE));
                gui.setItem(19, new ItemStack(Material.ENDER_PEARL));
                gui.setItem(20, new ItemStack(Material.CHEST));
                gui.setItem(21, new ItemStack(Material.ENDER_PEARL));
                gui.setItem(28, new ItemStack(Material.ENDER_EYE));
                gui.setItem(29, new ItemStack(Material.IRON_BLOCK));
                gui.setItem(30, new ItemStack(Material.ENDER_EYE));

                ItemStack enderInfo = createGuideItem(Material.CRAFTING_TABLE,
                    "§e§lCrafting Recipe",
                    "§7Pattern: E P E",
                    "§7         P C P",
                    "§7         E I E",
                    "§7(E=Ender Eye, P=Pearl,",
                    "§7 C=Chest, I=Iron Block)");
                gui.setItem(24, enderInfo);

                // Clone recipe
                gui.setItem(14,
                    BackpackItem.createBackpack(BackpackTier.ENDERPACK));
                gui.setItem(15, new ItemStack(Material.ENDER_PEARL));

                ItemStack cloneResult =
                    BackpackItem.createBackpack(BackpackTier.ENDERPACK);
                cloneResult.setAmount(2);
                gui.setItem(17, cloneResult);

                ItemStack cloneInfo = createGuideItem(Material.ENDER_PEARL,
                    "§5§lCloning Recipe",
                    "§71 Enderpack + 1 Ender Pearl",
                    "§7= 2 Enderpacks (same ID!)",
                    "",
                    "§5Both share the same storage",
                    "§7Great for multiple access points!");
                gui.setItem(25, cloneInfo);
                break;
        }
    }

    /**
     * Add upgrade path information.
     *
     * @param gui the inventory GUI.
     * @param tier the backpack tier.
     */
    private static void addUpgradeInfo(Inventory gui, BackpackTier tier) {
        List<String> lore = new ArrayList<>();
        lore.add("§e§lUpgrade Path");
        lore.add("");

        // Previous tier
        if (tier.getLevel() > 0) {
            BackpackTier previous =
                BackpackTier.fromLevel(tier.getLevel() - 1);
            if (previous != null && previous != BackpackTier.DIRT) {
                lore.add("§7Upgraded from: " + previous.getDisplayName());
            }
        }

        // Next tier
        BackpackTier next = BackpackTier.fromLevel(tier.getLevel() + 1);
        if (next != null && tier != BackpackTier.ENDERPACK) {
            lore.add("§7Upgrades to: " + next.getDisplayName());
            lore.add("§7Material: §f"
                + getDisplayName(next.getUpgradeMaterial()));
        } else if (tier == BackpackTier.NETHERITE) {
            lore.add("§7This is the max tier!");
        } else if (tier == BackpackTier.ENDERPACK) {
            lore.add("§5Enderpack is special!");
            lore.add("§7Cannot be upgraded");
        }

        ItemStack upgradeInfo = createGuideItem(Material.EXPERIENCE_BOTTLE,
            lore.toArray(new String[0]));
        gui.setItem(32, upgradeInfo);
    }

    /**
     * Create a guide info item.
     *
     * @param material the material for the item.
     * @param lore the lore lines for the item.
     * @return the created item.
     */
    private static ItemStack createGuideItem(Material material, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (lore.length > 0) {
            meta.displayName(TextUtil.fromLegacy(lore[0]));
            if (lore.length > 1) {
                List<String> loreList = new ArrayList<>();
                for (int i = 1; i < lore.length; i++) {
                    loreList.add(lore[i]);
                }
                meta.lore(TextUtil.fromLegacy(loreList));
            }
        }

        if (!item.setItemMeta(meta)) {
            // Fallback if meta setting fails - return as-is
            return item;
        }
        return item;
    }

    /**
     * Get display name for a material.
     *
     * @param material the material.
     * @return the display name.
     */
    private static String getDisplayName(Material material) {
        String name = material.name().replace("_", " ").toLowerCase();
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(word.charAt(0)))
                .append(word.substring(1));
        }
        return result.toString();
    }

    /**
     * Handle GUI clicks.
     *
     * @param event the inventory click event.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof GuideInventoryHolder guideHolder)) {
            return;
        }

        event.setCancelled(true); // Cancel all clicks in guide GUI

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        // Main guide - clicking on backpack opens detail view
        if (guideHolder.getTier() == null) {
            if (BackpackItem.isBackpack(clicked)) {
                BackpackTier tier = BackpackItem.getBackpackTier(clicked);
                if (tier != null) {
                    openTierDetail(player, tier);
                    player.playSound(player.getLocation(),
                        org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                }
            }
        } else {
            // Detail view - back button
            if (clicked.getType() == Material.ARROW) {
                openMainGuide(player);
                player.playSound(player.getLocation(),
                    org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
        }
    }

    private static Component detailTitle(BackpackTier tier) {
        return TextUtil.fromLegacy(DETAIL_TITLE_PREFIX + tier.getDisplayName());
    }

    private static final class GuideInventoryHolder implements InventoryHolder {

        private final Inventory inventory;
        private final BackpackTier tier;

        private GuideInventoryHolder(Component title, BackpackTier tier) {
            this.tier = tier;
            this.inventory = Bukkit.createInventory(this, 54, title);
        }

        @Override
        @SuppressWarnings("EI_EXPOSE_REP")
        public Inventory getInventory() {
            return inventory;
        }

        private BackpackTier getTier() {
            return tier;
        }
    }
}

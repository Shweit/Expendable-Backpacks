package com.shweit.expendablebackpacks.commands;

import com.shweit.expendablebackpacks.gui.BackpackGuideGUI;
import com.shweit.expendablebackpacks.items.BackpackItem;
import com.shweit.expendablebackpacks.items.BackpackTier;
import com.shweit.expendablebackpacks.storage.BackpackManager;
import com.shweit.expendablebackpacks.util.TextUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Main command handler for all /backpack commands.
 */
public class BackpackCommand implements CommandExecutor, TabCompleter {

    private final BackpackManager backpackManager;

    /**
     * Creates a new backpack command handler.
     *
     * @param backpackManager the backpack manager instance
     */
    @SuppressWarnings("EI_EXPOSE_REP2")
    public BackpackCommand(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // No args - show help/guide
        if (args.length == 0) {
            showGuide(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "give":
                return handleGive(sender, args);
            case "open":
                return handleOpen(sender, args);
            case "clear":
                return handleClear(sender, args);
            case "clone":
                return handleClone(sender, args);
            case "help":
            case "?":
                showGuide(sender);
                return true;
            default:
                sender.sendMessage("§cUnknown subcommand. Use /backpack help");
                return true;
        }
    }

    /**
     * Show crafting guide and info.
     * Opens GUI for players, shows text for console.
     *
     * @param sender the command sender
     */
    private void showGuide(CommandSender sender) {
        // Open GUI for players
        if (sender instanceof Player) {
            BackpackGuideGUI.openMainGuide((Player) sender);
            return;
        }

        // Text guide for console
        sender.sendMessage("§6§l=== Backpack Plugin Guide ===");
        sender.sendMessage("");
        sender.sendMessage("§e§lBackpack Tiers:");
        for (BackpackTier tier : BackpackTier.values()) {
            sender.sendMessage("  " + tier.getDisplayName() + " §7- "
                + tier.getRows() + " rows (" + tier.getSlots() + " slots)");
        }
        sender.sendMessage("");
        sender.sendMessage("§e§lCrafting:");
        sender.sendMessage("  §7Leather Backpack: §fLeather + String + Chest");
        sender.sendMessage("  §7  Pattern: L S L / L C L / L L L");
        sender.sendMessage("  §7Dirt Backpack: §fSurround Leather BP with Dirt");
        sender.sendMessage("  §7Enderpack: §fEnder Eyes + Pearls + Chest + Iron Block");
        sender.sendMessage("  §7  Pattern: E P E / P C P / E I E");
        sender.sendMessage("");
        sender.sendMessage("§e§lUpgrades:");
        sender.sendMessage("  §7Surround backpack with 8x upgrade material");
        sender.sendMessage("  §7Leather → Copper: §6Copper Ingots");
        sender.sendMessage("  §7Copper → Iron: §fIron Ingots");
        sender.sendMessage("  §7Iron → Gold: §eGold Ingots");
        sender.sendMessage("  §7Gold → Diamond: §bDiamonds");
        sender.sendMessage("  §7Diamond → Netherite: §8Smithing Table");
        sender.sendMessage("");
        sender.sendMessage("§e§lEnderpack Cloning:");
        sender.sendMessage("  §7Place 1 Enderpack + 1 Ender Pearl in crafting");
        sender.sendMessage("  §7Get 2 Enderpacks with §5shared storage");
        sender.sendMessage("");
        sender.sendMessage("§7Right-click any backpack to open it!");
    }

    /**
     * Handle the give subcommand.
     * Command: /backpack give player tier.
     *
     * @param sender the command sender
     * @param args command arguments
     * @return true if handled successfully
     */
    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("backpack.give")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /backpack give <player> <tier>");
            sender.sendMessage(
                "§cTiers: dirt, leather, copper, iron, gold, diamond, netherite, enderpack");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + args[1]);
            return true;
        }

        BackpackTier tier = BackpackTier.fromName(args[2]);
        if (tier == null) {
            sender.sendMessage("§cInvalid tier: " + args[2]);
            sender.sendMessage(
                "§cAvailable: dirt, leather, copper, iron, gold, diamond, netherite, enderpack");
            return true;
        }

        ItemStack backpack = BackpackItem.createBackpack(tier);
        target.getInventory().addItem(backpack);

        sender.sendMessage("§aGave " + tier.getDisplayName() + " §ato " + target.getName());
        target.sendMessage("§aYou received a " + tier.getDisplayName());
        return true;
    }

    /**
     * Handle the open subcommand.
     * Command: /backpack open uuid.
     *
     * @param sender the command sender
     * @param args command arguments
     * @return true if handled successfully
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private boolean handleOpen(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        if (!sender.hasPermission("backpack.openOthers")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /backpack open <uuid>");
            return true;
        }

        UUID backpackUUID;
        try {
            backpackUUID = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid UUID format.");
            return true;
        }

        if (!backpackManager.backpackExists(backpackUUID)) {
            sender.sendMessage("§cNo backpack found with that UUID.");
            return true;
        }

        Player player = (Player) sender;
        // Open with generic title since we don't know the tier
        Inventory inventory = backpackManager.getInventory(
            backpackUUID, TextUtil.fromLegacy("§7Backpack"), 27);
        player.openInventory(inventory);
        player.sendMessage("§aOpened backpack: §7"
            + backpackUUID.toString().substring(0, 8) + "...");
        return true;
    }

    /**
     * Handle the clear subcommand.
     * Command: /backpack clear uuid.
     *
     * @param sender the command sender
     * @param args command arguments
     * @return true if handled successfully
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private boolean handleClear(CommandSender sender, String[] args) {
        if (!sender.hasPermission("backpack.clear")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /backpack clear <uuid>");
            return true;
        }

        UUID backpackUUID;
        try {
            backpackUUID = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid UUID format.");
            return true;
        }

        backpackManager.clearInventory(backpackUUID);
        sender.sendMessage("§aCleared backpack: §7"
            + backpackUUID.toString().substring(0, 8) + "...");
        return true;
    }

    /**
     * Handle the clone subcommand.
     * Command: /backpack clone uuid.
     *
     * @param sender the command sender
     * @param args command arguments
     * @return true if handled successfully
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private boolean handleClone(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        if (!sender.hasPermission("backpack.clone")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /backpack clone <uuid>");
            sender.sendMessage("§cNote: Only Enderpacks can be cloned.");
            return true;
        }

        UUID backpackUUID;
        try {
            backpackUUID = UUID.fromString(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid UUID format.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack enderpack = BackpackItem.createBackpack(BackpackTier.ENDERPACK, backpackUUID);

        player.getInventory().addItem(enderpack);
        player.sendMessage("§5Cloned Enderpack with ID: §7"
            + backpackUUID.toString().substring(0, 8) + "...");
        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Subcommand completions
            completions.addAll(Arrays.asList("give", "open", "clear", "clone", "help"));
        } else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "give":
                    // Show online players
                    return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());

                case "open":
                case "clear":
                case "clone":
                    // Show all backpack UUIDs
                    return getBackpackUUIDCompletions();

                default:
                    break;
            }
        } else if (args.length == 3) {
            String subcommand = args[0].toLowerCase();

            if (subcommand.equals("give")) {
                // Show tier names for give command
                return Arrays.stream(BackpackTier.values())
                    .map(tier -> tier.name().toLowerCase())
                    .collect(Collectors.toList());
            }
        }

        return completions.stream()
            .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
            .collect(Collectors.toList());
    }

    /**
     * Get tab completions for backpack UUIDs.
     * Returns full UUID strings from storage.
     *
     * @return list of UUID strings
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    private List<String> getBackpackUUIDCompletions() {
        return backpackManager.getAllBackpackUUIDs().stream()
            .map(UUID::toString)
            .collect(Collectors.toList());
    }
}

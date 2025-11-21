package com.shweit.expendablebackpacks.listeners;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.shweit.expendablebackpacks.items.BackpackItem;
import com.shweit.expendablebackpacks.items.BackpackTier;
import com.shweit.expendablebackpacks.util.BackpackBlockUtil;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles placement, breaking, and protection of backpack blocks.
 */
public class BackpackBlockListener implements Listener {

    /**
     * Handles placement of backpack items as blocks.
     *
     * @param event the block place event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();

        // Check if placing a backpack
        if (!BackpackItem.isBackpack(item)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
        UUID backpackUUID = BackpackItem.getBackpackUUID(item);
        BackpackTier tier = BackpackItem.getBackpackTier(item);

        if (backpackUUID == null || tier == null) {
            player.sendMessage("§cError: Invalid backpack data!");
            event.setCancelled(true);
            return;
        }

        // Ensure the block is a player head
        if (block.getType() != Material.PLAYER_HEAD
            && block.getType() != Material.PLAYER_WALL_HEAD) {
            return;
        }

        // Set the skull texture and store backpack data
        if (block.getState() instanceof Skull skull) {
            // Set the texture using Paper's profile API
            String textureValue = tier.getTextureValue();
            if (textureValue != null && !textureValue.startsWith("PLACEHOLDER")) {
                try {
                    PlayerProfile profile = Bukkit.createProfile(backpackUUID);
                    profile.getProperties().add(
                        new ProfileProperty("textures", textureValue));
                    skull.setPlayerProfile(profile);
                } catch (Exception e) {
                    player.sendMessage("§cError: Failed to set backpack texture!");
                    event.setCancelled(true);
                    return;
                }
            }

            // Store UUID and tier in the block's PDC
            if (!BackpackBlockUtil.setBlockData(block, backpackUUID, tier)) {
                player.sendMessage("§cError: Failed to store backpack data!");
                event.setCancelled(true);
                return;
            }

            player.sendMessage("§7Backpack placed! §8(Right-click to open)");
        }
    }

    /**
     * Handles breaking of backpack blocks.
     *
     * @param event the block break event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Check if breaking a backpack block
        if (!BackpackBlockUtil.isBackpackBlock(block)) {
            return;
        }

        @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
        UUID backpackUUID = BackpackBlockUtil.getBackpackUUIDFromBlock(block);
        BackpackTier tier = BackpackBlockUtil.getBackpackTierFromBlock(block);

        if (backpackUUID == null || tier == null) {
            return;
        }

        // Cancel default drops
        event.setDropItems(false);

        // Create and drop the backpack item with preserved UUID
        ItemStack backpackItem = BackpackItem.createBackpack(tier, backpackUUID);
        Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().dropItemNaturally(dropLocation, backpackItem);
    }

    /**
     * Protects backpack blocks from entity explosions (Creeper, TNT, etc.).
     *
     * @param event the entity explode event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        blocks.removeIf(BackpackBlockUtil::isBackpackBlock);
    }

    /**
     * Protects backpack blocks from block explosions (Bed, Respawn Anchor, etc.).
     *
     * @param event the block explode event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        List<Block> blocks = event.blockList();
        blocks.removeIf(BackpackBlockUtil::isBackpackBlock);
    }

    /**
     * Protects backpack blocks from burning.
     *
     * @param event the block burn event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (BackpackBlockUtil.isBackpackBlock(block)) {
            event.setCancelled(true);
        }
    }

    /**
     * Protects backpack blocks from being pushed by pistons.
     *
     * @param event the block piston extend event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (BackpackBlockUtil.isBackpackBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Protects backpack blocks from being pulled by sticky pistons.
     *
     * @param event the block piston retract event
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (BackpackBlockUtil.isBackpackBlock(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}

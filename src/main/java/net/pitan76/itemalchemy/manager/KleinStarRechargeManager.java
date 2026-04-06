package net.pitan76.itemalchemy.manager;

import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.config.RechargeConfig;
import net.pitan76.itemalchemy.item.KleinStar;
import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvents;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.PlatformUtil;
import net.pitan76.mcpitanlib.api.util.inventory.CompatPlayerInventory;
import net.pitan76.mcpitanlib.api.util.particle.CompatParticleTypes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Manages automatic recharging of chargeable items from Klein Star EMC storage.
 */
public class KleinStarRechargeManager {

    // DEBUG flag
    private static final boolean DEBUG_RECHARGE = PlatformUtil.isDevelopmentEnvironment();
    
    // Track last recharge tick per player to avoid multiple recharges per tick
    // Use WeakHashMap to allow garbage collection of player entries
    private static final Map<String, Integer> playerTickCounters = new WeakHashMap<>();
    
    /**
     * Called every player tick to handle automatic recharging.
     * @param player the player to recharge items for
     */
    public static void tryRechargeItems(Player player) {
        if (player == null || player.getWorld() == null) return;
        
        // Get or create tick counter for this player
        String playerId = player.getUUID().toString();
        int currentTick = playerTickCounters.getOrDefault(playerId, 0);
        currentTick++;
        
        if (currentTick < RechargeConfig.RECHARGE_RATE) {
            playerTickCounters.put(playerId, currentTick);
            return;
        }
        
        // Reset counter and perform recharge
        playerTickCounters.put(playerId, 0);
        
        if (DEBUG_RECHARGE) {
            System.out.println("[DEBUG-RECHARGE] tryRechargeItems called for player " + player.getName());
        }
        rechargeItemsFromKleinStar(player);
    }
    
    /**
     * Main recharge logic: finds Klein Stars and chargeable items, then transfers EMC.
     * @param player the player to recharge items for
     */
    public static void rechargeItemsFromKleinStar(Player player) {
        List<ItemStack> kleinStars = findKleinStars(player);
        if (kleinStars.isEmpty()) {
            if (DEBUG_RECHARGE) {
                System.out.println("[DEBUG-RECHARGE] NO KLEIN STAR - skipping recharge for " + player.getName());
            }
            return;
        }
        
        if (DEBUG_RECHARGE) {
            System.out.println("[DEBUG-RECHARGE] Found " + kleinStars.size() + " Klein Stars with EMC");
        }
        
        List<ChargeableItem> chargeableItems = findChargeableItems(player);
        if (chargeableItems.isEmpty()) return;
        
        // Sort by priority (lower charge = higher priority)
        chargeableItems.sort(Comparator
            .comparingInt((ChargeableItem item) -> item.currentCharge));
        
        int chargesPerformed = 0;
        
        for (ChargeableItem chargeableItem : chargeableItems) {
            if (chargesPerformed >= RechargeConfig.MAX_CHARGES_PER_TICK) break;
            
            // Skip if already max charge
            if (chargeableItem.currentCharge >= chargeableItem.maxCharge) continue;
            
            // Calculate EMC needed for one charge level
            int emcNeeded = chargeableItem.emcPerChargeLevel;
            
            // Try to extract from available Klein Stars
            for (ItemStack starStack : kleinStars) {
                // Skip Klein Stars with auto-charge disabled
                if (!KleinStar.isAutoChargeEnabled(starStack)) {
                    continue;
                }
                
                long extracted = KleinStar.extractEmc(starStack, emcNeeded);
                
                if (extracted > 0) {
                    // Calculate how many charge levels we can afford
                    int chargeLevels = (int) Math.min(
                        chargeableItem.maxCharge - chargeableItem.currentCharge,
                        extracted / chargeableItem.emcPerChargeLevel
                    );
                    
                    if (chargeLevels > 0) {
                        // Refund unused EMC
                        long unusedEmc = extracted - ((long) chargeLevels * chargeableItem.emcPerChargeLevel);
                        if (unusedEmc > 0) {
                            KleinStar.addEmc(starStack, unusedEmc);
                        }
                        
                        // Apply charge
                        int newCharge = chargeableItem.currentCharge + chargeLevels;
                        ItemUtils.setCharge(chargeableItem.stack, newCharge);
                        
                        if (DEBUG_RECHARGE) {
                            System.out.println("[DEBUG-RECHARGE] CHARGED item " + chargeableItem.stack.getItem().getClass().getSimpleName() + " from " + chargeableItem.currentCharge + " to " + newCharge);
                        }
                        
                        // Feedback
                        if (RechargeConfig.VISUAL_FEEDBACK) {
                            spawnChargeParticles(player, chargeableItem.stack);
                        }
                        if (RechargeConfig.AUDIO_FEEDBACK) {
                            playChargeSound(player);
                        }
                        
                        chargesPerformed++;
                        chargeableItem.currentCharge = newCharge;
                        
                        // Break to process next item on next tick (spread load)
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Finds all Klein Stars in player's inventory (main hand, offhand, armor, inventory) with stored EMC.
     * @param player the player to search
     * @return list of Klein Star ItemStacks with EMC
     */
    private static List<ItemStack> findKleinStars(Player player) {
        List<ItemStack> stars = new ArrayList<>();
        
        // Check main hand and offhand
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();
        
        if (ItemStackUtil.getItem(mainHand) instanceof KleinStar && KleinStar.getStoredEmc(mainHand) > 0) {
            stars.add(mainHand);
        }
        if (ItemStackUtil.getItem(offHand) instanceof KleinStar && KleinStar.getStoredEmc(offHand) > 0) {
            stars.add(offHand);
        }
        
        // Check inventory
        CompatPlayerInventory inventory = new CompatPlayerInventory(player.getInventory());
        for (int i = 0; i < inventory.callSize(); i++) {
            ItemStack stack = inventory.callGetStack(i);
            if (ItemStackUtil.getItem(stack) instanceof KleinStar && KleinStar.getStoredEmc(stack) > 0) {
                stars.add(stack);
            }
        }
        
        return stars;
    }
    
    /**
     * Finds all chargeable items that need recharging.
     * @param player the player to search
     * @return list of ChargeableItem objects
     */
    private static List<ChargeableItem> findChargeableItems(Player player) {
        List<ChargeableItem> items = new ArrayList<>();
        
        // Check all inventory slots
        CompatPlayerInventory inventory = new CompatPlayerInventory(player.getInventory());
        for (int i = 0; i < inventory.callSize(); i++) {
            ItemStack stack = inventory.callGetStack(i);
            
            if (ItemStackUtil.getItem(stack) instanceof IRechargeableFromKlein) {
                IRechargeableFromKlein rechargeableItem = (IRechargeableFromKlein) ItemStackUtil.getItem(stack);

                int charge = ItemUtils.getCharge(stack);
                int maxCharge = rechargeableItem.getMaxCharge();
                
                // Skip if max charge
                if (charge >= maxCharge) continue;
                
                // Skip selected item if config requires inactive-only charging
                if (RechargeConfig.ONLY_WHEN_INACTIVE && i == inventory.getSelectedSlot()) {
                    continue;
                }
                
                // Get item-specific EMC cost
                int emcPerLevel = rechargeableItem.getEmcCostPerCharge();
                
                items.add(new ChargeableItem(stack, charge, maxCharge, emcPerLevel));
            }
        }
        
        return items;
    }
    
    private static void spawnChargeParticles(Player player, ItemStack chargedItem) {
        // Spawn particles at player position
        double x = player.getX();
        double y = player.getY() + 1.0;
        double z = player.getZ();
        
        for (int i = 0; i < 10; i++) {
            player.getMidohraWorld().addParticle(CompatParticleTypes.ENCHANT,
                x + (Math.random() - 0.5), y + (Math.random() - 0.5) * 0.5, z + (Math.random() - 0.5),
                0, 0.01, 0
            );
        }
    }
    
    private static void playChargeSound(Player player) {
        // Use player.playSound instead of world.playSound for correct method signature
        player.playSound(CompatSoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f);
    }
    
    /**
     * Internal data class for tracking chargeable items during recharge processing.
     */
    private static class ChargeableItem {
        final ItemStack stack;
        int currentCharge;
        final int maxCharge;
        final int emcPerChargeLevel;
        
        ChargeableItem(ItemStack stack, int currentCharge, int maxCharge, int emcPerChargeLevel) {
            this.stack = stack;
            this.currentCharge = currentCharge;
            this.maxCharge = maxCharge;
            this.emcPerChargeLevel = emcPerChargeLevel;
        }
    }
}

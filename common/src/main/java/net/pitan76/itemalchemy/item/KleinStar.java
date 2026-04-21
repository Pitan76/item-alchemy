package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarColorArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarStepArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemUseEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.StackActionResult;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;

public class KleinStar extends CompatItem {

    public static final String EMC_KEY = "stored_emc";
    public static final String AUTO_CHARGE_KEY = "auto_charge_enabled";

    public enum Tier {
        EIN(50_000L),
        ZWEI(200_000L),
        DREI(800_000L),
        VIER(3_200_000L),
        SPHERE(12_800_000L),
        OMEGA(51_200_000L);

        private final long maxEmc;

        Tier(long maxEmc) {
            this.maxEmc = maxEmc;
        }

        public long getMaxEmc() {
            return maxEmc;
        }
    }

    private final Tier tier;

    public KleinStar(Tier tier, CompatibleItemSettings settings) {
        super(settings);
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    public long getMaxEmc() {
        return tier.getMaxEmc();
    }

    public static long getStoredEmc(ItemStack stack) {
        NbtCompound nbt = stack.getCustomNbtM().getCompound(ItemAlchemy.MOD_ID);
        if (!nbt.has(EMC_KEY)) {
            return 0;
        }
        return nbt.getLong(EMC_KEY);
    }

    public static void setStoredEmc(ItemStack stack, long emc) {
        NbtCompound nbt = stack.getCustomNbtM();
        NbtCompound customNbt = nbt.getCompound(ItemAlchemy.MOD_ID);

        customNbt.putLong(EMC_KEY, emc);

        nbt.put(ItemAlchemy.MOD_ID, customNbt);
        stack.setCustomNbt(nbt);
    }

    /**
     * Adds EMC to the Klein Star. Returns the amount actually added.
     */
    public static long addEmc(ItemStack stack, long amount) {
        if (!stack.getItem().instanceOf(KleinStar.class)) return 0;
        KleinStar star = stack.getItem().getCompatItem(KleinStar.class);

        long stored = getStoredEmc(stack);
        long max = star.getMaxEmc();
        long space = max - stored;
        long toAdd = Math.min(amount, space);

        if (toAdd > 0) {
            setStoredEmc(stack, stored + toAdd);
        }
        return toAdd;
    }

    /**
     * Extracts EMC from the Klein Star. Returns the amount actually extracted.
     */
    public static long extractEmc(ItemStack stack, long amount) {
        long stored = getStoredEmc(stack);
        long toExtract = Math.min(amount, stored);

        if (toExtract > 0) {
            setStoredEmc(stack, stored - toExtract);
        }
        return toExtract;
    }

    /**
     * Get whether auto-charge is enabled for this Klein Star
     */
    public static boolean isAutoChargeEnabled(ItemStack stack) {
        NbtCompound nbt = stack.getCustomNbtM();
        NbtCompound customNbt = nbt.getCompound(ItemAlchemy.MOD_ID);
        if (!customNbt.has(AUTO_CHARGE_KEY)) {
            return true; // Default to enabled
        }
        return customNbt.getBoolean(AUTO_CHARGE_KEY);
    }

    /**
     * Set whether auto-charge is enabled for this Klein Star
     */
    public static void setAutoChargeEnabled(ItemStack stack, boolean enabled) {
        NbtCompound nbt = stack.getCustomNbtM();
        NbtCompound customNbt = nbt.getCompound(ItemAlchemy.MOD_ID);

        customNbt.putBoolean(AUTO_CHARGE_KEY, enabled);

        nbt.put(ItemAlchemy.MOD_ID, customNbt);
        stack.setCustomNbt(nbt);
    }

    /**
     * Toggle auto-charge status for this Klein Star
     */
    public static boolean toggleAutoCharge(ItemStack stack) {
        boolean current = isAutoChargeEnabled(stack);
        setAutoChargeEnabled(stack, !current);
        return !current;
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e) {
        ItemStack stack = ItemStack.of(e.getStack()); // TODO: 直接MidohraのItemStackを取得できるようにする
        e.addTooltip(TooltipUtil.generateTooltipLines(ItemStackUtil.getItem(e.getStack())));
        
        long stored = getStoredEmc(stack);
        long max = getMaxEmc();
        e.addTooltip(TextUtil.literal("EMC: " + formatNumber(stored) + " / " + formatNumber(max)));
        
        // Show auto-charge status
        boolean autoCharge = isAutoChargeEnabled(stack);
        String autoChargeStatus = autoCharge ? "§aON" : "§cOFF";
        e.addTooltip(TextUtil.literal("Auto-charge: " + autoChargeStatus + " §7(Sneak+Right-Click to toggle)"));
    }

    @Override
    public StackActionResult onRightClick(ItemUseEvent e) {
        if (e.isClient()) return e.consume();
        
        ItemStack stack = e.getStackM();
        
        if (e.isSneaking()) {
            // Toggle auto-charge
            boolean newState = toggleAutoCharge(stack);
            String message = newState ? "§aAuto-charge enabled" : "§cAuto-charge disabled";
            e.user.sendMessage(TextUtil.literal(message));
            return e.success();
        }
        
        return super.onRightClick(e);
    }

    @Override
    public boolean isItemBarVisible(ItemBarVisibleArgs args) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemBarStepArgs args) {
        long stored = getStoredEmc(ItemStack.of(args.getStack())); // TODO: 直接MidohraのItemStackを取得できるようにする
        long max = getMaxEmc();
        if (max == 0) return 0;
        return (int) (13L * stored / max);
    }

    @Override
    public int getItemBarColor(ItemBarColorArgs args) {
        // Cyan/teal color for EMC bar
        return 0x00CCFF;
    }

    private static String formatNumber(long number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000.0);
        }
        return String.valueOf(number);
    }
}

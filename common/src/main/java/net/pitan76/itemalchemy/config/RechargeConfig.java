package net.pitan76.itemalchemy.config;

/**
 * Configuration values for the Klein Star automatic recharging system.
 */
public class RechargeConfig {
    /**
     * Number of ticks between recharge attempts.
     * Default: 20 ticks (1 second at 20 TPS)
     */
    public static int RECHARGE_RATE = 20;
    
    /**
     * Multiplier for EMC cost per charge level.
     * Default: 1.0 (no multiplier)
     */
    public static double EMC_COST_MULTIPLIER = 1.0;
    
    /**
     * Enable visual feedback (particles) when recharging.
     * Default: true
     */
    public static boolean VISUAL_FEEDBACK = true;
    
    /**
     * Enable audio feedback when recharging.
     * Default: true
     */
    public static boolean AUDIO_FEEDBACK = true;
    
    /**
     * Only recharge items that are NOT in the player's main hand.
     * When false, all items including the active one will be recharged.
     * Default: false
     */
    public static boolean ONLY_WHEN_INACTIVE = false;
    
    /**
     * Maximum number of charge operations per tick.
     * This prevents lag when many items need recharging.
     * Default: 1
     */
    public static int MAX_CHARGES_PER_TICK = 1;
}

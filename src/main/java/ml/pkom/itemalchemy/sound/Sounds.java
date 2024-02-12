package ml.pkom.itemalchemy.sound;

import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.mcpitanlibarch.api.event.registry.RegistryResult;
import net.minecraft.sound.SoundEvent;

public class Sounds {
    public static final RegistryResult<SoundEvent> EXCHANGE_SOUND = register("exchange");
    public static final RegistryResult<SoundEvent> CHARGE_SOUND = register("charge");
    public static final RegistryResult<SoundEvent> UNCHARGE_SOUND = register("uncharge");

    public static void init(){

    }
    private static RegistryResult<SoundEvent> register(String id) {
        return ItemAlchemy.registry.registerSoundEvent(ItemAlchemy.id(id));
    }
}

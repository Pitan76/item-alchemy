package net.pitan76.itemalchemy.sound;

import net.minecraft.sound.SoundEvent;
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Sounds {
    public static final RegistryResult<SoundEvent> EXCHANGE_SOUND = register("exchange");
    public static final RegistryResult<SoundEvent> CHARGE_SOUND = register("charge");
    public static final RegistryResult<SoundEvent> UNCHARGE_SOUND = register("uncharge");

    public static void init() {

    }
    private static RegistryResult<SoundEvent> register(String id) {
        return registry.registerSoundEvent(_id(id));
    }
}

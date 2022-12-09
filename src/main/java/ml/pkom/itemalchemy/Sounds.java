package ml.pkom.itemalchemy;

import ml.pkom.mcpitanlibarch.api.event.registry.RegistryEvent;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Sounds {
    public static final RegistryEvent<SoundEvent> EXCHANGE_SOUND = register("exchange");

    public static void init(){

    }
    private static RegistryEvent<SoundEvent> register(String id) {
        return ItemAlchemy.registry.registerSoundEvent(ItemAlchemy.id(id));
    }
}

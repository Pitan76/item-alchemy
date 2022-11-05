package ml.pkom.itemalchemy;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Sounds {
    public static final Identifier EXCHANGE_SOUND = ItemAlchemy.id("exchange");
    public static SoundEvent EXCHANGE_SOUND_EVENT = new SoundEvent(EXCHANGE_SOUND);


    public static void init(){
        Registry.register(Registry.SOUND_EVENT, EXCHANGE_SOUND, EXCHANGE_SOUND_EVENT);
    }
}

package net.pitan76.itemalchemy.sound;

import net.pitan76.mcpitanlib.api.sound.CompatSoundEvent;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Sounds {
    public static final CompatSoundEvent EXCHANGE_SOUND = register("exchange");
    public static final CompatSoundEvent CHARGE_SOUND = register("charge");
    public static final CompatSoundEvent UNCHARGE_SOUND = register("uncharge");

    public static void init() {

    }

    private static CompatSoundEvent register(String id) {
        return registry.registerCompatSoundEvent(_id(id));
    }
}

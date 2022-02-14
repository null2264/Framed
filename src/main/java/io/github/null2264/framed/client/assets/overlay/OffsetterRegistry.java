package io.github.null2264.framed.client.assets.overlay;

import com.mojang.serialization.Codec;
import io.github.null2264.framed.util.Identifiable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class OffsetterRegistry
{
    private static final Map<Identifier, Identifiable<Codec<Offsetter>>> registeredOffsetters = new HashMap<>();

    private OffsetterRegistry() {
    }

    public static void register(final Identifier id, final Codec<Offsetter> codec) {
        registeredOffsetters.put(id, new Identifiable<>(codec, id));
    }

    public static Identifiable<Codec<Offsetter>> get(final Identifier id) {
        return registeredOffsetters.get(id);
    }
}

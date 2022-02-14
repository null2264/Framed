package io.github.null2264.framed.client.assets.overlay;

import io.github.null2264.framed.client.util.ToOptional;
import io.github.null2264.framed.util.Float4;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.null2264.framed.Framed.META;

@Environment(EnvType.CLIENT)
public interface Offsetter extends ToOptional<Offsetter>
{
    Identifier NONE_ID = META.id("none");
    Offsetter NONE = new Offsetter()
    {
        @Override
        public Optional<Offsetter> toOptional() {
            return Optional.empty();
        }

        @Override
        public <T> T match(final Function<Offsetter, T> some, final Supplier<T> none) {
            return none.get();
        }

        @Override
        public Float4 offset(final Float4 original) {
            return original;
        }

        @Override
        public Identifier getId() {
            return NONE_ID;
        }
    };

    Float4 offset(Float4 original);

    Identifier getId();
}

package io.github.null2264.framed.client.assets.overlay;

import com.mojang.serialization.Codec;
import io.github.null2264.framed.client.util.ToOptional;
import io.github.null2264.framed.util.Float4;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.null2264.framed.client.FramedClient.CODECS;

@Environment(EnvType.CLIENT)
public abstract class SidedOffsetters
{
    public static final Codec<SidedOffsetters.Base> CODEC = Some.CODEC.xmap(some -> some, base -> (Some) base);
    public static final Base NONE = new Base()
    {
        @Override
        public Optional<Base> toOptional() {
            return Optional.empty();
        }

        @Override
        public Float4 applyUs(final Float4 origUs, final Direction dir) {
            return origUs;
        }

        @Override
        public Float4 applyVs(final Float4 origVs, final Direction dir) {
            return origVs;
        }

        @Override
        public <T> T match(final Function<Base, T> some, final Supplier<T> none) {
            return none.get();
        }
    };

    private SidedOffsetters() {
    }

    public interface Base extends ToOptional<Base>
    {
        Float4 applyUs(Float4 origUs, Direction dir);

        Float4 applyVs(Float4 origVs, Direction dir);
    }

    public static class Some implements Base, ToOptional.Some<Base>
    {
        public static final Codec<SidedOffsetters.Some> CODEC = CODECS.sidedMapOf(Offsetters.CODEC).xmap(SidedOffsetters.Some::new, so -> so.map);

        private final Map<Direction, Offsetters> map;

        public Some(final Map<Direction, Offsetters> map) {
            for (final Direction dir : Direction.values()) {
                map.putIfAbsent(dir, Offsetters.NONE);
            }
            this.map = map;
        }

        @Override
        public Float4 applyUs(final Float4 origUs, final Direction dir) {
            return map.get(dir).u.offset(origUs);
        }

        @Override
        public Float4 applyVs(final Float4 origVs, final Direction dir) {
            return map.get(dir).v.offset(origVs);
        }
    }
}

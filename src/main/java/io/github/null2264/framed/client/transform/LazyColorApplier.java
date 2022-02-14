package dev.alexnader.framed.client.transform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;

@Environment(EnvType.CLIENT)
public abstract class LazyColorApplier {
    @SuppressWarnings("java:S1186")
    public static final LazyColorApplier NONE = new LazyColorApplier() {
        @Override
        public void apply(final MutableQuadView mqv, final int color) { }
    };
    
    public abstract void apply(MutableQuadView mqv, int color);

    public static class Some extends LazyColorApplier {
        @Override
        public void apply(final MutableQuadView mqv, final int color) {
            mqv.spriteColor(0, color, color, color, color);
        }
    }
}

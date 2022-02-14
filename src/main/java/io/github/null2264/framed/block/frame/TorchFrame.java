package io.github.null2264.framed.block.frame;

import net.minecraft.block.TorchBlock;
import net.minecraft.particle.ParticleTypes;

public class TorchFrame extends TorchBlock {
    public TorchFrame(final Settings settings) {
        super(settings, ParticleTypes.FLAME);
    }
}

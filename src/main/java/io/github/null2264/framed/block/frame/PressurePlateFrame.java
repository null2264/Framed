package io.github.null2264.framed.block.frame;

import net.minecraft.block.PressurePlateBlock;

public class PressurePlateFrame extends PressurePlateBlock {
    public PressurePlateFrame(final Settings settings) {
        super(ActivationRule.MOBS, settings);
    }
}

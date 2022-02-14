package io.github.null2264.framed.mixin.local;

import io.github.null2264.framed.block.FrameSlotInfo;
import io.github.null2264.framed.block.entity.FrameBlockEntity;
import io.github.null2264.framed.block.frame.*;
import io.github.null2264.framed.block.frame.data.Sections;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

import static io.github.null2264.framed.Framed.META;

@Mixin({
    BlockFrame.class,
    StairsFrame.class,
    FenceFrame.class,
    FenceGateFrame.class,
    TrapdoorFrame.class,
    DoorFrame.class,
    PathFrame.class,
    TorchFrame.class,
    WallTorchFrame.class,
    PressurePlateFrame.class,
    WallFrame.class,
    LayerFrame.class,
    CarpetFrame.class,
    PaneFrame.class
})
public abstract class SinglePartFrame implements FrameSlotInfo
{
    private SinglePartFrame() {
        throw new IllegalStateException("Mixin constructor should not run.");
    }

    @Override
    public Sections sections() {
        return META.FRAME_SECTIONS;
    }

    @Override
    public int getRelativeSlotAt(final Vec3d posInBlock, final Direction side) {
        return 0;
    }

    @Override
    public boolean absoluteSlotIsValid(final FrameBlockEntity frame, final BlockState state, final int slot) {
        return frame.sections().containsSlot(slot);
    }
}

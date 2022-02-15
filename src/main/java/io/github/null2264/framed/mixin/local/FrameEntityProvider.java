package io.github.null2264.framed.mixin.local;

import io.github.null2264.framed.block.entity.FrameBlockEntity;
import io.github.null2264.framed.block.frame.*;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

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
public class FrameEntityProvider implements BlockEntityProvider
{
    private FrameEntityProvider() {
        throw new IllegalStateException("Mixin constructor should not run.");
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
        return new FrameBlockEntity(pos, state, META.FRAME_SECTIONS);
    }
}

package io.github.null2264.framed.block;

import io.github.null2264.framed.block.entity.FrameBlockEntity;
import io.github.null2264.framed.block.frame.data.Sections;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public interface FrameSlotInfo
{
    Sections sections();

    int getRelativeSlotAt(Vec3d posInBlock, Direction side);

    boolean absoluteSlotIsValid(FrameBlockEntity frame, BlockState state, int slot);
}

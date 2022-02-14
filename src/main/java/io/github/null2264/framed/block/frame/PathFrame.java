package io.github.null2264.framed.block.frame;

import io.github.null2264.framed.block.PathBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public class PathFrame extends PathBlock
{
    public PathFrame(final Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideInvisible(final BlockState state, final BlockState stateFrom, final Direction direction) {
        return super.isSideInvisible(state, stateFrom, direction) || (state == stateFrom);
    }
}

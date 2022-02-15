package io.github.null2264.framed;

import io.github.null2264.framed.block.entity.FrameBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import static io.github.null2264.framed.Framed.BLOCKS;
import static io.github.null2264.framed.Framed.META;

public class FramedBlockEntityTypes extends Registrar<BlockEntityType<?>>
{
    public FramedBlockEntityTypes() {
        super(Registry.BLOCK_ENTITY_TYPE);
    }

    public final BlockEntityType<FrameBlockEntity> FRAME = register(
        FabricBlockEntityTypeBuilder.create(
            FrameBlockEntity::new,
            BLOCKS.BLOCK_FRAME,
            BLOCKS.STAIRS_FRAME,
            BLOCKS.FENCE_FRAME,
            BLOCKS.FENCE_GATE_FRAME,
            BLOCKS.TRAPDOOR_FRAME,
            BLOCKS.DOOR_FRAME,
            BLOCKS.PATH_FRAME,
            BLOCKS.TORCH_FRAME,
            BLOCKS.WALL_TORCH_FRAME,
            BLOCKS.PRESSURE_PLATE_FRAME,
            BLOCKS.WALL_FRAME,
            BLOCKS.LAYER_FRAME,
            BLOCKS.CARPET_FRAME,
            BLOCKS.PANE_FRAME,
            BLOCKS.SLAB_FRAME
        ).build(null),
        META.id("frame")
    );
}

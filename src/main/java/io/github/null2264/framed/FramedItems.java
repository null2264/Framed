package io.github.null2264.framed;

import io.github.null2264.framed.items.FramersHammer;
import net.minecraft.item.Item;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.registry.Registry;

import static io.github.null2264.framed.Framed.BLOCKS;
import static io.github.null2264.framed.Framed.META;

@SuppressWarnings("unused")
public class FramedItems extends Registrar<Item>
{
    public final FramersHammer FRAMERS_HAMMER = register(new FramersHammer(new Item.Settings().maxCount(1).group(META.MAIN_ITEM_GROUP)), META.id("framers_hammer"));
    public final WallStandingBlockItem TORCH_FRAME = register(
        new WallStandingBlockItem(BLOCKS.TORCH_FRAME, BLOCKS.WALL_TORCH_FRAME, new Item.Settings().group(META.MAIN_ITEM_GROUP)),
        META.id("torch_frame")
    );

    public FramedItems() {
        super(Registry.ITEM);
    }
}

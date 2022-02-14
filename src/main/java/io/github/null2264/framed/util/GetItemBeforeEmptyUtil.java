package io.github.null2264.framed.util;

import io.github.null2264.framed.mixin.mc.GetItemBeforeEmpty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GetItemBeforeEmptyUtil {
    private GetItemBeforeEmptyUtil() {
        throw new IllegalStateException("Should not instantiate utility class.");
    }

    public static Item getItemBeforeEmpty(final ItemStack stack) {
        //noinspection ConstantConditions
        return ((GetItemBeforeEmpty) (Object) stack).getItemBeforeEmpty();
    }
}

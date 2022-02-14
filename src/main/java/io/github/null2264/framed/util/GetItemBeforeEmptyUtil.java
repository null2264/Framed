package dev.alexnader.framed.util;

import dev.alexnader.framed.mixin.mc.GetItemBeforeEmpty;
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

package dev.alexnader.framed.mixin.mc;

import dev.alexnader.framed.Framed;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {
    @Inject(method = "canConnect", at = @At("HEAD"), cancellable = true)
    void connectToFenceFrame(final BlockState state, final boolean neighborIsFullSquare, final Direction dir, final CallbackInfoReturnable<Boolean> cir) {
        if (state.isOf(Framed.BLOCKS.FENCE_FRAME)) {
            cir.setReturnValue(true);
        }

        //noinspection ConstantConditions
        if ((Object) this == Framed.BLOCKS.FENCE_FRAME && state.getBlock() instanceof FenceBlock) {
            cir.setReturnValue(true);
        }
    }
}

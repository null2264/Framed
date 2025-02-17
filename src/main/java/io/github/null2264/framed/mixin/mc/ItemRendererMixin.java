package io.github.null2264.framed.mixin.mc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.null2264.framed.Framed.ITEMS;
import static io.github.null2264.framed.Framed.META;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
    @Final
    @Shadow
    private ItemModels models;

    @Shadow
    public abstract BakedModel getModel(ItemStack stack, World world, LivingEntity entity, int seed);

    @Redirect(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemModels;getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;"))
    BakedModel getModelProxy(final ItemModels itemModels, final ItemStack stack) {
        if (stack.getItem() == ITEMS.FRAMERS_HAMMER) {
            return itemModels.getModelManager().getModel(new ModelIdentifier(META.id("framers_hammer_none"), "inventory"));
        }
        return itemModels.getModel(stack);
    }

    @Redirect(method = "innerRenderInGui*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;"))
    BakedModel getHeldItemModelProxy(final ItemRenderer itemRenderer, final ItemStack stack, final World world, final LivingEntity entity, int seed) {
        if (stack.getItem() == ITEMS.FRAMERS_HAMMER) {
            final ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
            final BakedModel model = models.getModelManager().getModel(new ModelIdentifier(META.id("framers_hammer"), "inventory"));
            final BakedModel model2 = model.getOverrides().apply(model, stack, clientWorld, entity, seed);
            return model2 == null ? models.getModelManager().getMissingModel() : model2;
        }
        return this.getModel(stack, world, entity, seed);
    }
}

package dev.alexnader.framed;

import dev.alexnader.framed.block.frame.data.Sections;
import dev.alexnader.framed.gui.FrameGuiDescription;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dev.alexnader.framed.Framed.ITEMS;

public class FramedMeta {
    public final String NAMESPACE = "framed";

    public Identifier id(final String path) {
        return new Identifier(NAMESPACE, path);
    }

    public final Logger LOGGER = LogManager.getLogger("Framed");

    public final ScreenHandlerType<FrameGuiDescription> FRAME_SCREEN_HANDLER_TYPE =
        ScreenHandlerRegistry.registerExtended(
            id("frame"),
            (syncId, inventory, buf) -> new FrameGuiDescription(syncId, inventory, ScreenHandlerContext.create(inventory.player.world, buf.readBlockPos()))
        );

    public final Sections FRAME_SECTIONS = new Sections(1);
    public final Sections SLAB_FRAME_SECTIONS = new Sections(2);

    public final ItemGroup MAIN_ITEM_GROUP = FabricItemGroupBuilder
        .create(id("framed"))
        .icon(() -> new ItemStack(ITEMS.FRAMERS_HAMMER))
        .build();
}

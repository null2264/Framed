package io.github.null2264.framed.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.null2264.framed.block.entity.FrameBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

import static io.github.null2264.framed.Framed.META;
import static io.github.null2264.framed.Framed.SPECIAL_ITEMS;
import static io.github.null2264.framed.util.GuiUtil.*;

public class FrameGuiDescription extends SyncedGuiDescription
{
    public FrameGuiDescription(final int syncId, final PlayerInventory playerInventory, final ScreenHandlerContext context) {
        this(
            syncId,
            playerInventory,
            context,
            context.get((world, pos) -> (FrameBlockEntity) world.getBlockEntity(pos))
                .orElseThrow(() -> new IllegalArgumentException("FrameGuiDescription can only be used with FrameBlockEntity."))
        );
    }

    private FrameGuiDescription(final int syncId, final PlayerInventory playerInventory, final ScreenHandlerContext context, final FrameBlockEntity frame) {
        super(META.FRAME_SCREEN_HANDLER_TYPE, syncId, playerInventory, SyncedGuiDescription.getBlockInventory(context, frame.size()), SyncedGuiDescription.getBlockPropertyDelegate(context));

        final WGridPanel root = new WGridPanel(9);

        root.add(centered(label("gui.framed.frame.base_label")), 1, 2, 8, 2);

        root.add(
            slotRow(SingleItemSlots::new, blockInventory, frame.getSections().base()),
            5 - frame.getSections().base().size(), 4
        );

        root.add(centered(label("gui.framed.frame.overlay_label")), 9, 2, 8, 2);

        root.add(
            slotRow(SingleItemSlots::new, blockInventory, frame.getSections().overlay()),
            13 - frame.getSections().overlay().size(), 4
        );

        root.add(centered(label("gui.framed.frame.special_label")), 0, 6, 18, 2);

        SPECIAL_ITEMS.MAP.forEach((item, specialItem) -> root.add(
            new SingleItemSlots(blockInventory, frame.getSections().special().makeAbsolute(specialItem.offset()), 1, 1, false),
            9 - frame.getSections().special().size() + specialItem.offset() * 2, 8
        ));

        root.add(createPlayerInventoryPanel(), 0, 11);

        root.validate(this);
        rootPanel = root;
    }
}

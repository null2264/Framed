package io.github.null2264.framed.block.entity;

import com.mojang.datafixers.util.Pair;
import io.github.null2264.framed.Framed;
import io.github.null2264.framed.block.frame.data.FrameData;
import io.github.null2264.framed.block.frame.data.Sections;
import io.github.null2264.framed.gui.FrameGuiDescription;
import io.github.null2264.framed.items.SpecialItems;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.github.null2264.framed.Framed.META;
import static io.github.null2264.framed.Framed.SPECIAL_ITEMS;
import static io.github.null2264.framed.util.GetItemBeforeEmptyUtil.getItemBeforeEmpty;
import static io.github.null2264.framed.util.ValidQuery.checkIf;

public class FrameBlockEntity extends LockableContainerBlockEntity implements ExtendedScreenHandlerFactory, RenderAttachmentBlockEntity
{
    private FrameData data;

    public FrameBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(blockPos, blockState, META.FRAME_SECTIONS);
    }

    public FrameBlockEntity(BlockPos blockPos, BlockState blockState, final Sections sections) {
        super(Framed.BLOCK_ENTITY_TYPES.FRAME, blockPos, blockState);

        setData(sections);
    }

    public FrameData data() {
        return data;
    }

    public void setData(Sections sections) {
        data = new FrameData(sections);
    }

    public Sections getSections() {
        return data.getSections();
    }

    public Optional<ItemStack>[] items() {
        return data.getItems();
    }

    public List<Optional<ItemStack>> baseItems() {
        return data.baseItems();
    }

    public List<Optional<ItemStack>> overlayItems() {
        return data.overlayItems();
    }

    @SuppressWarnings("unused") // kept in case needed in the future
    public List<Optional<ItemStack>> specialItems() {
        return data.specialItems();
    }

    public Optional<BlockState>[] baseStates() {
        return data.baseStates();
    }

    public void copyFrom(final int slot, final ItemStack stack, final int count, final boolean take) {
        final ItemStack newStack = stack.copy();
        final int realCount = Math.min(count, stack.getCount());

        newStack.setCount(realCount);

        if (take) {
            stack.setCount(stack.getCount() - realCount);
        }

        this.setStack(slot, newStack);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean isValid(final int slot, final ItemStack stack) {
        switch (getSections().findSectionIndexOf(slot)) {
            case Sections.BASE_INDEX:
                return checkIf(stack).isValidForBase(s -> Optional.of(s.getBlock().getDefaultState()), world, pos).isPresent();
            case Sections.OVERLAY_INDEX:
                return checkIf(stack).isValidForOverlay();
            case Sections.SPECIAL_INDEX:
                return checkIf(stack).isValidForSpecial();
            default:
                return false;
        }
    }

    private void beforeRemove(final int slot) {
        switch (getSections().findSectionIndexOf(slot)) {
            case Sections.BASE_INDEX:
                baseStates()[getSections().base().makeRelative(slot)] = Optional.empty();
                break;
            case Sections.OVERLAY_INDEX:
                break;
            case Sections.SPECIAL_INDEX:
                //noinspection ConstantConditions
                SPECIAL_ITEMS.MAP.get(getItemBeforeEmpty(getStack(slot))).onRemove(world, this);
                break;
            default:
                throw new IllegalArgumentException("Invalid slot: " + slot);
        }
    }

    @Override
    public ItemStack removeStack(final int slot, final int amount) {
        beforeRemove(slot);

        return Optional.of(slot)
            .filter(s -> getSections().itemIndices().contains(s))
            .filter(s -> amount > 0)
            .flatMap(s -> items()[s])
            .map(orig -> new Pair<>(orig, orig.split(amount)))
            .map(pair -> {
                markDirty();
                if (pair.getFirst().isEmpty()) {
                    items()[slot] = Optional.empty();
                }
                return pair.getSecond();
            })
            .orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeStack(final int slot) {
        beforeRemove(slot);

        markDirty();

        final Optional<ItemStack> result = items()[slot];

        items()[slot] = Optional.empty();

        return result.orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStack(final int slot) {
        return items()[slot].orElse(ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return items().length;
    }

    @Override
    public boolean isEmpty() {
        return Arrays.stream(items()).noneMatch(Optional::isPresent);
    }

    @Override
    public boolean canPlayerUse(final PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0, size = size(); i < size; i++) {
            items()[i] = Optional.empty();
        }
    }

    @Override
    public void setStack(final int slot, final ItemStack stack) {
        final int sectionIndex = getSections().findSectionIndexOf(slot);

        final Runnable setStack = () -> {
            items()[slot] = Optional.of(stack);
            stack.setCount(Math.min(stack.getCount(), getMaxCountPerStack()));
            markDirty();
        };

        switch (sectionIndex) {
            case Sections.BASE_INDEX:
                setStack.run();
                final int baseSlot = getSections().base().makeRelative(slot);
                baseStates()[baseSlot] = baseItems().get(baseSlot)
                    .map(ItemStack::getItem)
                    .filter(i -> i instanceof BlockItem)
                    .map(i -> ((BlockItem) i).getBlock().getDefaultState());
                break;
            case Sections.SPECIAL_INDEX:
                final SpecialItems.SpecialItem old = SPECIAL_ITEMS.MAP.get(getItemBeforeEmpty(getStack(slot)));
                if (old != null && world != null) {
                    old.onRemove(world, this);
                }

                setStack.run();

                final SpecialItems.SpecialItem _new = SPECIAL_ITEMS.MAP.get(getStack(slot).getItem());
                if (_new != null && world != null) {
                    _new.onAdd(world, this);
                }
                break;
            default:
                setStack.run();
                break;
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();

        final World world = this.world;
        if (world != null) {
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();

            if (world.isClient) {
                MinecraftClient.getInstance().worldRenderer.updateBlock(world, pos, getCachedState(), state, 1);
            } else {
                // TODO: Find out wth is this sync function used for
                // sync();

                PlayerLookup.tracking(this).forEach(p -> p.networkHandler.sendPacket(this.toUpdatePacket()));

                world.updateNeighborsAlways(pos.offset(Direction.UP), block);
            }
        }
    }

    @Override
    public List<Pair<Optional<BlockState>, Optional<Identifier>>> getRenderAttachmentData() {
        return data.toRenderAttachment();
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        writeClientNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        readClientNbt(tag);
    }

    public void writeClientNbt(NbtCompound tag) {
        tag.put("frameData", data.writeNbt());
    }

    public void readClientNbt(NbtCompound NbtCompound) {
        data = FrameData.readNbt(NbtCompound.getCompound("frameData"));
        this.markDirty();
    }

    @Override
    protected ScreenHandler createScreenHandler(final int syncId, final PlayerInventory playerInventory) {
        return new FrameGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public void writeScreenOpeningData(final ServerPlayerEntity serverPlayerEntity, final PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }
}

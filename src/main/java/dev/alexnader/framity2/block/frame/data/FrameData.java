package dev.alexnader.framity2.block.frame.data;

import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;

import java.util.Optional;

public class FrameData {
    private static Optional<ItemStack>[] itemsFromTag(final Sections sections, final ListTag tag) {
        final Optional<ItemStack>[] items = sections.makeItems();

        for (int i = 0, size = tag.size(); i < size; i++) {
            final CompoundTag stackTag = tag.getCompound(i);
            final int slot = stackTag.getByte("Slot") & 255;
            if (sections.containsSlot(slot)) {
                items[slot] = Optional.of(ItemStack.fromTag(stackTag));
            }
        }

        return items;
    }

    private static Optional<BlockState>[] baseStatesFromTag(final Sections sections, final ListTag tag) {
        final Optional<BlockState>[] baseStates = sections.makeBaseStates();

        for (int i = 0, size = tag.size(); i < size; i++) {
            final CompoundTag stateTag = tag.getCompound(i);
            final int realIndex = stateTag.getInt("i");
            //noinspection OptionalGetWithoutIsPresent
            baseStates[realIndex] = Optional.of(
                BlockState.CODEC.decode(new Dynamic<>(NbtOps.INSTANCE, stateTag)).result().get().getFirst()
            );
        }

        return baseStates;
    }

    public static FrameData fromTag(final CompoundTag tag) {
        final Sections sections = Sections.fromTag(tag.getList("format", 3));

        return new FrameData(
            sections,
            itemsFromTag(sections, tag.getList("Items", 10)),
            baseStatesFromTag(sections, tag.getList("states", 10))
        );
    }

    private final Sections sections;
    private final Optional<ItemStack>[] items;
    private final Optional<BlockState>[] baseStates;

    public FrameData(final Sections sections, final Optional<ItemStack>[] items, final Optional<BlockState>[] baseStates) {
        this.sections = sections;
        this.items = items;
        this.baseStates = baseStates;
    }

    public FrameData(final Sections sections) {
        this(sections, sections.makeItems(), sections.makeBaseStates());
    }

    public Sections sections() {
        return sections;
    }

    public Optional<ItemStack>[] items() {
        return items;
    }

    public Optional<BlockState>[] baseStates() {
        return baseStates;
    }

    public CompoundTag toTag() {
        final CompoundTag tag = new CompoundTag();

        tag.put("format", sections.toTag());

        final ListTag itemsTag = new ListTag();
        for (int i = 0, size = items.length; i < size; i++) {
            final int i2 = i;
            items[i].ifPresent(stack -> {
                final CompoundTag stackTag = new CompoundTag();
                stack.toTag(stackTag);
                stackTag.putByte("Slot", (byte)i2);
                itemsTag.add(stackTag);
            });
        }
        if (!itemsTag.isEmpty()) {
            tag.put("Items", itemsTag);
        }

        final ListTag baseStatesTag = new ListTag();
        for (int i = 0, size = baseStates.length; i < size; i++) {
            final int i2 = i;
            baseStates[i].ifPresent(baseState -> {
                final CompoundTag baseStateTag = new CompoundTag();
                baseStateTag.putInt("i", i2);
                //noinspection OptionalGetWithoutIsPresent
                baseStatesTag.add(
                    BlockState.CODEC.encode(baseState, NbtOps.INSTANCE, baseStateTag).get().left().get()
                );
            });
        }
        if (!baseStatesTag.isEmpty()) {
            tag.put("states", baseStatesTag);
        }

        return tag;
    }
}

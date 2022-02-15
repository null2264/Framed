package io.github.null2264.framed.block.frame.data;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.null2264.framed.Framed.OVERLAYS;

public class FrameData
{
    private final Sections sections;
    private final Optional<ItemStack>[] items;
    private final Optional<BlockState>[] baseStates;

    public FrameData(final Sections sections, final Optional<ItemStack>[] items, final Optional<BlockState>[] baseStates) {
        this.sections = sections;
        this.items = items;
        this.baseStates = baseStates;
    }
    public FrameData(@Nonnull final Sections sections) {
        this(sections, sections.makeItems(), sections.makeBaseStates());
    }

    private static Optional<ItemStack>[] itemsFromTag(final Sections sections, final NbtList tag) {
        final Optional<ItemStack>[] items = sections.makeItems();

        for (int i = 0, size = tag.size(); i < size; i++) {
            final NbtCompound stackTag = tag.getCompound(i);
            final int slot = stackTag.getByte("Slot") & 255;
            if (sections.containsSlot(slot)) {
                items[slot] = Optional.of(ItemStack.fromNbt(stackTag));
            }
        }

        return items;
    }

    private static Optional<BlockState>[] baseStatesFromTag(final Sections sections, final NbtList tag) {
        final Optional<BlockState>[] baseStates = sections.makeBaseStates();

        for (int i = 0, size = tag.size(); i < size; i++) {
            final NbtCompound stateTag = tag.getCompound(i);
            final int realIndex = stateTag.getInt("i");
            baseStates[realIndex] = BlockState.CODEC.decode(new Dynamic<>(NbtOps.INSTANCE, stateTag)).result().map(Pair::getFirst);
        }

        return baseStates;
    }

    public static FrameData readNbt(final NbtCompound tag) {
        final Sections sections = Sections.readNbt(tag.getList("format", 3));

        return new FrameData(
            sections,
            itemsFromTag(sections, tag.getList("Items", 10)),
            baseStatesFromTag(sections, tag.getList("states", 10))
        );
    }

    public Sections getSections() {
        return sections;
    }

    public Optional<ItemStack>[] getItems() {
        return items;
    }

    public List<Optional<ItemStack>> baseItems() {
        return Arrays.asList(items).subList(sections.base().start(), sections.base().end());
    }

    public List<Optional<ItemStack>> overlayItems() {
        return Arrays.asList(items).subList(sections.overlay().start(), sections.overlay().end());
    }

    public List<Optional<ItemStack>> specialItems() {
        return Arrays.asList(items).subList(sections.special().start(), sections.special().end());
    }

    public Optional<BlockState>[] baseStates() {
        return baseStates;
    }

    public NbtCompound writeNbt() {
        final NbtCompound tag = new NbtCompound();

        tag.put("format", sections.toNbt());

        final NbtList itemsTag = new NbtList();
        for (int i = 0, size = items.length; i < size; i++) {
            final int i2 = i;
            items[i].ifPresent(stack -> {
                final NbtCompound stackTag = new NbtCompound();
                stack.setNbt(stackTag);
                stackTag.putByte("Slot", (byte) i2);
                itemsTag.add(stackTag);
            });
        }
        if (!itemsTag.isEmpty()) {
            tag.put("Items", itemsTag);
        }

        final NbtList baseStatesTag = new NbtList();
        for (int i = 0, size = baseStates.length; i < size; i++) {
            final int i2 = i;
            baseStates[i].ifPresent(baseState -> {
                final NbtCompound baseStateTag = new NbtCompound();
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

    public List<Pair<Optional<BlockState>, Optional<Identifier>>> toRenderAttachment() {
        //noinspection UnstableApiUsage
        return Streams.zip(
            Arrays.stream(baseStates),
            overlayItems().stream().map(i -> i.flatMap(OVERLAYS::getOverlayId)),
            Pair::new
        ).collect(Collectors.toList());
    }
}

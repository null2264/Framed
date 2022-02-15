package io.github.null2264.framed;

import io.github.null2264.framed.client.FramedConfig;
import io.github.null2264.framed.data.OverlayDataListener;
import io.github.null2264.framed.items.SpecialItems;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class Framed implements ModInitializer
{
    public static FramedProperties PROPERTIES;
    public static SpecialItems SPECIAL_ITEMS;

    public static FramedMeta META;
    public static FramedBlocks BLOCKS;
    public static FramedItems ITEMS;
    public static FramedBlockEntityTypes BLOCK_ENTITY_TYPES;

    public static OverlayDataListener OVERLAYS;

    @Override
    public void onInitialize() {
        AutoConfig.register(FramedConfig.class, JanksonConfigSerializer::new);

        PROPERTIES = new FramedProperties();
        SPECIAL_ITEMS = new SpecialItems();

        META = new FramedMeta();
        BLOCKS = new FramedBlocks();
        ITEMS = new FramedItems();
        BLOCK_ENTITY_TYPES = new FramedBlockEntityTypes();

        OVERLAYS = new OverlayDataListener();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(OVERLAYS);
    }
}

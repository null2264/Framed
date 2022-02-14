package dev.alexnader.framed.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static dev.alexnader.framed.Framed.META;

public class OverlayDataListener implements SimpleResourceReloadListener<Collection<Identifier>> {
    private final Map<Ingredient, Identifier> triggers = new HashMap<>();

    public Optional<Identifier> getOverlayId(final ItemStack stack) {
        return triggers.entrySet().stream().filter(e -> e.getKey().test(stack)).map(Map.Entry::getValue).findFirst();
    }

    public boolean hasOverlay(final ItemStack stack) {
        return getOverlayId(stack).isPresent();
    }

    @Override
    public CompletableFuture<Collection<Identifier>> load(final ResourceManager resourceManager, final Profiler profiler, final Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            triggers.clear();

            return resourceManager.findResources("framed/overlays", s -> s.endsWith(".json"));
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(final Collection<Identifier> identifiers, final ResourceManager resourceManager, final Profiler profiler, final Executor executor) {
        return CompletableFuture.runAsync(() -> {
            for (final Identifier id : identifiers) {
                try {
                    final JsonElement element = new Gson().fromJson(new BufferedReader(new InputStreamReader(resourceManager.getResource(id).getInputStream())), JsonElement.class);

                    if (!element.isJsonObject()) {
                        throw new JsonParseException("Invalid JSON: expected an object.");
                    }

                    final JsonObject obj = element.getAsJsonObject();

                    if (!obj.has("trigger")) {
                        throw new JsonParseException("Invalid JSON: expected the key `trigger`.");
                    }

                    triggers.put(Ingredient.fromJson(obj.get("trigger")), id);
                } catch (final Exception e) {
                    META.LOGGER.warn("Exception while parsing overlay: " + e);
                }
            }
        }, executor);
    }

    private final Identifier id = META.id("data/overlay");

    @Override
    public Identifier getFabricId() {
        return id;
    }
}

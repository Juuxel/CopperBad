package juuxel.copperbad;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;

public final class CopperBad implements ModInitializer {
    private static final ImmutableSet<String> WHITELIST = ImmutableSet.of("minecraft", "astromine");

    @Override
    public void onInitialize() {
        visit(Registry.BLOCK, checkFor("copper_ore"));
        visit(Registry.ITEM, checkFor("copper_ingot"));
    }

    private static <T> BiConsumer<T, Identifier> checkFor(String path) {
        return (entry, id) -> {
            if (id.getPath().equals(path) && !WHITELIST.contains(id.getNamespace())) {
                throw new CopperFoundException(entry, id);
            }
        };
    }

    private static <T> void visit(Registry<T> registry, BiConsumer<? super T, ? super Identifier> visitor) {
        for (T entry : registry) {
            visitor.accept(entry, registry.getId(entry));
        }

        RegistryEntryAddedCallback.event(registry).register((rawId, id, entry) -> visitor.accept(entry, id));
    }

    private static final class CopperFoundException extends RuntimeException {
        CopperFoundException(Object entry, Identifier id) {
            super("Copper entry " + entry + " was registered with banned id " + id);
        }
    }
}

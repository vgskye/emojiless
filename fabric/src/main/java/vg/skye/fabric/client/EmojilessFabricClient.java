package vg.skye.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import vg.skye.EmojilessClient;

public final class EmojilessFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        EmojilessClient.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                EmojilessClient.reloadCallback(manager);
            }

            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("emojiless", "emoji");
            }
        });
    }
}

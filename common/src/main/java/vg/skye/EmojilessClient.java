package vg.skye;

import com.mojang.blaze3d.platform.NativeImage;
import com.samsthenerd.inline.api.client.InlineClientAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

public final class EmojilessClient {
    public record EmojiTexture(ResourceLocation loc, int w, int h) {}

    public static Map<String, EmojiTexture> emojis = new HashMap<>();

    public static void init() {
        InlineClientAPI.INSTANCE.addMatcher(EmojiMatcher.INSTANCE);
    }

    public static void reloadCallback(ResourceManager manager) {
        emojis.clear();
        var definitions = manager.listResources("emojis", path -> path.getPath().endsWith(".png"));
        for (var entry : definitions.entrySet()) {
            try {
                var segments = entry.getKey().getPath().split("/");
                var filename = segments[segments.length - 1].split(".png")[0];
                var stream = entry.getValue().open();
                var img = NativeImage.read(stream);
                var tex = new DynamicTexture(img);
                var loc = new ResourceLocation("emojiless", "emoji_textures/" + entry.getKey().getNamespace() + "/" + entry.getKey().getPath());
                emojis.put(filename, new EmojiTexture(loc, img.getWidth(), img.getHeight()));
                Minecraft.getInstance().execute(() -> {
                    Minecraft.getInstance().getTextureManager().register(loc, tex);
                });
            } catch (Exception e) {
                Emojiless.LOGGER.warn("Failed to load emoji", e);
            }
        }
    }
}

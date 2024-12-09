package vg.skye;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.samsthenerd.inline.api.client.InlineClientAPI;
import com.samsthenerd.inline.utils.Spritelike;
import com.samsthenerd.inline.utils.TextureSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import vg.skye.mixin.NativeImageAccessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public final class EmojilessClient {
    public static Map<String, Spritelike> emojis = new HashMap<>();

    public static void init() {
        InlineClientAPI.INSTANCE.addMatcher(EmojiMatcher.INSTANCE);
        InlineClientAPI.INSTANCE.addMatcher(EmojiMatcher.STANDARD);
    }

    public static AnimatedTextureSprite readGif(ResourceLocation loc, ByteBuffer buf) throws IOException {
        NativeImage image;
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            PointerBuffer delayBuf = memoryStack.mallocPointer(1);
            IntBuffer wBuf = memoryStack.mallocInt(1);
            IntBuffer hBuf = memoryStack.mallocInt(1);
            IntBuffer framesBuf = memoryStack.mallocInt(1);
            IntBuffer channelsBuf = memoryStack.mallocInt(1);
            ByteBuffer imageBuf = STBImage.stbi_load_gif_from_memory(
                    buf,
                    delayBuf,
                    wBuf,
                    hBuf,
                    framesBuf,
                    channelsBuf,
                    4
            );
            if (imageBuf == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            image = new NativeImage(
                    wBuf.get(0),
                    hBuf.get(0) * framesBuf.get(0),
                    true
            );
            MemoryUtil.memCopy(
                    MemoryUtil.memAddress(imageBuf),
                    ((NativeImageAccessor) (Object) image).getPixels(),
                    (long) wBuf.get(0) * hBuf.get(0) * framesBuf.get(0) * 4
            );

            var tex = new DynamicTexture(image);
            Minecraft.getInstance().execute(() -> {
                Minecraft.getInstance().getTextureManager().register(loc, tex);
            });
            var delays = new int[framesBuf.get(0)];
            delayBuf.getIntBuffer(framesBuf.get(0)).get(delays);
            return new AnimatedTextureSprite(
                    loc,
                    image.getWidth(),
                    hBuf.get(0) * framesBuf.get(0),
                    hBuf.get(0),
                    delays
            );
        }
    }

    public static void reloadCallback(ResourceManager manager) {
        emojis.clear();
        for (var entry : manager.listResources("emojis", path -> path.getPath().endsWith(".png")).entrySet()) {
            try {
                var segments = entry.getKey().getPath().split("/");
                var filename = segments[segments.length - 1].split(".png")[0];
                var stream = entry.getValue().open();
                var img = NativeImage.read(stream);
                var tex = new DynamicTexture(img);
                var loc = new ResourceLocation("emojiless", "emoji_textures/" + entry.getKey().getNamespace() + "/" + entry.getKey().getPath());
                emojis.put(filename, new TextureSprite(loc, 0, 0, 1, 1, img.getWidth(), img.getHeight()));
                Minecraft.getInstance().execute(() -> {
                    Minecraft.getInstance().getTextureManager().register(loc, tex);
                });
            } catch (Exception e) {
                Emojiless.LOGGER.warn("Failed to load emoji", e);
            }
        }
        for (var entry : manager.listResources("emojis", path -> path.getPath().endsWith(".gif")).entrySet()) {
            try {
                var segments = entry.getKey().getPath().split("/");
                var filename = segments[segments.length - 1].split(".gif")[0];
                var stream = entry.getValue().open();
                var buf = TextureUtil.readResource(stream);
                buf.rewind();
                var loc = new ResourceLocation("emojiless", "emoji_textures/" + entry.getKey().getNamespace() + "/" + entry.getKey().getPath());
                var img = readGif(loc, buf);
                emojis.put(filename, img);
            } catch (Exception e) {
                Emojiless.LOGGER.warn("Failed to load animated emoji", e);
            }
        }
    }
}

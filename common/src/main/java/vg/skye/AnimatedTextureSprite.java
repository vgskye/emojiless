package vg.skye;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.samsthenerd.inline.utils.Spritelike;
import com.samsthenerd.inline.utils.TextureSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class AnimatedTextureSprite extends Spritelike {
    private final ResourceLocation id;
    private final int frameHeight;
    private final int textWidth;
    private final int textHeight;
    private final int[] delays;
    private final int totalLength;

    public AnimatedTextureSprite(ResourceLocation id, int textWidth, int textHeight, int frameHeight, List<Integer> delays){
        this(id, textWidth, textHeight, frameHeight, delays.stream().mapToInt(Integer::intValue).toArray());
    }

    public AnimatedTextureSprite(ResourceLocation id, int textWidth, int textHeight, int frameHeight, int[] delays){
        this.id = id;
        this.textWidth = textWidth;
        this.textHeight = textHeight;
        this.frameHeight = frameHeight;
        this.delays = delays;
        int sum = 0;
        for (int delay : delays) {
            sum += delay;
        }
        totalLength = sum;
    }

    @Override
    public SpritelikeType getType(){
        return AnimatedTextureSpriteType.INSTANCE;
    }

    public ResourceLocation getTextureId(){
        return id;
    }

    public float getMinU(){
        return 0;
    }
    public float getMinV(){
        long currentFrame = (System.nanoTime() / 1000000L) % totalLength;
        int delayAcc = 0;
        for (int i = 0; i < delays.length; i++) {
            delayAcc += delays[i];
            if (delayAcc >= currentFrame) {
                int topOff = frameHeight * i;
                return (float) topOff / textHeight;
            }
        }
        return 0;
    }
    public float getMaxU(){
        return 1;
    }
    public float getMaxV(){
        long currentFrame = (System.nanoTime() / 1000000L) % totalLength;
        int delayAcc = 0;
        for (int i = 0; i < delays.length; i++) {
            delayAcc += delays[i];
            if (delayAcc >= currentFrame) {
                int botOff = frameHeight * (i + 1);
                return (float) botOff / textHeight;
            }
        }
        return 1;
    }

    public int getTextureWidth(){
        return textWidth;
    }
    public int getTextureHeight(){
        return textHeight;
    }
    public int getFrameHeight() {
        return frameHeight;
    }
    public List<Integer> getDelays() {
        return Arrays.stream(delays).boxed().toList();
    }

    public static class AnimatedTextureSpriteType implements SpritelikeType{
        public static final TextureSprite.TextureSpriteType INSTANCE = new TextureSprite.TextureSpriteType();
        private static final Codec<AnimatedTextureSprite> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(AnimatedTextureSprite::getTextureId),
                Codec.INT.optionalFieldOf("textWidth", 16).forGetter(AnimatedTextureSprite::getTextureWidth),
                Codec.INT.optionalFieldOf("textHeight", 16).forGetter(AnimatedTextureSprite::getTextureHeight),
                Codec.INT.optionalFieldOf("frameHeight", 16).forGetter(AnimatedTextureSprite::getFrameHeight),
                Codec.list(Codec.INT).optionalFieldOf("delays", List.of()).forGetter(AnimatedTextureSprite::getDelays)
        ).apply(instance, AnimatedTextureSprite::new));

        public Codec<AnimatedTextureSprite> getCodec(){
            return CODEC;
        }

        public String getId(){
            return "animated_texture";
        }
    }
}

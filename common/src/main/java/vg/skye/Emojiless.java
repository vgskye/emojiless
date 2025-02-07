package vg.skye;

import com.samsthenerd.inline.utils.Spritelike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Emojiless {
    public static final String MOD_ID = "emojiless";
    public static final Logger LOGGER = LoggerFactory.getLogger("emojiless");

    public static void init() {
        Spritelike.registerType(AnimatedTextureSprite.AnimatedTextureSpriteType.INSTANCE);
    }
}

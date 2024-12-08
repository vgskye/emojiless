package vg.skye;

import com.samsthenerd.inline.api.data.SpriteInlineData;
import com.samsthenerd.inline.api.matching.ContinuousMatcher;
import com.samsthenerd.inline.api.matching.MatchContext;
import com.samsthenerd.inline.api.matching.MatcherInfo;
import com.samsthenerd.inline.utils.TextureSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiMatcher implements ContinuousMatcher {
    public static final EmojiMatcher INSTANCE = new EmojiMatcher();

    private static final Pattern REGEX = Pattern.compile(":([a-z0-9_.-]+):");

    @Override
    public ContinuousMatchResult match(String input, MatchContext matchContext) {
        Matcher regexMatcher = REGEX.matcher(input);
        ContinuousMatchResult result = new ContinuousMatchResult();
        while(regexMatcher.find()){
            MatchResult mr = regexMatcher.toMatchResult();
            String emoji = mr.group(1);
            var tex = EmojilessClient.emojis.get(emoji);
            if (tex != null) {
                var sprite = new TextureSprite(tex.loc(), 0, 0, 1, 1, tex.w(), tex.h());
                result.addMatch(mr.start(), mr.end(), new SpriteInlineData(sprite));
            }
        }
        return result;
    }

    @Override
    public MatcherInfo getInfo() {
        return MatcherInfo.fromId(new ResourceLocation("emojiless", "emoji"));
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation("emojiless", "emoji");
    }
}

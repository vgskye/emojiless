package vg.skye;

import com.samsthenerd.inline.api.data.SpriteInlineData;
import com.samsthenerd.inline.api.matching.*;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiMatcher implements ContinuousMatcher {
    public static final EmojiMatcher INSTANCE = new EmojiMatcher();
    public static final ContinuousMatcher STANDARD = new RegexMatcher.Standard(
            "emoji",
            "[a-z0-9_.-]+",
            new ResourceLocation("emojiless", "emoji_standard"),
            name -> {
                var emoji = EmojilessClient.emojis.get(name);
                if (emoji == null)
                    return null;
                return new InlineMatch.DataMatch(new SpriteInlineData(emoji), Style.EMPTY);
            },
            MatcherInfo.fromId(new ResourceLocation("emojiless", "emoji_standard"))
    );

    private static final Pattern REGEX = Pattern.compile(":([a-z0-9_.-]+):");
    public static final Pattern PARTIAL = Pattern.compile(":[a-z0-9_-]*$");
    public static final Pattern PARTIAL_NEG = Pattern.compile(":[a-z0-9_.-]+:[a-z0-9_-]*$");

    @Override
    public ContinuousMatchResult match(String input, MatchContext matchContext) {
        Matcher regexMatcher = REGEX.matcher(input);
        ContinuousMatchResult result = new ContinuousMatchResult();
        while(regexMatcher.find()){
            MatchResult mr = regexMatcher.toMatchResult();
            String emoji = mr.group(1);
            var tex = EmojilessClient.emojis.get(emoji);
            if (tex != null) {
                result.addMatch(mr.start(), mr.end(), new SpriteInlineData(tex));
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

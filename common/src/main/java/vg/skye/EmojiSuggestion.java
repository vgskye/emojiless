package vg.skye;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import java.util.ArrayList;
import java.util.List;

public class EmojiSuggestion extends Suggestion {
    private final String value;
    private EmojiSuggestion(StringRange range, String text) {
        super(range, ":" + text + ": " + text);
        this.value = text;
    }

    @Override
    public String apply(String input) {
        String valueToReplace = ":" + value + ":";
        StringRange range = getRange();
        if (range.getStart() == 0 && range.getEnd() == input.length()) {
            return valueToReplace;
        }
        final StringBuilder result = new StringBuilder();
        if (range.getStart() > 0) {
            result.append(input, 0, range.getStart());
        }
        result.append(valueToReplace);
        if (range.getEnd() < input.length()) {
            result.append(input.substring(range.getEnd()));
        }
        return result.toString();
    }

    public static Suggestions suggest(String text, int start) {
        List<Suggestion> result = new ArrayList<>();
        String remaining = text.substring(start + 1);
        for (String key: EmojilessClient.emojis.keySet()) {
            if (key.startsWith(remaining)) {
                result.add(new EmojiSuggestion(StringRange.between(start, text.length()), key));
            }
        }
        return Suggestions.create(text, result);
    }
}

package vg.skye.mixin;

import com.mojang.brigadier.suggestion.Suggestions;
import com.samsthenerd.inline.api.client.InlineClientAPI;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vg.skye.EmojiMatcher;
import vg.skye.EmojiSuggestion;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {
    @Shadow @Final
    EditBox input;

    @Shadow private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow public abstract void showSuggestions(boolean bl);

    @Inject(method = "updateCommandInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/SharedSuggestionProvider;suggest(Ljava/lang/Iterable;Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;)Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.AFTER))
    private void inject(CallbackInfo ci) {
        if (!InlineClientAPI.INSTANCE.getConfig().isMatcherEnabled(new ResourceLocation("emojiless", "emoji")))
            return;
        String text = this.input.getValue();
        int cursor = this.input.getCursorPosition();
        String textUptoCursor = text.substring(0, cursor);
        var negMatcher = EmojiMatcher.PARTIAL_NEG.matcher(textUptoCursor);
        var matcher = EmojiMatcher.PARTIAL.matcher(textUptoCursor);
        if (matcher.find() && !negMatcher.find()) {
            this.pendingSuggestions = CompletableFuture.completedFuture(EmojiSuggestion.suggest(textUptoCursor, matcher.start()));
            this.showSuggestions(false);
        }
    }
}

package org.hiedacamellia.languagereload.core.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TranslatableFormatException;
import org.hiedacamellia.languagereload.core.access.ILanguage;
import org.hiedacamellia.languagereload.core.access.ITranslationStorage;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TranslatableComponent.class})
abstract class TranslatableTextContentMixin {
    @Unique
    @Nullable
    private String previousTargetLanguage;
    @Unique
    private final Map<String, List<FormattedText>> separatedecomposedPartsCache = Maps.newHashMap();
    @Unique
    @Nullable
    private List<FormattedText> saveddecomposedParts;
    @Shadow
    @Final
    private String key;
    @Shadow
    @Nullable
    private Language decomposedWith;
    @Shadow
    private List<FormattedText> decomposedParts;

    TranslatableTextContentMixin() {
    }

    @Inject(method={"decompose"}, at={@At(value="RETURN")})
    void onUpdatedecomposedParts(CallbackInfo ci) {
        if (!ClientConfig.multilingualItemSearch) {
            return;
        }
        if (this.decomposedWith == null) {
            return;
        }
        ClientLanguage decomposedPartstorage = ((ILanguage)this.decomposedWith).languagereload_getTranslationStorage();
        if (decomposedPartstorage == null) {
            return;
        }
        String targetLanguage = ((ITranslationStorage)decomposedPartstorage).languagereload_getTargetLanguage();
        if (Objects.equals(this.previousTargetLanguage, targetLanguage)) {
            return;
        }
        if (targetLanguage == null) {
            this.previousTargetLanguage = null;
            this.decomposedParts = this.saveddecomposedParts;
            this.saveddecomposedParts = null;
            return;
        }
        if (this.previousTargetLanguage == null) {
            this.saveddecomposedParts = this.decomposedParts;
        }
        this.previousTargetLanguage = targetLanguage;
        this.decomposedParts = this.separatedecomposedPartsCache.computeIfAbsent(targetLanguage, k -> {
            String string = this.decomposedWith.getOrDefault(this.key);
            try {
                ImmutableList.Builder<FormattedText> builder = ImmutableList.builder();
                this.decomposeTemplate(string, builder::add);
                return builder.build();
            }
            catch (TranslatableFormatException e) {
                return ImmutableList.of(FormattedText.of(string));
            }
        });
    }

    @Inject(method={"decompose"}, at={@At(value="INVOKE", target="Lnet/minecraft/locale/Language;getOrDefault(Ljava/lang/String;)Ljava/lang/String;")})
    void onUpdatedecomposedParts$clearCache(CallbackInfo ci) {
        this.previousTargetLanguage = null;
        this.separatedecomposedPartsCache.clear();
        this.saveddecomposedParts = null;
    }

    @Shadow
    protected abstract void decomposeTemplate(String template, Consumer<FormattedText> consumer);
}

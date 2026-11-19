package org.hiedacamellia.languagereload.core.mixin;

import com.google.common.collect.Maps;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.hiedacamellia.languagereload.core.access.ITranslationStorage;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientLanguage.class})
abstract class TranslationStorageMixin
extends Language
implements ITranslationStorage {
    @Unique
    @Nullable
    private String targetLanguage;
    @Unique
    private static Map<String, Map<String, String>> separateTranslationsOnLoad;
    @Unique
    private Map<String, Map<String, String>> separateTranslations;

    TranslationStorageMixin() {
    }

    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    void onConstructed(Map<String, String> translations, boolean rightToLeft, CallbackInfo ci) {
        this.separateTranslations = separateTranslationsOnLoad;
        separateTranslationsOnLoad = null;
    }

    @Inject(method={"loadFrom"}, at={@At(value="HEAD")})
    private static void onLoad(ResourceManager resourceManager, List<LanguageInfo> definitions, CallbackInfoReturnable<ClientLanguage> cir) {
        separateTranslationsOnLoad = Maps.newHashMap();
    }

    @Redirect(method={"appendFrom"}, at=@At(value="INVOKE", target="Lnet/minecraft/locale/Language;loadFromJson(Ljava/io/InputStream;Ljava/util/function/BiConsumer;)V"))
    private static void onInternalLoad$saveSeparately(InputStream inputStream, BiConsumer<String, String> entryConsumer, List<Resource> resources, Map<String, String> dest) {
        if (ClientConfig.multilingualItemSearch) {
            Language.loadFromJson(inputStream, (key, value) -> {
                entryConsumer.accept(key, value);
                if (separateTranslationsOnLoad != null) {
                    separateTranslationsOnLoad.computeIfAbsent("default", k -> Maps.newHashMap()).put(key, value);
                }
            });
        } else {
            Language.loadFromJson(inputStream, entryConsumer);
        }
    }

    @Inject(method={"getOrDefault"}, at={@At(value="HEAD")}, cancellable=true)
    void onGet(String key, CallbackInfoReturnable<String> cir) {
        if (this.targetLanguage != null && this.separateTranslations != null) {
            Map<String, String> targetTranslations = this.separateTranslations.get(this.targetLanguage);
            cir.setReturnValue(targetTranslations == null ? "" : targetTranslations.getOrDefault(key, ""));
        }
    }

    @Override
    @Nullable
    public String languagereload_getTargetLanguage() {
        return this.targetLanguage;
    }

    @Override
    public void languagereload_setTargetLanguage(@Nullable String value) {
        this.targetLanguage = value;
    }
}

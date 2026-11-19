package org.hiedacamellia.languagereload.core.mixin;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.hiedacamellia.languagereload.LanguageReload;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LanguageManager.class})
abstract class LanguageManagerMixin {
    @Shadow
    private Map<String, LanguageInfo> languages;
    @Shadow
    private LanguageInfo currentLanguage;

    LanguageManagerMixin() {
    }

    @Shadow
    public abstract LanguageInfo getLanguage(String var1);

    @Redirect(method={"onResourceManagerReload"}, at=@At(value="INVOKE", target="Ljava/util/List;add(Ljava/lang/Object;)Z"))
    boolean onReload$addFallbacks(List<LanguageInfo> list, Object currentLang) {
        if (!ClientConfig.language.equals("*") && this.currentLanguage != null) {
            list.add(this.currentLanguage);
        }
        for (String code : Lists.reverse(ClientConfig.fallbacks)) {
            LanguageInfo info = this.getLanguage(code);
            if (info != null && !list.contains(info)) {
                list.add(info);
            }
        }
        return true;
    }

    @Inject(method={"onResourceManagerReload"}, at=@At(value="HEAD"))
    void onReload$setSystemLanguage(ResourceManager manager, CallbackInfo ci) {
        if (LanguageReload.shouldSetSystemLanguage && this.languages != null) {
            LanguageReload.shouldSetSystemLanguage = false;
            LanguageReload.LOGGER.info("Language is not set. Setting it to system language");
            Locale locale = Locale.getDefault();
            List<String> matchingLanguages = this.languages.keySet().stream().filter(code -> code.split("_")[0].equalsIgnoreCase(locale.getLanguage())).toList();
            int count = matchingLanguages.size();
            if (count > 1) {
                matchingLanguages.stream().filter(code -> {
                    String[] split = code.split("_");
                    if (split.length < 2) {
                        return false;
                    }
                    return split[1].equalsIgnoreCase(locale.getCountry());
                }).findFirst().ifPresent(lang -> LanguageManagerMixin.setSystemLanguage(lang, locale));
            } else if (count == 1) {
                LanguageManagerMixin.setSystemLanguage(matchingLanguages.get(0), locale);
            }
        }
    }

    @Unique
    private static void setSystemLanguage(final String lang, Locale locale) {
        LanguageReload.LOGGER.info("Set language to {} (mapped from {})", lang, locale.toLanguageTag());
        LanguageReload.setLanguage(lang, new LinkedList<String>(){
            {
                if (!lang.equals("en_us")) {
                    this.add("en_us");
                }
            }
        });
    }
}

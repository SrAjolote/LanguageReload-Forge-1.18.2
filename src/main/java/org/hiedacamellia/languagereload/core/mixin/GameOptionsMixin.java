package org.hiedacamellia.languagereload.core.mixin;

import java.io.File;
import java.util.LinkedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.nbt.CompoundTag;
import org.hiedacamellia.languagereload.LanguageReload;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Options.class})
abstract class GameOptionsMixin {
    @Shadow
    @Final
    private File optionsFile;
    @Shadow
    public String languageCode;

    GameOptionsMixin() {
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    void onConstructed(Minecraft client, File optionsFile, CallbackInfo ci) {
        if (!LanguageReload.shouldSetSystemLanguage) {
            GameOptionsMixin.checkConfigLanguage(this.languageCode);
        }
    }

    @Inject(method={"load"}, at={@At(value="HEAD")})
    void onLoad(CallbackInfo ci) {
        if (!this.optionsFile.exists()) {
            LanguageReload.shouldSetSystemLanguage = true;
        }
    }

    @Inject(method={"dataFix"}, at={@At(value="RETURN")})
    void onUpdate(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        String lang = cir.getReturnValue().getString("lang");
        if (lang.isEmpty()) {
            LanguageReload.shouldSetSystemLanguage = true;
        } else {
            GameOptionsMixin.checkConfigLanguage(lang);
        }
    }

    @Unique
    private static void checkConfigLanguage(String language) {
        if (!ClientConfig.language.equals(language)) {
            LanguageReload.LOGGER.info("Game language ({}) and config language ({}) are different. Updating config", language, ClientConfig.language);
            if (!ClientConfig.SPEC.isLoaded()) {
                return;
            }
            ClientConfig.PREVIOUS_LANGUAGE.set(ClientConfig.language);
            ClientConfig.PREVIOUS_FALLBACKS.set(ClientConfig.fallbacks);
            ClientConfig.LANGUAGE.set(language);
            ClientConfig.FALLBACKS.set(new LinkedList<String>());
            if (!language.equals("en_us")) {
                LinkedList<String> a = new LinkedList<String>(ClientConfig.fallbacks);
                a.add("en_us");
                ClientConfig.FALLBACKS.set(a);
            }
            ClientConfig.SPEC.save();
            ClientConfig.load();
        }
    }
}

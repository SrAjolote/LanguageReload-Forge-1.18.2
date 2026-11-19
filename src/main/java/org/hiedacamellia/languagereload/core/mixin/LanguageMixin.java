/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.language.ClientLanguage
 *  net.minecraft.locale.Language
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.hiedacamellia.languagereload.core.mixin;

import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import org.hiedacamellia.languagereload.core.access.ILanguage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Language.class}, priority=990)
public class LanguageMixin
implements ILanguage {
    @Unique
    @Nullable
    private ClientLanguage translationStorage = null;
    @Unique
    @Nullable
    private static ClientLanguage translationStorageOnSetInstance = null;

    @Inject(method={"inject"}, at={@At(value="HEAD")})
    private static void onSetInstance(Language language, CallbackInfo ci) {
        if (language instanceof ClientLanguage) {
            ClientLanguage translationStorage;
            translationStorageOnSetInstance = translationStorage = (ClientLanguage)language;
        }
    }

    @Inject(method={"inject"}, at={@At(value="TAIL")})
    private static void afterSetInstance(Language language, CallbackInfo ci) {
        ((ILanguage)language).languagereload_setTranslationStorage(translationStorageOnSetInstance);
        translationStorageOnSetInstance = null;
    }

    @Override
    public void languagereload_setTranslationStorage(ClientLanguage translationStorage) {
        this.translationStorage = translationStorage;
    }

    @Override
    public ClientLanguage languagereload_getTranslationStorage() {
        return this.translationStorage;
    }
}


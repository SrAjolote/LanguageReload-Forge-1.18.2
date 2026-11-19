/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.language.ClientLanguage
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package org.hiedacamellia.languagereload.core.access;

import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public interface ILanguage {
    public void languagereload_setTranslationStorage(ClientLanguage var1);

    public ClientLanguage languagereload_getTranslationStorage();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package org.hiedacamellia.languagereload.core.access;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;

@OnlyIn(value=Dist.CLIENT)
public interface ILanguageOptionsScreen {
    public void languagereload_focusList(LanguageListWidget var1);

    public void languagereload_focusEntry(LanguageEntry var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.AdvancementProgress
 *  net.minecraft.advancements.DisplayInfo
 *  net.minecraft.client.gui.screens.advancements.AdvancementTab
 *  net.minecraft.client.gui.screens.advancements.AdvancementWidget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package org.hiedacamellia.languagereload.core.mixin;

import java.util.List;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={AdvancementWidget.class})
public interface AdvancementWidgetAccessor {
    @Accessor(value="tab")
    public AdvancementTab languagereload_getTab();

    @Accessor(value="display")
    public DisplayInfo languagereload_getDisplay();

    @Accessor(value="progress")
    public AdvancementProgress languagereload_getProgress();

    @Accessor(value="parent")
    public AdvancementWidget languagereload_getParent();

    @Accessor(value="parent")
    public void languagereload_setParent(AdvancementWidget var1);

    @Accessor(value="children")
    public List<AdvancementWidget> languagereload_getChildren();

    @Mutable
    @Accessor(value="children")
    public void languagereload_setChildren(List<AdvancementWidget> var1);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.advancements.AdvancementTab
 *  net.minecraft.client.gui.screens.advancements.AdvancementWidget
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package org.hiedacamellia.languagereload.core.mixin;

import java.util.Map;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.hiedacamellia.languagereload.core.access.IAdvancementsTab;
import org.hiedacamellia.languagereload.core.mixin.AdvancementWidgetAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={AdvancementTab.class})
public abstract class AdvancementTabMixin
implements IAdvancementsTab {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private Map<Advancement, AdvancementWidget> widgets;

    @Override
    public void languagereload_recreateWidgets() {
        this.widgets.replaceAll((advancement, widget) -> {
            AdvancementWidget newWidget = new AdvancementWidget(((AdvancementWidgetAccessor)widget).languagereload_getTab(), this.minecraft, advancement, ((AdvancementWidgetAccessor)widget).languagereload_getDisplay());
            newWidget.setProgress(((AdvancementWidgetAccessor)widget).languagereload_getProgress());
            ((AdvancementWidgetAccessor)newWidget).languagereload_setParent(((AdvancementWidgetAccessor)widget).languagereload_getParent());
            ((AdvancementWidgetAccessor)newWidget).languagereload_setChildren(((AdvancementWidgetAccessor)widget).languagereload_getChildren());
            return newWidget;
        });
    }
}


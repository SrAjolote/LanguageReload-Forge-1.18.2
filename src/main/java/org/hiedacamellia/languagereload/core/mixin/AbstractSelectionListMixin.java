package org.hiedacamellia.languagereload.core.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.util.Mth;
import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractSelectionList.class})
public class AbstractSelectionListMixin {
    @Inject(method={"getEntryAtPosition(DD)Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;"}, at={@At(value="RETURN")}, cancellable=true)
    private void getEntryAtPosition(double x, double y, CallbackInfoReturnable<LanguageEntry> cir) {
        if ((Object)this instanceof LanguageListWidget) {
            LanguageListWidget list = (LanguageListWidget)(Object)this;
            int halfRowWidth = list.getRowWidth() / 2;
            int center = list.getX0() + list.getWidth() / 2;
            int minX = center - halfRowWidth;
            int maxX = center + halfRowWidth;
            int scrollbarPositionX = list.getScrollbarPosition();
            int m = Mth.floor(y - (double)list.getY0()) - list.getHeaderHeight() + (int)list.getScrollAmount() - 4 + 2;
            int entryIndex = m / list.getItemHeight();
            cir.setReturnValue(x < (double)scrollbarPositionX && x >= (double)minX && x <= (double)maxX && entryIndex >= 0 && m >= 0 && entryIndex < list.getItemCountA() ? list.getChildren().get(entryIndex) : null);
        }
    }
}

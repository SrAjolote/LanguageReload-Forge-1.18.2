package org.hiedacamellia.languagereload.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.core.access.ILanguageOptionsScreen;
import org.hiedacamellia.languagereload.core.mixin.EntryListWidgetAccessor;

public class LanguageListWidget
extends ObjectSelectionList<LanguageEntry> {
    private final Component title;
    private final LanguageSelectScreen screen;

    public LanguageListWidget(Minecraft client, LanguageSelectScreen screen, int width, int height, Component title) {
        super(client, width, height, 48, height - 55 + 4, 24);
        this.title = title;
        this.screen = screen;
        this.setRenderHeader(true, 13);
        this.centerListVertically = false;
    }

    @Override
    protected void renderDecorations(PoseStack poseStack, int x, int y) {
        MutableComponent headerText = this.title.copy().withStyle(new ChatFormatting[]{ChatFormatting.UNDERLINE, ChatFormatting.BOLD});
        int headerPosX = x + this.width / 2 - this.minecraft.font.width((FormattedText)headerText) / 2;
        int headerPosY = Math.min(this.y0 + 3, y);
        this.minecraft.font.draw(poseStack, (Component)headerText, headerPosX, headerPosY, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        LanguageEntry selectedEntry = (LanguageEntry)this.getSelected();
        if (selectedEntry == null) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        if (keyCode == 32 || keyCode == 257) {
            selectedEntry.toggle();
            this.setFocused(null);
            ((ILanguageOptionsScreen)this.screen).languagereload_focusEntry(selectedEntry);
            return true;
        }
        if (Screen.hasShiftDown()) {
            if (keyCode == 264) {
                selectedEntry.moveDown();
                return true;
            }
            if (keyCode == 265) {
                selectedEntry.moveUp();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        LanguageEntry entry = (LanguageEntry)this.getEntryAtPosition(mouseX, mouseY);
        if (entry == null && button == 0) {
            return true;
        }
        if (entry != null && entry.mouseClicked(mouseX, mouseY, button)) {
            LanguageEntry focusedEntry = (LanguageEntry)this.getFocused();
            if (focusedEntry != entry && focusedEntry instanceof ContainerEventHandler) {
                ContainerEventHandler parentElement = (ContainerEventHandler)focusedEntry;
                parentElement.setFocused(null);
            }
            this.setDragging(true);
            return true;
        }
        return ((EntryListWidgetAccessor)((Object)this)).languagereload_isScrolling();
    }

    public LanguageSelectScreen getScreen() {
        return this.screen;
    }

    public int getRowHeight() {
        return this.itemHeight;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    public int getScrollbarPosition() {
        return this.getRight() - 6;
    }

    public void updateScroll() {
        this.setScrollAmount(this.getScrollAmount());
    }

    public int getX0() {
        return this.x0;
    }

    public int getY0() {
        return this.y0;
    }

    public int getHeaderHeight() {
        return this.headerHeight;
    }

    public int getItemHeight() {
        return this.itemHeight;
    }

    public int getItemCountA() {
        return this.getItemCount();
    }

    public List<LanguageEntry> getChildren() {
        return this.children();
    }
}

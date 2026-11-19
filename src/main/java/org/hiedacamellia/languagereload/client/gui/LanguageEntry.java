package org.hiedacamellia.languagereload.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;
import org.hiedacamellia.languagereload.core.access.ILanguageOptionsScreen;

public class LanguageEntry
extends ObjectSelectionList.Entry<LanguageEntry> {
    private static final Component DEFAULT_LANGUAGE_TOOLTIP = new TranslatableComponent("language.default.tooltip");
    private static final ResourceLocation TEXTURE = new ResourceLocation("languagereload", "textures/language_selection.png");
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 64;
    private static final int HOVERED_V_OFFSET = 24;
    private final Minecraft client = Minecraft.getInstance();
    private final String code;
    private final LanguageInfo language;
    private final LinkedList<String> selectedLanguages;
    private final Runnable refreshListsAction;
    private final List<AbstractWidget> buttons = new ArrayList<AbstractWidget>();
    private final Button addButton = this.addButton(15, 24, 0, 0, __ -> this.add());
    private final Button removeButton = this.addButton(15, 24, 15, 0, __ -> this.remove());
    private final Button moveUpButton = this.addButton(11, 11, 31, 0, __ -> this.moveUp());
    private final Button moveDownButton = this.addButton(11, 11, 31, 13, __ -> this.moveDown());
    private LanguageListWidget parentList;

    public LanguageEntry(Runnable refreshListsAction, String code, LanguageInfo language, LinkedList<String> selectedLanguages) {
        this.code = code;
        this.language = language;
        this.selectedLanguages = selectedLanguages;
        this.refreshListsAction = refreshListsAction;
    }

    private Button addButton(int width, int height, int u, int v, Button.OnPress action) {
        ImageButton button = new ImageButton(0, 0, width, height, u, v, 24, TEXTURE, 64, 64, action);
        button.visible = false;
        this.buttons.add((AbstractWidget)button);
        return button;
    }

    private boolean isDefault() {
        return this.code.equals("en_us");
    }

    private boolean isSelected() {
        return this.selectedLanguages.contains(this.code);
    }

    private boolean isFirst() {
        return this.code.equals(this.selectedLanguages.peekFirst());
    }

    private boolean isLast() {
        return this.code.equals(this.selectedLanguages.peekLast());
    }

    private boolean isEntryFocused() {
        return this.parentList != null && this.parentList.getSelected() == this;
    }

    private void add() {
        if (this.isEntryFocused()) {
            this.parentList.setSelected(null);
        }
        this.selectedLanguages.addFirst(this.code);
        this.refreshListsAction.run();
    }

    private void remove() {
        if (this.isEntryFocused()) {
            this.parentList.setSelected(null);
        }
        this.selectedLanguages.remove(this.code);
        this.refreshListsAction.run();
    }

    public void toggle() {
        if (!this.isSelected()) {
            this.add();
        } else {
            this.remove();
        }
    }

    public void moveUp() {
        if (!this.isSelected()) {
            return;
        }
        if (this.isFirst()) {
            return;
        }
        int index = this.selectedLanguages.indexOf(this.code);
        this.selectedLanguages.add(index - 1, this.selectedLanguages.remove(index));
        this.refreshListsAction.run();
    }

    public void moveDown() {
        if (!this.isSelected()) {
            return;
        }
        if (this.isLast()) {
            return;
        }
        int index = this.selectedLanguages.indexOf(this.code);
        this.selectedLanguages.add(index + 1, this.selectedLanguages.remove(index));
        this.refreshListsAction.run();
    }

    @Override
    public void render(PoseStack poseStack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        x -= 2;
        y -= 2;
        if (hovered || this.isEntryFocused()) {
            GuiComponent.fill(poseStack, x + 1, y + 1, x + entryWidth - 1, y + entryHeight + 3, -1601138544);
            this.buttons.forEach(button -> {
                button.visible = false;
            });
            final int btnX = x;
            final int btnY = y;
            this.renderButtons((button, buttonX, buttonY) -> {
                button.x = buttonX;
                button.y = buttonY;
                button.visible = true;
                button.render(poseStack, mouseX, mouseY, tickDelta);
            }, btnX, btnY);
            if (this.isDefault()) {
                this.renderDefaultLanguageTooltip(poseStack, x, y);
            }
        }
        this.client.font.draw(poseStack, this.language.getName(), (float)(x + 29), (float)(y + 3), 0xFFFFFF);
        this.client.font.draw(poseStack, this.language.getRegion(), (float)(x + 29), (float)(y + 14), 0x808080);
    }

    private void renderButtons(ButtonRenderer renderer, int x, int y) {
        if (this.isSelected()) {
            renderer.render(this.removeButton, x, y);
            if (!this.isFirst()) {
                renderer.render(this.moveUpButton, x + this.removeButton.getWidth() + 1, y);
            }
            if (!this.isLast()) {
                renderer.render(this.moveDownButton, x + this.removeButton.getWidth() + 1, y + this.moveUpButton.getHeight() + 2);
            }
        } else {
            renderer.render(this.addButton, x + 7, y);
        }
    }

    private void renderDefaultLanguageTooltip(PoseStack poseStack, int x, int y) {
        List<FormattedCharSequence> tooltip = this.client.font.split(DEFAULT_LANGUAGE_TOOLTIP, this.parentList.getRowWidth() - 6);
        this.parentList.getScreen().renderTooltip(poseStack, tooltip, x + 3, y + this.parentList.getRowHeight() + 4);
    }

    @Override
    public Component getNarration() {
        return new TranslatableComponent("narrator.select", this.language.toString());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AbstractWidget widget : this.buttons) {
            if (!widget.mouseClicked(mouseX, mouseY, button)) continue;
            ((ILanguageOptionsScreen)this.parentList.getScreen()).languagereload_focusList(this.parentList);
            return true;
        }
        return false;
    }

    public void setParent(LanguageListWidget list) {
        this.parentList = list;
    }

    public LanguageListWidget getParent() {
        return this.parentList;
    }

    public String getCode() {
        return this.code;
    }

    public LanguageInfo getLanguage() {
        return this.language;
    }

    @FunctionalInterface
    private static interface ButtonRenderer {
        public void render(Button var1, int var2, int var3);
    }
}

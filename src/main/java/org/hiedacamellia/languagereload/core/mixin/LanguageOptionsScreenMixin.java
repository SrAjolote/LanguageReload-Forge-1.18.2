package org.hiedacamellia.languagereload.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.hiedacamellia.languagereload.LanguageReload;
import org.hiedacamellia.languagereload.client.gui.LanguageEntry;
import org.hiedacamellia.languagereload.client.gui.LanguageListWidget;
import org.hiedacamellia.languagereload.core.access.ILanguageOptionsScreen;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LanguageSelectScreen.class})
public abstract class LanguageOptionsScreenMixin
extends OptionsSubScreen
implements ILanguageOptionsScreen {
    @Unique
    private LanguageListWidget availableLanguageList;
    @Unique
    private LanguageListWidget selectedLanguageList;
    @Unique
    private EditBox searchBox;
    @Unique
    private final LinkedList<String> selectedLanguages = new LinkedList<String>();
    @Unique
    private final Map<String, LanguageEntry> languageEntries = new LinkedHashMap<String, LanguageEntry>();

    LanguageOptionsScreenMixin(Screen parent, Options options, Component title) {
        super(parent, options, title);
    }

    @Unique
    private LanguageSelectScreen it() {
        return (LanguageSelectScreen)(Object)this;
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    void onConstructed(Screen parent, Options options, LanguageManager languageManager, CallbackInfo ci) {
        LanguageInfo selectedInfo = languageManager.getSelected();
        String currentLangCode = selectedInfo != null ? selectedInfo.getCode() : "";
        if (!currentLangCode.isEmpty() && !currentLangCode.equals("*")) {
            this.selectedLanguages.add(currentLangCode);
        }
        this.selectedLanguages.addAll(ClientConfig.fallbacks);
        for (LanguageInfo language : languageManager.getLanguages()) {
            String code = language.getCode();
            this.languageEntries.put(code, new LanguageEntry(this::refresh, code, language, this.selectedLanguages));
        }
    }

    @Inject(method={"init"}, at={@At(value="HEAD")}, cancellable=true)
    void onInit(CallbackInfo ci) {
        this.searchBox = new EditBox(this.minecraft.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, TextComponent.EMPTY);
        this.searchBox.setResponder(__ -> this.refresh());
        this.addWidget(this.searchBox);
        this.setInitialFocus(this.searchBox);

        int listWidth = Math.min(this.width / 2 - 4, 200);
        this.availableLanguageList = new LanguageListWidget(this.minecraft, this.it(), listWidth, this.height, new TranslatableComponent("pack.available.title"));
        this.selectedLanguageList = new LanguageListWidget(this.minecraft, this.it(), listWidth, this.height, new TranslatableComponent("pack.selected.title"));
        this.availableLanguageList.setLeftPos(this.width / 2 - 4 - listWidth);
        this.selectedLanguageList.setLeftPos(this.width / 2 + 4);
        this.addWidget(this.availableLanguageList);
        this.addWidget(this.selectedLanguageList);
        this.refresh();

        this.addRenderableWidget(Option.FORCE_UNICODE_FONT.createButton(this.options, this.width / 2 - 155, this.height - 28, 150));
        this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height - 28, 150, 20, CommonComponents.GUI_DONE, this::onDone));
        super.init();
        ci.cancel();
    }

    @Unique
    private void onDone(Button button) {
        if (this.minecraft == null) {
            return;
        }
        this.minecraft.setScreen(this.lastScreen);
        String language = this.selectedLanguages.peekFirst();
        if (language == null) {
            LanguageReload.setLanguage("*", new LinkedList<String>());
        } else {
            LinkedList<String> fallbacks = new LinkedList<String>(this.selectedLanguages);
            fallbacks.removeFirst();
            LanguageReload.setLanguage(language, fallbacks);
        }
    }

    @Override
    public void languagereload_focusList(LanguageListWidget list) {
        this.setFocused(list);
    }

    @Override
    public void languagereload_focusEntry(LanguageEntry entry) {
        if (entry.getParent() != null) {
            this.setFocused(entry.getParent());
            entry.getParent().setSelected(entry);
        }
    }

    @Unique
    private void refresh() {
        this.refreshList(this.selectedLanguageList, this.selectedLanguages.stream().map(this.languageEntries::get).filter(Objects::nonNull));
        this.refreshList(this.availableLanguageList, this.languageEntries.values().stream().filter(entry -> {
            if (this.selectedLanguageList.children().contains(entry)) {
                return false;
            }
            String query = this.searchBox.getValue().toLowerCase(Locale.ROOT);
            String langCode = entry.getCode().toLowerCase(Locale.ROOT);
            String langName = entry.getLanguage().getName().toLowerCase(Locale.ROOT);
            return langCode.contains(query) || langName.contains(query);
        }));
    }

    @Unique
    private void refreshList(LanguageListWidget list, Stream<? extends LanguageEntry> entries) {
        LanguageEntry selectedEntry = (LanguageEntry)list.getSelected();
        list.setSelected(null);
        list.children().clear();
        entries.forEach(entry -> {
            list.children().add(entry);
            entry.setParent(list);
            if (entry == selectedEntry) {
                list.setSelected(entry);
            }
        });
        list.setScrollAmount(list.getScrollAmount());
    }

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true)
    void onRender(PoseStack poseStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.renderBackground(poseStack);
        this.availableLanguageList.render(poseStack, mouseX, mouseY, delta);
        this.selectedLanguageList.render(poseStack, mouseX, mouseY, delta);
        this.searchBox.render(poseStack, mouseX, mouseY, delta);
        GuiComponent.drawCenteredString(poseStack, this.minecraft.font, this.title, this.width / 2, 8, 0xFFFFFF);
        GuiComponent.drawCenteredString(poseStack, this.minecraft.font, LanguageSelectScreen.WARNING_LABEL, this.width / 2, this.height - 46, 0x808080);
        super.render(poseStack, mouseX, mouseY, delta);
        ci.cancel();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }
}

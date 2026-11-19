package org.hiedacamellia.languagereload.core.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

@Mixin(value={KeyboardHandler.class})
public abstract class KeyboardMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void debugFeedbackTranslated(String var1, Object ... var2);

    @Shadow
    protected abstract void debugWarningTranslated(String var1, Object ... var2);

    @Unique
    private void processLanguageReloadKeys() {
        if (Screen.hasShiftDown()) {
            final LanguageManager languageManager = this.minecraft.getLanguageManager();
            final LanguageInfo language = languageManager.getLanguage(ClientConfig.previousLanguage);
            final boolean noLanguage = ClientConfig.previousLanguage.equals("*");
            if (language == null && !noLanguage) {
                this.debugWarningTranslated("debug.reload_languages.switch.failure", new Object[0]);
            } else {
                LanguageReload.setLanguage(ClientConfig.previousLanguage, ClientConfig.previousFallbacks);
                ArrayList<Component> languages = new ArrayList<Component>(){
                    {
                        if (noLanguage) {
                            this.add(new TextComponent("\u2205"));
                        }
                        if (language != null) {
                            this.add(new TextComponent(language.toString()));
                        }
                        this.addAll(ClientConfig.fallbacks.stream().map(languageManager::getLanguage).filter(Objects::nonNull).map(l -> (Component)new TextComponent(l.toString())).toList());
                    }
                };
                this.debugFeedbackTranslated("debug.reload_languages.switch.success", ComponentUtils.formatList((Collection<Component>)languages, (Component)new TextComponent(", ")));
            }
        } else {
            LanguageReload.reloadLanguages();
            this.debugFeedbackTranslated("debug.reload_languages.message", new Object[0]);
        }
    }

    @Inject(method={"handleDebugKeys"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V", ordinal=6, shift=At.Shift.AFTER)})
    private void onProcessF3$addHelp(int key, CallbackInfoReturnable<Boolean> cir) {
        this.minecraft.gui.getChat().addMessage(new TranslatableComponent("debug.reload_languages.help"));
    }

    @Inject(method={"handleDebugKeys"}, at={@At(value="RETURN")}, cancellable=true)
    private void onProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == 74) {
            this.processLanguageReloadKeys();
            cir.setReturnValue(true);
        }
    }

    @Inject(method={"keyPress"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V")}, cancellable=true)
    private void onOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (InputConstants.isKeyDown((long)window, (int)292) && key == 74) {
            if (action != 0) {
                this.processLanguageReloadKeys();
            }
            ci.cancel();
        }
    }

    @Inject(method={"charTyped"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V", ordinal=0)}, cancellable=true)
    private void onOnChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
        if (InputConstants.isKeyDown((long)window, (int)292) && InputConstants.isKeyDown((long)window, (int)74)) {
            ci.cancel();
        }
    }
}

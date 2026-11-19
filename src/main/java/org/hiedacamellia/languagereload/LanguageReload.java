package org.hiedacamellia.languagereload;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hiedacamellia.languagereload.core.access.IAdvancementsScreen;
import org.hiedacamellia.languagereload.core.config.ClientConfig;
import org.hiedacamellia.languagereload.core.mixin.BookScreenAccessor;
import org.hiedacamellia.languagereload.core.mixin.ClientChunkManagerAccessor;
import org.hiedacamellia.languagereload.core.mixin.ClientChunkMapAccessor;
import org.hiedacamellia.languagereload.core.mixin.SignTextAccessor;

@Mod(value="languagereload")
public class LanguageReload {
    public static final String MODID = "languagereload";
    public static final Logger LOGGER = LogManager.getLogger("languagereload");
    public static boolean shouldSetSystemLanguage = false;
    public static final String NO_LANGUAGE = "*";

    public LanguageReload() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    public static void reloadLanguages() {
        Minecraft client = Minecraft.getInstance();
        client.getLanguageManager().onResourceManagerReload(client.getResourceManager());
        client.updateTitle();
        client.gui.getChat().rescaleChat();
        Screen screen = client.screen;
        if (screen instanceof BookViewScreen) {
            BookViewScreen bookScreen = (BookViewScreen)screen;
            ((BookScreenAccessor)bookScreen).languagereload_setCachedPageIndex(-1);
        } else {
            screen = client.screen;
            if (screen instanceof AdvancementsScreen) {
                AdvancementsScreen advancementsScreen = (AdvancementsScreen)screen;
                ((IAdvancementsScreen)advancementsScreen).languagereload_recreateWidgets();
            }
        }
        if (client.level != null) {
            ClientChunkManagerAccessor chunkManager = (ClientChunkManagerAccessor)client.level.getChunkSource();
            ClientChunkCache.Storage storage = chunkManager.languagereload_getChunks();
            AtomicReferenceArray<LevelChunk> chunks = ((ClientChunkMapAccessor)(Object)storage).languagereload_getChunks();
            for (int i = 0; i < chunks.length(); ++i) {
                LevelChunk chunk = chunks.get(i);
                if (chunk == null) continue;
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof SignBlockEntity)) continue;
                    SignBlockEntity sign = (SignBlockEntity)blockEntity;
                    ((SignTextAccessor)sign).languagereload_setRenderMessages(null);
                }
            }
        }
    }

    public static void setLanguage(String language, LinkedList<String> fallbacks) {
        Minecraft client = Minecraft.getInstance();
        LanguageManager languageManager = client.getLanguageManager();
        if (!ClientConfig.SPEC.isLoaded()) {
            return;
        }
        LanguageInfo selectedInfo = languageManager.getSelected();
        String currentCode = selectedInfo != null ? selectedInfo.getCode() : "";
        boolean languageIsSame = currentCode.equals(language);
        boolean fallbacksAreSame = ClientConfig.fallbacks.equals(fallbacks);
        if (languageIsSame && fallbacksAreSame) {
            return;
        }
        ClientConfig.PREVIOUS_LANGUAGE.set(currentCode);
        ClientConfig.PREVIOUS_FALLBACKS.set(ClientConfig.fallbacks);
        ClientConfig.LANGUAGE.set(language);
        ClientConfig.FALLBACKS.set(fallbacks);
        ClientConfig.SPEC.save();
        LanguageInfo newInfo = languageManager.getLanguage(language);
        if (newInfo != null) {
            languageManager.setSelected(newInfo);
        }
        client.options.languageCode = language;
        client.options.save();
        ClientConfig.load();
        LanguageReload.reloadLanguages();
    }
}

package org.hiedacamellia.languagereload.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid="languagereload", bus=Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue MULTILINGUAL_ITEM_SEARCH = BUILDER.define("multilingualItemSearch", true);
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FALLBACKS = BUILDER.defineList("fallbacks", new ArrayList<String>(), o -> o instanceof String);
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> PREVIOUS_FALLBACKS = BUILDER.defineList("previousFallbacks", new ArrayList<String>(), o -> o instanceof String);
    public static final ForgeConfigSpec.ConfigValue<String> LANGUAGE = BUILDER.define("language", "");
    public static final ForgeConfigSpec.ConfigValue<String> PREVIOUS_LANGUAGE = BUILDER.define("previousLanguage", "");
    public static boolean multilingualItemSearch = false;
    public static LinkedList<String> fallbacks = new LinkedList<String>();
    public static LinkedList<String> previousFallbacks = new LinkedList<String>();
    public static String language = "*";
    public static String previousLanguage = "*";
    public static ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        ClientConfig.load();
    }

    public static void load() {
        multilingualItemSearch = MULTILINGUAL_ITEM_SEARCH.get();
        fallbacks = new LinkedList<String>(FALLBACKS.get());
        previousFallbacks = new LinkedList<String>(PREVIOUS_FALLBACKS.get());
        language = LANGUAGE.get();
        previousLanguage = PREVIOUS_LANGUAGE.get();
    }
}

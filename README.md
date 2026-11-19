# Language Reload (Unofficial Forge)

An unofficial backport of Language Reload for Minecraft Forge 1.18.2.

### About
In large modpacks, changing the game language or applying translation resource packs forces Minecraft to reload every texture, model, and sound, often freezing the client for minutes. This mod modifies the reload pipeline so only language files are updated when switching languages.

### Features
- **Fast language switching:** Reloads translation files without reloading textures or models.
- **Language fallback:** Allows selecting multiple languages in priority order so missing translations fall back to another language (such as English) instead of showing raw translation keys.
- **Search bar:** Adds a search box to the language selection menu.
- **Hotkey:** Press `F3 + J` to reload language files in-game.
- **Client-side only:** Does not need to be installed on servers.

### Building
Run `./gradlew build` using JDK 17. The compiled mod will be generated in `build/libs/`.

### Credits
This is an unofficial Forge 1.18.2 backport. Full credit goes to the original authors:
- Original Fabric mod by [Jerozgen](https://modrinth.com/mod/language-reload)
- Forge 1.20.1+ port by hiedacamellia / kaguya154

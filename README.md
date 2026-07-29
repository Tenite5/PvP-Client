# pvp client

A lightweight, client-side PvP enhancement mod for Minecraft (Fabric), by **Tenite**.

Everything runs on your screen only — server-safe visual and quality-of-life features inspired by clients like Lunar and Feather.

## Features

### HUD Overlays
- **FPS Counter** — clean FPS display
- **Ping Display** — live latency to the current server
- **Potion Effects** — active effects with timers, 5 anchor positions
- **Armor Status** — durability of your equipped armor, optional per-slot background
- **Saturation** — current saturation and a configurable preview of saturation from held food

The FPS, ping, effects, and armor overlays are freely draggable and scalable in the built-in **HUD editor**. Saturation is drawn directly on the vanilla hunger bar.

### Combat & Camera
- **Zoom** — spyglass-style zoom on a key or mouse button, with smooth zoom and cinematic camera options
- **Freelook** — look around without turning your character (hold or toggle, perspective choice, sensitivity and invert options)
- **Toggle Sprint** — sprint without holding the key
- **Hit Color** — recolor the damage flash on entities, with a separate color for their armor
- **View Model** — scale and reposition your hand, adjust shield height, fire overlay height, and resize or move the totem pop
- **Hitboxes** — configurable entity boxes with color, fill, transparency, player-only, and box-only modes

### Atmosphere
- **Weather** — force clear, rain, or thunder on your screen only
- **Time of Day** — lock the visuals to sunrise, day, noon, sunset, night, or midnight

## Usage

- Press **Right Shift** (rebindable, mouse buttons supported) to open the client menu.
- Click a module card to activate it, or **Edit** to open its settings.
- Use **Edit HUD** to drag and scale overlays.
- All settings are saved automatically.

## Versions

Built for Minecraft **26.2**, **1.21.11**, and **1.21.4** (Fabric Loader + Fabric API required).

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for your Minecraft version.
2. Install the matching [Fabric API](https://modrinth.com/mod/fabric-api).
3. Download the matching PvP Client JAR from the [`versions`](versions) directory.
4. Place both JARs in your profile's `mods` directory.

Do not install more than one PvP Client version at the same time.

## Building

The current source target is Minecraft 26.2. Compatibility ports are maintained in [`ports/mc1_21_11`](ports/mc1_21_11) and [`ports/mc1_21_4`](ports/mc1_21_4).

Build every supported version:

```powershell
.\gradlew.bat buildAllVersions
```

On Linux or macOS, use `./gradlew buildAllVersions`. Finished production JARs are copied into [`versions`](versions).

## License

[MIT](LICENSE) — Copyright (c) 2026 Tenite

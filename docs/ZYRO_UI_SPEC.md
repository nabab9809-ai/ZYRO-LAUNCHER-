# ZYRO Launcher — UI Specification

Status: initial product/UI specification; implementation has not started.

## Product direction

ZYRO is an original Android Minecraft: Java Edition launcher experience. Its interface must have its own visual identity and interaction design. Zalith Launcher 2 may be used as broad inspiration for launcher usability and navigation patterns, but ZYRO must not copy its exact layouts, assets, icons, or branding.

The existing ZYRO name and app icon are already established and must be preserved.

## Global experience

- Orientation: landscape only across the entire app, including login, settings, managers, and dialogs.
- Theme: dark black surfaces with a crimson primary accent.
- Visual quality: polished, modern, uncluttered, and consistent.
- Motion: smooth, restrained transitions and responsive feedback; animations must not block interaction.
- Device support: adapt to a broad range of Android phones and tablets, screen sizes, Android versions, memory capacities, CPU ABIs, and GPU capabilities where dependencies permit. Do not hardcode support for one phone model.
- Performance: use adaptive resource and memory behaviour. Do not assume every device supports every renderer or graphics feature.

## Navigation

Use a custom vertical navigation rail on the left as the initial design direction. The main content area changes to the selected destination.

Initial destinations:
1. Home
2. Versions
3. Mods
4. Shaders
5. Resource Packs
6. Worlds
7. Settings

The navigation rail is a design decision, not a mandate to copy another launcher.

## Home dashboard

The landscape dashboard is split into two primary areas:

### Account and player area
- Offer both Offline Account and Microsoft Account sign-in.
- Show the selected account and relevant profile details.
- Show a Minecraft character preview.
- For Microsoft accounts, retrieve and display the authenticated Minecraft Java profile skin and cape when available.
- For offline accounts, allow a username and a supported local/custom skin workflow where technically feasible.
- Do not collect or store Microsoft passwords. Use a secure official Microsoft authentication flow and protect tokens.

### Game/profile area
- Show installed game profiles/versions, including Vanilla and modded profiles.
- Let the user select a profile and see its relevant details.
- Provide clear actions to create/import/manage profiles.
- Provide a prominent Play button for the selected profile.
- Show download, install, and launch progress and useful error messages.

## Management sections

- Versions and profiles: install, update, select, and manage Minecraft versions and separate configurations.
- Mods: browse local files, import, enable/disable where supported, and remove mods.
- Shaders: import, view, and manage shader packs.
- Resource packs: import, view, enable/disable, and remove resource packs.
- Worlds: list, import/export, and manage local worlds with safe file handling.

Actual capabilities depend on Minecraft version, loader, renderer, and filesystem permissions; the UI must not imply unsupported operations work.

## Settings

Provide a structured settings area with at least:

- Graphics and renderer: compatible renderer selection and renderer-specific options; show only options supported by the current device/runtime.
- Java/runtime: runtime selection/management and Java configuration.
- Memory/performance: RAM allocation with safe device-aware recommendations, resolution, and relevant performance controls.
- Game: JVM/game arguments and per-profile configuration where supported.
- Controls: touch controls and input configuration.
- Launcher: appearance, language, downloads, storage, and behaviour.
- Diagnostics: logs, crash reports, and troubleshooting information.

## Engineering guardrails

- Preserve and verify the existing Minecraft launch pipeline while the new interface is developed.
- Audit existing account, version, runtime, renderer, mods, resource-pack, shader, and world-management implementations before replacing or duplicating them.
- Separate presentation from launcher services so the new UI does not tightly couple to Amethyst/Pojav-specific screens.
- Reuse compatible, properly licensed runtime and launcher components when that is safer than reimplementing them. An original ZYRO UI does not require rewriting the Minecraft runtime.
- Check CPU ABI, Android API level, available memory, and graphics capabilities at runtime. Clearly communicate limitations rather than promising universal renderer support.
- Never expose authentication tokens, passwords, or private signing material in logs or repository files.
- Test landscape layouts across multiple aspect ratios and test performance on both low- and high-memory Android devices.

## First implementation milestone

Before replacing the existing launcher screen:
1. Audit the current activity/navigation structure and existing feature implementations.
2. Produce a screen/component map for the new ZYRO interface.
3. Implement the new Home shell on this redesign branch while keeping existing launch services intact.
4. Build a debug APK and verify it in CI before moving on to the other sections.

No UI implementation or existing launcher code has been changed as part of this specification commit.

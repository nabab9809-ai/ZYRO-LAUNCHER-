# ZYRO Launcher Architecture Audit

Audit branch: `zyro-ui-redesign`  
Audit date: 2026-11-12  
Scope: existing Android launcher source inspected before changing the interface.

## Confirmed existing architecture

### Activity and navigation
- `LauncherActivity` is the central launcher host. It owns the fragment container, account spinner, settings button, progress layout, and listeners for authentication, settings navigation, and game launch.
- The initial screen is `MainMenuFragment`, hosted in `R.id.container_fragment`.
- The settings button currently toggles between the main menu and `LauncherPreferenceFragment`; the same top-level button is also used as a home/back-to-main-menu control.
- The current landscape activity layout is `layout-land/activity_pojav_launcher.xml`. It places the account spinner across the top, settings button at the top-right, a dynamic fragment container, and a persistent progress area at the bottom.
- The manifest currently does not force `LauncherActivity` into landscape. Some gameplay/control activities are separately configured for sensor landscape. The requested always-landscape launcher behavior therefore needs an intentional, activity-level implementation and testing, not just a landscape-only layout.

### Home screen and profile launch
- `MainMenuFragment` owns the existing home-screen controls and selected Minecraft version spinner.
- The Play action signals `ExtraConstants.LAUNCH_GAME`; it does not implement Minecraft startup itself.
- The launch listener in `LauncherActivity` validates the selected launcher profile/version and selected account, handles special LWJGL3ify cases and demo-account restrictions, then starts `MinecraftDownloader`.
- The version spinner reloads launcher profiles when the home fragment resumes.
- Existing actions include custom controls, JAR mod installation, log sharing, opening the game directory, and external news/Discord links.

### Authentication
- `SelectAuthFragment` routes to Microsoft login or local/offline login.
- `MicrosoftLoginFragment` uses a WebView OAuth flow and hands the redirect URI back through the existing `ExtraCore` event system.
- Offline/local login is implemented in a separate `LocalLoginFragment` (must be retained and inspected further before changing the account UI).
- The existing account spinner is owned/bound by `LauncherActivity`; the redesign should re-skin or wrap this account flow rather than replace the authentication pipeline.

### Preferences and device configuration
- `LauncherPreferences` contains persisted settings for RAM allocation, Java arguments/runtime, resolution scale, sustained performance, alternate surface, VSync, Zink driver preference, touch controls, gyro, mouse/gamepad behavior, download source, and integrity checks.
- `LauncherPreferenceFragment` is the root AndroidX Preference screen and reloads the in-memory preference model when settings change.
- Renderer-specific and runtime settings must be mapped from the existing preference resources/implementation before creating new controls. The UI must not expose settings that the engine cannot actually apply.

### Build and compatibility observations
- The app module currently declares minSdk 21, targetSdk 34, compileSdk 37, and uses Android Java source/target compatibility 8.
- The Android CI workflow builds a debug APK and uploads it as an artifact; its push trigger is broad (all branches except the excluded localization branch), so commits to this redesign branch should trigger CI.
- The current manifest declares OpenGL ES 2.0 as a required feature and marks PC hardware as optional. Actual renderer support still depends on bundled native components, device GPU/driver, and runtime compatibility.
- The application id is inherited from the Amethyst-based project. Branding resources are overridden for ZYRO in build types. Package-id migration is a separate high-risk change and is not part of the UI redesign milestone.

## UI redesign implications

1. Preserve `LauncherActivity` as the stable host initially; do not replace the account/authentication, profile, downloader, or game-launch event pipeline.
2. Introduce the ZYRO navigation shell incrementally around the existing fragment container. Keep the persistent progress reporting visible while downloads/installations run.
3. Build the first screen as a new presentation layer that reads the existing selected account/profile state and calls the existing launch action.
4. Reuse the existing local and Microsoft login fragments behind a ZYRO-styled account chooser. Skin/cape rendering requires a separate audit of profile/skin APIs and assets; it is not confirmed by this pass.
5. Build settings from a verified inventory of preference XMLs, preference screen classes, renderer implementations, and runtime manager APIs. Avoid decorative toggles that do not change real engine settings.
6. Test landscape behavior across the launcher, authentication WebView, preferences, dialogs, file pickers, and small/large Android screens. Do not hard-code the Vivo T3x dimensions or hardware.
7. Keep changes isolated on `zyro-ui-redesign`; do not merge into the stable branch until the APK builds and core flows are checked.

## Next implementation sequence

- [x] Create isolated redesign branch and UI specification.
- [x] Inspect launcher host, home fragment, account/auth entry points, preference model, manifest, and CI workflow.
- [ ] Finish inventory of local login, profile/skin/cape, renderer preference screens, runtime management, and profile/version resources.
- [ ] Implement the ZYRO landscape navigation shell without changing game-launch behavior.
- [ ] Rebuild the home presentation around existing account/profile/Play actions.
- [ ] Add remaining management screens in small, buildable increments.
- [ ] Run CI and test install/launch, login, profile selection, settings, and game launch before sharing an APK.

## Known gaps from this pass

- Skin/cape retrieval and 3D character preview were not yet traced end-to-end.
- The complete renderer selection implementation and renderer compatibility checks were not yet audited.
- Runtime manager and all preference sub-screens were not yet inventoried.
- No UI implementation or APK build has been made on this branch yet.

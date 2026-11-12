# ZYRO Launcher

ZYRO is an Android-focused community launcher project based on Amethyst Android.

## Project goals

- Preserve the upstream Amethyst Java launcher functionality.
- Develop an original, modern dark-crimson interface and ZYRO branding.
- Add an engine choice at the per-installation/version level:
  - **Java**: launch through the existing Amethyst Java runtime and game pipeline.
  - **Rust Experimental**: launch through a separately integrated, compatible Horizon/Rust Android runtime when integration is technically supported.
- Keep engine-specific compatibility clear. Java loaders such as Forge/Fabric are not assumed to work in a separate Rust implementation.

## Visual system

- Background: `#100909`
- Surface: `#1A0D0F`
- Card: `#241316`
- Primary: `#B51224`
- Accent: `#FF3048`
- Main text: `#FFFFFF`
- Muted text: `#A99A9D`

## Development milestones

1. Audit upstream modules, resources, application ID, signing configuration, and CI.
2. Implement ZYRO branding and theme without removing launcher functionality.
3. Build and test a Java-engine APK in GitHub Actions.
4. Audit Horizon/Rust project architecture, licence, ABI, native libraries, data paths, and launch protocol.
5. Implement a real per-installation engine dispatcher only after the Rust runtime can be packaged and launched reliably.
6. Test on physical Android devices, including low-memory behaviour.

## Important constraints

- This project is not affiliated with Mojang or Microsoft.
- Preserve upstream notices, licences, and dependency attributions.
- Do not commit passwords, tokens, private signing keys, or personal Microsoft account data.
- Do not present the Rust option as functional until it launches the actual Rust runtime.
- Minecraft assets and accounts must be obtained and used in accordance with their applicable terms.

## Build

The upstream project documents its Android debug build as:

```bash
./gradlew :app_pojavlauncher:assembleDebug
```

The output is expected under `app_pojavlauncher/build/outputs/apk/debug/`.

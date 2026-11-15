# ZYRO Rust Engine Integration

Status: the Horizon Rust backend is now compiled and packaged into ZYRO's ARM64 debug APK. Android CI has verified the native library, generated engine data, Android JNI exports, and APK contents. **Minecraft gameplay has not yet been verified on a physical Android device**, so treat this as a test build rather than a confirmed playable release.

## Target and architecture

Integrate the Android port of Horizon as a second game backend while keeping the existing Pojav/Amethyst Java backend intact. Horizon is a separate native Rust/wgpu client, not a renderer replacement for ZYRO's Java launcher.

Reference Android port: https://github.com/RxLauncherTeam/RxLauncher-Rust-Edition  
Android branch: `android`  
Pinned submodule commit: `150a414b09a18c900489d9462a7fc035dae744b7` at `third_party/horizon`.

## Current verified state

- Existing profiles without an engine field continue to use Java (Pojav).
- The profile editor stores a Java or Rust (Horizon) backend choice per profile.
- The Play dispatcher sends Rust profiles to `dev.horizon.android.HorizonActivity` before Java-version and Java-account validation. The normal Java path remains intact.
- ZYRO builds Horizon from the pinned Rust source with Cargo for `aarch64-linux-android`; it does not download a prebuilt APK or substitute a placeholder library.
- The ARM64 debug APK contains `lib/arm64-v8a/libhorizon_android.so`, generated `assets/engine-data.zip`, the Horizon license, and third-party notices.
- CI verifies the APK ZIP, required engine-data entries, AArch64 ELF header, and the JNI exports used by the documents provider and Activity callback:
  - `Java_dev_horizon_android_HorizonDocumentsProvider_nativeCall`
  - `Java_dev_horizon_android_HorizonDocumentsProvider_nativeOpen`
  - `Java_dev_horizon_android_HorizonActivity_nativeDocumentResult`
- `HorizonDocumentsProvider` adapts Android's Storage Access Framework to Horizon's Rust document store. The Activity callback is registered by the native IME bridge.
- Latest verified debug artifact: [Android CI run 94](https://github.com/nabab9809-ai/ZYRO-LAUNCHER-/actions/runs/43682244916), artifact `app-debug (recommended)`.

## Compatibility and separation

- The pinned Android port targets Minecraft Java 26.3; it does not launch arbitrary Java Minecraft versions.
- Horizon uses its own login, installation/resource preparation, profiles, worlds, settings, and native `.hmod` mods. ZYRO does not transfer its Java account, Java profile, worlds, or Forge/Fabric/NeoForge JARs into Horizon.
- The current ZYRO build packages `arm64-v8a` only. Horizon requires Android 9 / API 28 or newer and a supported graphics stack (Vulkan or OpenGL ES 3.0).
- Devices that fail the Rust API, ABI, graphics, or bundled-runtime checks stay on the existing Java backend; Java support is not removed.

## What remains unverified

A successful build and a correctly packaged shared library do not prove that Minecraft reaches a playable world. No physical-device test has yet confirmed Horizon's first-run resource installation, sign-in, world launch, touch/keyboard/gamepad input, audio, pause/resume, or clean shutdown.

The Android CI workflow currently publishes the debug APK. The release APK step is skipped when the private `GPLAY_KEYSTORE_PASSWORD` secret is not configured; a release artifact is not required to test the debug build.

## Device test checklist

1. Install the ARM64 debug APK on Android 9 or newer.
2. Open a Rust (Horizon) profile and press Play. Confirm that Horizon's own native launcher opens.
3. Complete Horizon's own first-run resource preparation and sign-in.
4. Select its supported Minecraft version and launch a world.
5. Test touch controls, keyboard/gamepad if available, audio, pause/resume, and shutdown.
6. Switch back to a Java profile and confirm the original Pojav/Amethyst launch path still works.

Do not mark the Rust backend as gameplay-ready until those device checks pass.

## Build workflows

- [Android CI](https://github.com/nabab9809-ai/ZYRO-LAUNCHER-/actions/workflows/android.yml) builds and verifies the packaged ARM64 debug APK.
- [Horizon Rust ARM64 build](https://github.com/nabab9809-ai/ZYRO-LAUNCHER-/actions/workflows/horizon-rust-arm64.yml) builds the standalone ARM64 native library. It is configured to run when the Rust launcher dispatcher, Android packaging workflow, preparation script, manifest, Horizon Java bridge, or pinned Horizon source changes.

# ZYRO Rust Engine Integration

Status: integration work started; no Rust engine is wired into the APK yet.

## Target

Integrate the Android port of Horizon, the Rust implementation of Minecraft Java
26.3, as a second game backend in ZYRO. Keep the existing Pojav/Amethyst Java
backend intact. This is not a renderer replacement: Horizon is a separate native
game client with its own runtime, renderer, resource preparation, input and mod
system.

Reference Android port: https://github.com/RxLauncherTeam/RxLauncher-Rust-Edition
Android branch: `android`

## Compatibility facts

- The Android port targets Minecraft Java 26.3; it does not launch arbitrary
  Minecraft versions or Java mod-loader profiles.
- It is a native Rust/wgpu application and does not use ZYRO's JRE + LWJGL
  launch pipeline.
- The documented Android 64-bit builds support Android 9 / API 28 or newer.
  The primary supported ABIs are `arm64-v8a` and `x86_64`.
- It prepares its own game resources and stores its own profiles/world data.
- Its mod format is Horizon's native `.hmod`; Forge/Fabric/NeoForge Java JARs
  are not compatible with this backend.
- Vulkan/GPU and device compatibility must be checked on actual hardware.

## Required architecture

1. Add an explicit engine choice to each ZYRO profile: **Java (Pojav)** or
   **Rust (Horizon)**. Existing profiles must default to Java so current users
   are not migrated unexpectedly.
2. Route Play through a backend dispatcher. The Java route must remain unchanged.
   The Rust route must launch a separately integrated Horizon Android runtime,
   not invoke the JRE/LWJGL launcher.
3. Integrate the Android port's native libraries, NativeActivity entry point,
   generated engine tables/assets, Rust build tasks and license/third-party
   notices. Do not copy only a desktop binary or download a prebuilt APK at
   runtime.
4. Give the Rust backend its own installation/resource preparation, profile
   data, worlds, settings and native-mod storage. Do not pass Java profile
   JARs or Forge/Fabric mods to it.
5. Gate Rust availability by supported Android API level, ABI and graphics
   capabilities. Keep the Java backend available on devices that cannot run it.
6. Verify debug APK packaging and install/launch on a physical arm64 Android
   device before marking the backend usable.

## Integration sequence

- [ ] Audit the Android port's Gradle, Cargo/NDK, NativeActivity, generated
      assets and license requirements against this repository's build.
- [ ] Bring the Android engine source into the repository with its upstream
      history and required notices preserved.
- [ ] Build and package the native engine for supported Android ABIs.
- [ ] Add the Rust backend launch Activity and lifecycle/input/surface bridge.
- [ ] Add per-profile backend selection and Play dispatch.
- [ ] Connect Rust-specific resource installation, account state, worlds and
      settings without conflating them with Java instances.
- [ ] Build, install and test on physical devices; publish only after runtime
      validation.

## Important distinction

A successful Gradle build or presence of a native library is not proof that
Minecraft runs. The backend is complete only after the Rust client reaches a
world and survives pause/resume, touch input, audio and shutdown on a real
Android device.

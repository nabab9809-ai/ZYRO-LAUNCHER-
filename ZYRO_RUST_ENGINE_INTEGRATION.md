# ZYRO Rust Engine Integration

Status: Horizon's Android branch is now pinned as a Git submodule at `third_party/horizon` (upstream commit `150a414b09a18c900489d9462a7fc035dae744b7`). This tracks the real upstream source without copying or rewriting its history. The native library/assets are not yet built or packaged, so Rust gameplay is not available in the APK.

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

- [x] Add backward-compatible profile metadata for Java/Rust backend IDs.
      Existing profiles with no field resolve to Java.
- [x] Audit the Android port's Gradle, Cargo/NDK, NativeActivity, generated
      assets and license requirements against this repository's build.
- [x] Register the upstream Android engine source as a Git submodule at
      `third_party/horizon`, pinned to `150a414b09a18c900489d9462a7fc035dae744b7`.
      Clone with submodules enabled to fetch the source tree; upstream history is preserved.
- [ ] Vendor/merge the required Android notices, Java wrappers, provider and resources into ZYRO's app module.
- [ ] Build and package the native engine for supported Android ABIs.
- [x] Add the Horizon NativeActivity Java bridge, manifest registration and a
      guarded Play dispatcher. It reports a clear message until the native
      library is packaged; this is not a working Rust runtime yet.
- [x] Add the profile editor control so users can select Java or Rust without
      manually editing profile JSON. The selection is saved per profile; legacy profiles remain Java.
- [ ] Complete the native lifecycle/input/surface bridge and verify it against
      the packaged Horizon library.
- [ ] Connect Rust-specific resource installation, account state, worlds and
      settings without conflating them with Java instances.
- [ ] Build, install and test on physical devices; publish only after runtime
      validation.

## Packaging audit findings

The Horizon Android port is a complete Android application, not a drop-in
Rust library. Its Android packaging currently declares:

- A dedicated `dev.horizon.android.HorizonActivity` with the
  `android.app.lib_name=horizon_android` native entry point.
- A `HorizonDocumentsProvider`, game theme, drawable resources and Java
  wrapper classes that must be merged or adapted.
- A native `libhorizon_android.so` plus generated `engine-data.zip` and
  control assets staged by its Android build script.
- A separate Gradle application configuration (AGP 8.11.1, compile SDK 35,
  min SDK 28 by default).

ZYRO currently has one Android application module, AGP 9.3.1, compile SDK 37
and min SDK 21. Therefore, adding only the Rust shared library or including
Horizon as a second application module would not integrate it into the
installed ZYRO app. The first integration scaffold is now present in ZYRO: its manifest registers
`dev.horizon.android.HorizonActivity`, the Play handler routes Rust profiles to
that Activity, and a guard checks Android API, ABI, OpenGL ES and the presence
of `libhorizon_android.so`. The upstream Java Activity wrapper is adapted into
the app module. The Horizon source, documents provider, native library, engine
data, controls and notices still need to be brought in and built; without them
the guard deliberately refuses to launch Rust.
Rust availability must be disabled below API 28 and for unsupported ABIs or devices lacking both Vulkan 1.0 and OpenGL ES 3.0; Java remains the fallback. The launcher declares Vulkan as optional so Vulkan-only devices are not filtered out at install time.

The source audit also confirms that Horizon targets Minecraft Java 26.3 and
uses its own native `.hmod` mods and resource/world storage. It cannot be
treated as a replacement renderer for arbitrary Java profiles.

## Important distinction

A successful Gradle build or presence of a native library is not proof that
Minecraft runs. The backend is complete only after the Rust client reaches a
world and survives pause/resume, touch input, audio and shutdown on a real
Android device.


## Source pin

ZYRO tracks Horizon's `android` branch as a Git submodule in `third_party/horizon`.
The pin is the upstream commit `150a414b09a18c900489d9462a7fc035dae744b7`.
This preserves upstream history and avoids a lossy source copy. A checkout must
initialize submodules (`git submodule update --init --recursive`) before the
Horizon Cargo workspace is available locally. This is source tracking, not a
compiled or packaged engine: ZYRO still needs the Android build pipeline,
resources and native library integration before Rust can launch.


## Latest integration fixes

- Added a **Game engine** selector to the profile editor with **Java (Pojav)** and
  **Rust (Horizon)** choices. The value is saved in each profile; older profile
  JSON without the field continues to resolve to Java.
- Corrected the Rust graphics capability gate: Vulkan 1.0-capable devices are
  accepted as well as OpenGL ES 3.0 devices. Vulkan is declared optional in the
  manifest so it does not block Java-only devices from installing ZYRO.
- These changes improve selection and eligibility checks only. They do not
  supply the missing Horizon native library, generated engine data, documents
  provider/resources or runtime packaging. Rust gameplay is still unavailable
  until those pieces are integrated and tested.

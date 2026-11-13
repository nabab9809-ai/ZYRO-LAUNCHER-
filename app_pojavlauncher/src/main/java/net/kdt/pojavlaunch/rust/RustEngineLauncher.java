package net.kdt.pojavlaunch.rust;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.widget.Toast;

import java.io.File;

import dev.horizon.android.HorizonActivity;

/** Routes Rust profiles to the embedded Horizon NativeActivity when its runtime is packaged. */
public final class RustEngineLauncher {
    private RustEngineLauncher() {}

    /**
     * Handles a Rust profile. Returns true when the request was consumed, including
     * when this APK does not yet contain the native runtime.
     */
    public static boolean launch(Activity activity) {
        if (Build.VERSION.SDK_INT < 28) {
            Toast.makeText(activity, "Horizon Rust engine requires Android 9 or newer.",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        boolean supportedAbi = false;
        for (String abi : Build.SUPPORTED_ABIS) {
            if ("arm64-v8a".equals(abi) || "x86_64".equals(abi)) {
                supportedAbi = true;
                break;
            }
        }
        if (!supportedAbi) {
            Toast.makeText(activity, "Horizon Rust engine is not available for this device architecture.",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configuration = manager == null ? null : manager.getDeviceConfigurationInfo();
        boolean supportsGles3 = configuration != null
                && configuration.reqGlEsVersion >= 0x00030000;
        boolean supportsVulkan = Build.VERSION.SDK_INT >= 24
                && activity.getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL, 1);
        if (!supportsGles3 && !supportsVulkan) {
            Toast.makeText(activity,
                    "Horizon Rust engine requires Vulkan or OpenGL ES 3.0 graphics support.",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        File nativeLibrary = new File(activity.getApplicationInfo().nativeLibraryDir,
                "libhorizon_android.so");
        if (!nativeLibrary.isFile()) {
            Toast.makeText(activity,
                    "Rust engine is not bundled in this ZYRO build yet. Your Java engine is unchanged.",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        activity.startActivity(new Intent(activity, HorizonActivity.class));
        return true;
    }
}

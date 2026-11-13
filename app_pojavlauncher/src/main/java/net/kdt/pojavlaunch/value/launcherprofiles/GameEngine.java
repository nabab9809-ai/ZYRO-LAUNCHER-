package net.kdt.pojavlaunch.value.launcherprofiles;

/**
 * Game runtime selected by a Minecraft profile.
 *
 * <p>Keep this independent of Android Activities so profile JSON can be read
 * safely before either backend is initialized. Unknown or missing values from
 * old launcher profile files deliberately fall back to the existing Java
 * runtime.</p>
 */
public final class GameEngine {
    public static final String JAVA = "java";
    public static final String RUST = "rust";

    private GameEngine() {
    }

    public static String resolve(String value) {
        return RUST.equalsIgnoreCase(value) ? RUST : JAVA;
    }

    public static boolean isRust(String value) {
        return RUST.equals(resolve(value));
    }
}

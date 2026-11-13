package net.kdt.pojavlaunch.value.launcherprofiles;

import androidx.annotation.Keep;

@Keep
public class MinecraftProfile {

	public static String LATEST_RELEASE = "latest-release";
	public static String LATEST_SNAPSHOT= "latest-snapshot";

	public String name;
	/** Selected game backend. Missing values in legacy launcher_profiles.json resolve to Java. */
	public String gameEngine = GameEngine.JAVA;
	public String type;
	public String created;
	public String lastUsed;
	public String icon;
	public String lastVersionId;
	public String gameDir;
	public String javaDir;
	public String javaArgs;
	public String logConfig;
	public boolean logConfigIsXML;
	public String pojavRendererName;
	public boolean useANGLE;
	public String controlFile;
	public MinecraftResolution[] resolution;


	public static MinecraftProfile createTemplate(){
		MinecraftProfile TEMPLATE = new MinecraftProfile();
		TEMPLATE.name = "";
		TEMPLATE.gameEngine = GameEngine.JAVA;
		TEMPLATE.lastVersionId = LATEST_RELEASE;
		return TEMPLATE;
	}

	public static MinecraftProfile getDefaultProfile(){
		MinecraftProfile defaultProfile = new MinecraftProfile();
		defaultProfile.name = "Default";
		defaultProfile.gameEngine = GameEngine.JAVA;
		defaultProfile.lastVersionId = "1.7.10";
		return defaultProfile;
	}

	public MinecraftProfile(){}

	public MinecraftProfile(MinecraftProfile profile){
		name = profile.name;
		gameEngine = GameEngine.resolve(profile.gameEngine);
		type = profile.type;
		created = profile.created;
		lastUsed = profile.lastUsed;
		icon = profile.icon;
		lastVersionId = profile.lastVersionId;
		gameDir = profile.gameDir;
		javaDir = profile.javaDir;
		javaArgs = profile.javaArgs;
		logConfig = profile.logConfig;
		logConfigIsXML = profile.logConfigIsXML;
		pojavRendererName = profile.pojavRendererName;
		controlFile = profile.controlFile;
		resolution = profile.resolution;
		useANGLE = profile.useANGLE;
	}
}

package one.spectra.better_chests;

import net.neoforged.fml.ModList;

public class DependencyChecker {
    public static boolean areDependenciesPresent() {
        var clothConfigModInfo = ModList.get().getModFileById("cloth_config");
        return clothConfigModInfo != null && clothConfigModInfo.versionString().codePointAt(0) >= 18;
    }
}

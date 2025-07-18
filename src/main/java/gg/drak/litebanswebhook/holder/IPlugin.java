package gg.drak.litebanswebhook.holder;

import gg.drak.thebase.objects.handling.derived.IModifierEventable;

import java.io.File;
import java.nio.file.Path;

public interface IPlugin extends IModifierEventable {
    @Override
    default ModifierType getModifierType() {
        return ModifierType.PLUGIN;
    }

    @Override
    default File getDataFolder() {
        return getOwnFolder();
    }

    @Override
    default String getIdentifier() {
        return "LiteBansWebhook";
    }

    static File getOwnFolder() {
        return new File(getPluginsDirectory(), "LiteBansWebhook");
    }

    static File getPluginsDirectory() {
        File file = getSystemFile();

        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }

        File pluginDirectory = null;
        for (File f : files) {
            if (f.getName().equals("plugins")) {
                pluginDirectory = f;
                break;
            }
        }
        if (pluginDirectory == null) {
            file = file.getParentFile();

            files = file.listFiles();
            if (files == null) {
                return null;
            }

            for (File f : files) {
                if (f.getName().equals("plugins")) {
                    pluginDirectory = f;
                    break;
                }
            }
        }

        return pluginDirectory;
    }

    static Path getSystemPath() {
        return Path.of(System.getProperty("user.dir"));
    }

    static File getSystemFile() {
        return getSystemPath().toFile();
    }
}

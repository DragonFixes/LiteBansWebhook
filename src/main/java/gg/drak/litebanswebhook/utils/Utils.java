package gg.drak.litebanswebhook.utils;

import gg.drak.litebanswebhook.LiteBansWebhook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class Utils {
    public static final String[] FILE_NAMES = {
            "ban-added", "ban-remove", "ipban-added", "ipmute-added", "ipban-removed", "ipmute-removed",
            "kick", "mute-added", "mute-remove", "warn-added", "warn-remove", "error"
    };

    public static final String[] JSON_VALUES = {
            "{\n\"embeds\":[{\n\"title\":\"New Punishment (%server%) :timer:\",\n\"description\":\"The player `%player%` was banned by `%staff%` for `%reason%` %duration%\",\n\"color\":\"16711680\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"Punishment Revoked (%server%) :white_check_mark:\",\n\"description\":\"The player `%player%` was unbanned by `%staff%` for `%reason%`\",\n\"color\":\"3329330\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"New Punishment (%server%) :timer:\",\n\"description\":\"The player `%player%` was ip-banned by `%staff%` for `%reason%` %duration%\",\n\"color\":\"16711680\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"New Punishment (%server%) :timer:\",\n\"description\":\"The player `%player%` was ip-muted by `%staff%` for `%reason%` %duration%\",\n\"color\":\"8900346\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"Punishment Revoked (%server%) :white_check_mark:\",\n\"description\":\"The player `%player%` was un-ip-banned by `%staff%` for `%reason%`\",\n\"color\":\"3329330\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"Punishment Revoked (%server%) :white_check_mark:\",\n\"description\":\"The player `%player%` was un-ip-muted by `%staff%` for `%reason%`\",\n\"color\":\"3329330\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"New Punishment (%server%) :boot:\",\n\"description\":\"The player `%player%` was kicked by `%staff%` for `%reason%`\",\n\"color\":\"16734208\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"New Punishment (%server%) :timer:\",\n\"description\":\"The player `%player%` was muted by `%staff%` for `%reason%` %duration%\",\n\"color\":\"8900346\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"Punishment Revoked (%server%) :white_check_mark:\",\n\"description\":\"The player `%player%` was unmuted by `%staff%` for `%reason%`\",\n\"color\":\"3329330\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"New Warning (%server%) :warning:\",\n\"description\":\"The player `%player%` was warned by `%staff%` for `%reason%`\",\n\"color\":\"16777062\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"Warning Revoked (%server%) :white_check_mark:\",\n\"description\":\"The player `%player%` was unwarned by `%staff%` for `%reason%`\",\n\"color\":\"3329330\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}",
            "{\n\"embeds\":[{\n\"title\":\"Error\",\n\"description\":\"There was an error parsing this message\",\n\"color\":\"16711680\",\n\"footer\":{\"text\":\"Powered by LiteBans Webhook\"}\n}]}"
    };

    public static File getEmbedsFolder() {
        File folder = new File(LiteBansWebhook.getInstance().getDataFolder(), "embeds");
        if (! folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }

    public static void checkFiles() {
        try {
            File folder = getEmbedsFolder();

            for (int i = 0; i < FILE_NAMES.length; i++) {
                File file = new File(folder, FILE_NAMES[i] + ".json");
                if (! file.exists()) {
                    LiteBansWebhook.getLogger().info("Created File {}.json", FILE_NAMES[i]);
                    FileWriter writer = new FileWriter(file);
                    writer.write(JSON_VALUES[i]);
                    writer.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getErrorJson() {
        return getJson("error", true);
    }

    public static String getJson(String fileName) {
        return getJson(fileName, false);
    }

    public static String getJson(String fileName, boolean isError) {
        for (int i = 0; i < FILE_NAMES.length; i++) {
            if (FILE_NAMES[i].equals(fileName)) {
                return JSON_VALUES[i];
            }
        }

        if (isError) return "{}";
        else return getErrorJson(); // or throw an exception if the file name is not found
    }

    public static File getFileWith(String fileName) {
        File folder = getEmbedsFolder();

        String actualName = (fileName.endsWith(".json") ? fileName : fileName + ".json");

        return new File(folder, actualName);
    }

    public static String read(String fileName) throws IOException {
        return new String(
                Files.readAllBytes(
                        getFileWith(fileName).toPath()
                )
        );
    }
}
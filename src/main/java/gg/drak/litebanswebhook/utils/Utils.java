package gg.drak.litebanswebhook.utils;

import gg.drak.litebanswebhook.holder.InstanceHolder;
import litebans.api.Entry;
import litebans.api.Events;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static final String BAN_ADDED = "ban-added";
    public static final String BAN_REMOVED = "ban-removed";
    public static final String IPBAN_ADDED = "ipban-added";
    public static final String IPMUTE_ADDED = "ipmute-added";
    public static final String IPBAN_REMOVED = "ipban-removed";
    public static final String IPMUTE_REMOVED = "ipmute-removed";
    public static final String KICK = "kick";
    public static final String MUTE_ADDED = "mute-added";
    public static final String MUTE_REMOVED = "mute-removed";
    public static final String WARN_ADDED = "warn-added";
    public static final String WARN_REMOVED = "warn-removed";
    public static final String ERROR = "error";

    public static final String[] FILE_NAMES = {
            BAN_ADDED,
            BAN_REMOVED,
            IPBAN_ADDED,
            IPMUTE_ADDED,
            IPBAN_REMOVED,
            IPMUTE_REMOVED,
            KICK,
            MUTE_ADDED,
            MUTE_REMOVED,
            WARN_ADDED,
            WARN_REMOVED,
            ERROR,
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
        File folder = new File(InstanceHolder.getPlugin().getDataFolder(), "embeds");
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
                    InstanceHolder.getLogger().info("Created File {}.json", FILE_NAMES[i]);
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
        return getFileWith(fileName, false);
    }

    public static File getFileWith(String fileName, boolean isError) {
        File folder = getEmbedsFolder();

        String actualName = (fileName.endsWith(".json") ? fileName : fileName + ".json");

        File file = new File(folder, actualName);
        if (! file.exists()) {
            if (isError) return file;
            else return getErrorFile();
        }

        return file;
    }

    public static File getErrorFile() {
        return getFileWith("error", true);
    }

    public static String read(String fileName) throws IOException {
        return new String(
                Files.readAllBytes(
                        getFileWith(fileName).toPath()
                )
        );
    }

    public static void registerEvents() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryRemoved(Entry entry) {
                LiteBansUtils.onRemoved(entry);
            }

            @Override
            public void entryAdded(Entry entry) {
                LiteBansUtils.onAdded(entry);
            }
        });
    }

    public static String wrap(String json, Entry entry) {
        String p = LiteBansUtils.getPlayerName(entry.getUuid());
        if (p == null || p.isEmpty()) {
            p = "Unknown Player";
        }
        String executor = entry.getExecutorName();
        if (executor == null || executor.isEmpty()) {
            executor = "Unknown Player";
        }
        json = json.replace("%player%", p).replace("%staff%", executor).replace("%server%", entry.getServerOrigin());
        if (entry.getReason() == null)
            json = json.replace("%reason%", "No Reason Provided");
        else
            json = json.replace("%reason%", entry.getReason());
        if (! entry.isPermanent())
            json = json.replace("%duration%", getDurationString(entry.getDuration()));
        else
            json = json.replace("%duration%", "");

        if (entry.isIpban()) {
            json = json.replace("%ipban%", "Yes");
        } else {
            json = json.replace("%ipban%", "No");
        }

        if (entry.getId() != -1) {
            json = json.replace("%id%", String.valueOf(entry.getId()));
        } else {
            json = json.replace("%id%", "Unknown ID");
        }

        return json;
    }

    public static String withFile(String fileName, Entry entry) {
        try {
            String json = Utils.read(fileName);
            return wrap(json, entry);
        } catch (Throwable e) {
            e.printStackTrace();
            return Utils.getErrorJson();
        }
    }

    public static String getDurationString(long duration) {
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        duration -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);

        StringBuilder sb = new StringBuilder();
        sb.append(",duration `");
        if (days > 0) {
            sb.append(days);
            sb.append(" day");
            if (days != 1) {
                sb.append("s");
            }
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(" hour");
            if (hours != 1) {
                sb.append("s");
            }
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append(" minute");
            if (minutes != 1) {
                sb.append("s");
            }
        }
        if (seconds > 0) {
            sb.append(seconds);
            sb.append(" second");
            if (seconds != 1) {
                sb.append("s");
            }
        }

        if (sb.length() == 0) {
            sb.append("0 seconds");
        }
        sb.append("`");
        return sb.toString();
    }
}
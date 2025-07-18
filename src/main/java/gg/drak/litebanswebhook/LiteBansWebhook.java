package gg.drak.litebanswebhook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.drak.litebanswebhook.config.MainConfig;
import gg.drak.litebanswebhook.utils.Utils;
import gg.drak.thebase.async.AsyncUtils;
import gg.drak.thebase.objects.handling.derived.IModifierEventable;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import litebans.api.Database;
import litebans.api.Entry;
import litebans.api.Events;
import org.slf4j.Logger;

@Getter @Setter
public class LiteBansWebhook implements IModifierEventable {
    @Getter @Setter
    private static LiteBansWebhook instance;

    @Getter @Setter
    private static MainConfig config;

    @Getter @Setter
    private static ProxyServer server;
    @Getter @Setter
    private static Logger logger;

    @Inject
    public LiteBansWebhook(ProxyServer server,
                              Logger logger) {
        setServer(server);
        setLogger(logger);
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        setInstance(this);
        setConfig(new MainConfig());

        Utils.checkFiles();

        String webhookUrl = getConfig().getWebhookUrl();
        if (webhookUrl.equals("WEBHOOK_URL") || webhookUrl.isEmpty()) {
            getLogger().error("Please set the webhook URL in the config.yml file!");
        }

        getLogger().info("Plugin Enabled!");
        registerEvents();
    }

    public void registerEvents() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryRemoved(Entry entry) {
                LiteBansWebhook.entryRemoved(entry);
            }

            @Override
            public void entryAdded(Entry entry) {
                LiteBansWebhook.entryAdded(entry);
            }
        });
    }

    public static String wrap(String json, Entry entry) {
        String p = getPlayerName(entry.getUuid());
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

    public static void entryRemoved(Entry entry) {
        try {
            String fileName = "error.json";
            switch (entry.getType()) {
                case "ban":
                    if (entry.isIpban()) {
                        fileName = "ipban-remove.json";
                    } else {
                        fileName = "ban-remove.json";
                    }
                    break;
                case "mute":
                    if (entry.isIpban()) {
                        fileName = "ipmute-remove.json";
                    } else {
                        fileName = "mute-remove.json";
                    }
                    break;
                case "warn":
                    fileName = "warn-remove.json";
                    break;
            }
            HttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost(getConfig().getWebhookUrl());
            request.setHeader("Content-type", "application/json");

            String json = withFile(fileName, entry);
            StringEntity params = new StringEntity(json);
            request.setEntity(params);

            sendRequest(client, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void entryAdded(Entry entry) {
        try {
            String fileName = "error.json";
            switch (entry.getType()) {
                case "ban":
                    if (entry.isIpban()) {
                        fileName = "ipban-added.json";
                    } else {
                        fileName = "ban-added.json";
                    }
                    break;
                case "kick":
                    fileName = "kick.json";
                    break;
                case "mute":
                    if (entry.isIpban()) {
                        fileName = "ipmute-added.json";
                    } else {
                        fileName = "mute-added.json";
                    }
                    break;
                case "warn":
                    fileName = "warn-added.json";
            }
            HttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost(getConfig().getWebhookUrl());
            request.setHeader("Content-type", "application/json");

            String json = withFile(fileName, entry);
            StringEntity params = new StringEntity(json);
            request.setEntity(params);

            sendRequest(client, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendRequest(HttpClient client, HttpPost request) {
        AsyncUtils.executeAsync(() -> {
            try {
                client.execute(request);
            } catch (IOException e) {
                getLogger().error("Failed to send notification. Is the webhook valid?");
            }
        });
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

    public static String getPlayerName(String uuid) {
        try {
            PreparedStatement stmt = Database.get()
                    .prepareStatement("SELECT name FROM {history} WHERE uuid = ? ORDER BY id DESC LIMIT 1");
            try {
                stmt.setString(1, uuid);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String str = rs.getString(1);
                    if (stmt != null)
                        stmt.close();
                    return str;
                }
                if (stmt != null)
                    stmt.close();
            } catch (Throwable throwable) {
                if (stmt != null)
                    try {
                        stmt.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ModifierType getModifierType() {
        return ModifierType.PLUGIN;
    }

    @Override
    public File getDataFolder() {
        return getOwnFolder();
    }

    @Override
    public String getIdentifier() {
        return "LiteBansWebhook";
    }

    public static File getOwnFolder() {
        return new File(getPluginsDirectory(), "LiteBansWebhook");
    }

    public static File getPluginsDirectory() {
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

    public static Path getSystemPath() {
        return Path.of(System.getProperty("user.dir"));
    }

    public static File getSystemFile() {
        return getSystemPath().toFile();
    }
}

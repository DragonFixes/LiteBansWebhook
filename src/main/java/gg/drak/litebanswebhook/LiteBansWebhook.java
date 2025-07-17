package gg.drak.litebanswebhook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import com.masterhaxixu.litebanswebhook.config.MainConfig;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.drak.thebase.objects.handling.derived.IModifierEventable;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import litebans.api.Database;
import litebans.api.Entry;
import litebans.api.Events;
import org.slf4j.Logger;

public class LiteBansWebhook implements IModifierEventable {
    private static LiteBansWebhook instance;

    public static LiteBansWebhook getInstance() {
        return instance;
    }

    public static void setInstance(LiteBansWebhook instance) {
        LiteBansWebhook.instance = instance;
    }

    private static MainConfig config;

    public static MainConfig getConfig() {
        if (config == null) {
            config = new MainConfig();
        }
        return config;
    }

    public static void setConfig(MainConfig config) {
        LiteBansWebhook.config = config;
    }

    private final ProxyServer server;
    private final Logger logger;

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    @Inject
    public LiteBansWebhook(ProxyServer server,
                              Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        setInstance(this);
        setConfig(new MainConfig());

        JsonChecker.checkFiles(getDataFolder().getAbsolutePath());
        String webhookUrl = getConfig().getWebhookUrl();
        if (webhookUrl.equals("WEBHOOK_URL")
                || webhookUrl.isEmpty()) {
            getLogger().error("Please set the webhook URL in the config.yml file!");
        }

        getLogger().info("Plugin Enabled!");
        registerEvents();
    }

    public void registerEvents() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryRemoved(Entry entry) {
                LiteBansWebhook.this.entryRemoved(entry);
            }

            @Override
            public void entryAdded(Entry entry) {
                LiteBansWebhook.this.entryAdded(entry);
            }
        });
    }

    private void entryRemoved(Entry entry) {
        try {
            String p = getPlayerName(entry.getUuid());
            String json;
            StringEntity params;
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(getConfig().getWebhookUrl());
            request.setHeader("Content-type", "application/json");
            switch (entry.getType()) {
                case "ban":
                    json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/ban-remove.json")));
                    json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("SERVER", entry.getServerOrigin());
                    if (entry.getReason() == null)
                        json = json.replace("REASON", "No Reason Provided");
                    else
                        json = json.replace("REASON", entry.getReason());
                    params = new StringEntity(json);
                    request.setEntity(params);
                    break;
                case "mute":
                    json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/mute-remove.json")));
                    json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("SERVER", entry.getServerOrigin());
                    if (entry.getReason() == null)
                        json = json.replace("REASON", "No Reason Provided");
                    else
                        json = json.replace("REASON", entry.getReason());
                    params = new StringEntity(json);
                    request.setEntity(params);
                    break;
                case "warn":
                    json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/warn-remove.json")));
                    json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("SERVER", entry.getServerOrigin());
                    if (entry.getReason() == null)
                        json = json.replace("REASON", "No Reason Provided");
                    else
                        json = json.replace("REASON", entry.getReason());
                    params = new StringEntity(json);
                    request.setEntity(params);
                    break;
            }

            getServer().getScheduler().buildTask(this, () -> {
                try {
                    httpClient.execute(request);
                } catch (IOException e) {
                    this.getLogger().error("Failed to send notification. Is the webhook valid?");
                }
            }).schedule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void entryAdded(Entry entry) {
        try {
            String p = getPlayerName(entry.getUuid());
            String json;
            StringEntity params;
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(getConfig().getWebhookUrl());
            request.setHeader("Content-type", "application/json");
            switch (entry.getType()) {
                case "ban":
                    if (entry.isIpban()) {
                        json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/ipban-added.json")));
                        json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("SERVER", entry.getServerOrigin());
                        if (entry.getReason() == null)
                            json = json.replace("REASON", "No Reason Provided");
                        else
                            json = json.replace("REASON", entry.getReason());
                        if (!entry.isPermanent())
                            json = json.replace("DURATION", getDurationString(entry.getDuration()));
                        else
                            json = json.replace("DURATION", "");
                        params = new StringEntity(json);
                        request.setEntity(params);
                    } else {
                        json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/ban-added.json")));
                        json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("SERVER", entry.getServerOrigin());
                        if (entry.getReason() == null)
                            json = json.replace("REASON", "No Reason Provided");
                        else
                            json = json.replace("REASON", entry.getReason());
                        if (!entry.isPermanent())
                            json = json.replace("DURATION", getDurationString(entry.getDuration()));
                        else
                            json = json.replace("DURATION", "");
                        params = new StringEntity(json);
                        request.setEntity(params);

                    }
                    break;
                case "kick":
                    json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/kick.json")));
                    json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("REASON",
                            entry.getReason()).replace("SERVER", entry.getServerOrigin());
                    if (!entry.isPermanent())
                        json = json.replace("DURATION", getDurationString(entry.getDuration()));
                    else
                        json = json.replace("DURATION", "");
                    params = new StringEntity(json);
                    request.setEntity(params);
                    break;
                case "mute":
                    if (entry.isIpban()) {
                        json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/ipmute-added.json")));
                        json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("SERVER", entry.getServerOrigin());
                        if (entry.getReason() == null)
                            json = json.replace("REASON", "No Reason Provided");
                        else
                            json = json.replace("REASON", entry.getReason());
                        if (!entry.isPermanent())
                            json = json.replace("DURATION", getDurationString(entry.getDuration()));
                        else
                            json = json.replace("DURATION", "");
                        params = new StringEntity(json);
                        request.setEntity(params);
                    } else {
                        json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/mute-added.json")));
                        json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("SERVER", entry.getServerOrigin());
                        if (entry.getReason() == null)
                            json = json.replace("REASON", "No Reason Provided");
                        else
                            json = json.replace("REASON", entry.getReason());
                        if (!entry.isPermanent())
                            json = json.replace("DURATION", getDurationString(entry.getDuration()));
                        else
                            json = json.replace("DURATION", "");
                        params = new StringEntity(json);
                        request.setEntity(params);
                    }
                    break;
                case "warn":
                    json = new String(Files.readAllBytes(Paths.get(getDataFolder().getAbsolutePath()+"/embeds/warn-added.json")));
                    json = json.replace("PLAYER", p).replace("EXECUTOR", entry.getExecutorName()).replace("SERVER", entry.getServerOrigin());
                    if (entry.getReason() == null)
                        json = json.replace("REASON", "No Reason Provided");
                    else
                        json = json.replace("REASON", entry.getReason());
                    params = new StringEntity(json);
                    request.setEntity(params);
            }

            getServer().getScheduler().buildTask(this, () -> {
                try {
                    httpClient.execute(request);
                } catch (IOException e) {
                    this.getLogger().error("Failed to send notification. Is the webhook valid?");
                }
            }).schedule();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDurationString(long duration) {
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

    private String getPlayerName(String uuid) {
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

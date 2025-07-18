package gg.drak.litebanswebhook.utils;

import gg.drak.litebanswebhook.holder.InstanceHolder;
import gg.drak.thebase.async.AsyncUtils;
import litebans.api.Database;
import litebans.api.Entry;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LiteBansUtils {
    public static void onRemoved(Entry entry) {
        try {
            String fileName = "error.json";
            switch (entry.getType()) {
                case "ban":
                    if (entry.isIpban()) {
                        fileName = Utils.IPBAN_REMOVED;
                    } else {
                        fileName = Utils.BAN_REMOVED;
                    }
                    break;
                case "mute":
                    if (entry.isIpban()) {
                        fileName = Utils.IPMUTE_REMOVED;
                    } else {
                        fileName = Utils.MUTE_REMOVED;
                    }
                    break;
                case "warn":
                    fileName = Utils.WARN_REMOVED;
                    break;
            }

            sendRequestWithFile(fileName, entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onAdded(Entry entry) {
        try {
            String fileName = "error.json";
            switch (entry.getType()) {
                case "ban":
                    if (entry.isIpban()) {
                        fileName = Utils.IPBAN_ADDED;
                    } else {
                        fileName = Utils.BAN_ADDED;
                    }
                    break;
                case "mute":
                    if (entry.isIpban()) {
                        fileName = Utils.IPMUTE_ADDED;
                    } else {
                        fileName = Utils.MUTE_ADDED;
                    }
                    break;
                case "kick":
                    fileName = Utils.KICK;
                    break;
                case "warn":
                    fileName = Utils.WARN_ADDED;
                    break;
            }

            sendRequestWithFile(fileName, entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendRequestWithFile(String fileName, Entry entry) throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(InstanceHolder.getConfig().getWebhookUrl());
        request.setHeader("Content-type", "application/json");

        String json = Utils.withFile(fileName, entry);
        StringEntity params = new StringEntity(json);
        request.setEntity(params);

        sendRequest(client, request);
    }

    public static void sendRequest(HttpClient client, HttpPost request) {
        AsyncUtils.executeAsync(() -> {
            try {
                client.execute(request);
            } catch (IOException e) {
                InstanceHolder.getLogger().error("Failed to send notification. Is the webhook valid?");
            }
        });
    }

    public static String getPlayerName(String uuid) {
        try (PreparedStatement stmt = Database.get()
                .prepareStatement("SELECT name FROM {history} WHERE uuid = ? ORDER BY id DESC LIMIT 1")) {
            stmt.setString(1, uuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

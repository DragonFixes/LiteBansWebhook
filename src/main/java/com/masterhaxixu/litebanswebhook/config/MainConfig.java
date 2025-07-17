package com.masterhaxixu.litebanswebhook.config;

import com.masterhaxixu.litebanswebhook.LiteBansWebhook;
import gg.drak.thebase.storage.resources.flat.simple.SimpleConfiguration;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", LiteBansWebhook.getInstance(), true);
    }

    @Override
    public void init() {
        getWebhookUrl();
    }

    public String getWebhookUrl() {
        return getOrSetDefault("webhook-url", "WEBHOOK_URL");
    }
}

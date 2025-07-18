package gg.drak.litebanswebhook.config;

import gg.drak.litebanswebhook.holder.InstanceHolder;
import gg.drak.thebase.storage.resources.flat.simple.SimpleConfiguration;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", InstanceHolder.getPlugin(), true);
    }

    @Override
    public void init() {
        getWebhookUrl();
    }

    public String getWebhookUrl() {
        return getOrSetDefault("webhook-url", "WEBHOOK_URL");
    }
}

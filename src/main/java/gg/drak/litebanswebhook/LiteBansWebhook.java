package gg.drak.litebanswebhook;

import com.google.inject.Inject;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import gg.drak.litebanswebhook.config.MainConfig;
import gg.drak.litebanswebhook.holder.IPlugin;
import gg.drak.litebanswebhook.holder.InstanceHolder;
import gg.drak.litebanswebhook.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

@Getter @Setter
public class LiteBansWebhook implements IPlugin {
    @Inject
    public LiteBansWebhook(ProxyServer server,
                              Logger logger) {
        InstanceHolder.setServer(server);
        InstanceHolder.setLogger(logger);
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        InstanceHolder.setPlugin(this);
        InstanceHolder.setConfig(new MainConfig());

        Utils.checkFiles();

        String webhookUrl = InstanceHolder.getConfig().getWebhookUrl();
        if (webhookUrl.equals("WEBHOOK_URL") || webhookUrl.isEmpty()) {
            InstanceHolder.getLogger().error("Please set the webhook URL in the config.yml file!");
        }

        InstanceHolder.getLogger().info("Plugin Enabled!");
        Utils.registerEvents();
    }
}

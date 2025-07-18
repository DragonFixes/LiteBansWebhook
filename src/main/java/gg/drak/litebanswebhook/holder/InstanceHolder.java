package gg.drak.litebanswebhook.holder;

import com.velocitypowered.api.proxy.ProxyServer;
import gg.drak.litebanswebhook.config.MainConfig;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

public class InstanceHolder {
    @Getter @Setter
    private static MainConfig config;
    @Getter @Setter
    private static ProxyServer server;
    @Getter @Setter
    private static Logger logger;
    @Getter @Setter
    private static IPlugin plugin;
}

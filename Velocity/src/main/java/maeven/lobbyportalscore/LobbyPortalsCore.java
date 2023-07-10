package maeven.lobbyportalscore;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

@Plugin(
        id = "lobbyportals-core",
        name = "LobbyPortals Core",
        authors = {"maeven"},
        version = "0.1.0"
)
public class LobbyPortalsCore {

    private final Logger logger;
    private final MinecraftChannelIdentifier pluginChannel = MinecraftChannelIdentifier.create("lobbyportals","main");
    private final ProxyServer proxyServer;

    @Inject
    public LobbyPortalsCore(ProxyServer proxyServer, Logger logger) {
        this.proxyServer = proxyServer;
        this.logger = logger;

        logger.info("Loading LobbyPortals Core for Velocity...");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxyServer.getEventManager().register(this, new VelocityServerConnection(proxyServer, logger));
        proxyServer.getChannelRegistrar().register(pluginChannel);
        logger.info("Registered Plugin Messaging channel " + pluginChannel.getId());
    }
}


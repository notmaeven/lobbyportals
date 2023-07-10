package maeven.lobbyportalscore;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.Optional;

public class VelocityServerConnection {

    private final ProxyServer proxyServer;
    private final Logger logger;

    public VelocityServerConnection(ProxyServer pProxy, Logger pLogger)
    {
        proxyServer = pProxy;
        logger = pLogger;

    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        String channel = event.getIdentifier().getId();
        if (!channel.equals("lobbyportals:main")) {
            return; // ignoring messages from other channels
        }
        //get raw byte[] data from PluginMessageEvent
        byte[] data = event.getData();
        String message = new String(data);

        // Extract player name and target server name from message
        String[] parts = message.split(" ");
        if (parts.length != 2) {
            return; // Invalid message
        }
        String playerNameRaw = parts[0];
        //removing ghost characters ASCII 0 and 12
        String playerName = playerNameRaw.substring(2);
        String targetServer = parts[1];

        //turn playerName into Player
        Player player;
        Optional<Player> playerOptional = proxyServer.getPlayer(playerName);
        if (playerOptional.isEmpty()) {
            return; //no player found
        } else {
            player = playerOptional.get();
        }

        //turn targetServer into RegisteredServer
        Optional<RegisteredServer> serverOptional = proxyServer.getServer(targetServer);
        if (serverOptional.isEmpty()) {
            return;
        }
        RegisteredServer server = serverOptional.get();
        logger.info("Attempting ConnectionRequest: " + player.getUsername() + " -> " + targetServer);
        player.createConnectionRequest(server).connect();
    }
}

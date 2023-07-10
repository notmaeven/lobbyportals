package maeven.lobbyportals;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class PlayerMovements implements Listener {

    private LobbyPortals plugin;

    public PlayerMovements(LobbyPortals pl)
    {
        this.plugin = pl;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        //get player's location
        Location locPlayer = player.getLocation();
        //get any possible portal
        ArrayList<PortalLocation> ports = plugin.getPortalLocations();
        Double radius = plugin.getPortalRange();
        for (PortalLocation cur : ports) {
            //check every portal location
            Location locPortal = (new Location(player.getWorld(), cur.getXPos(), cur.getYPos(), cur.getZPos()));
            if (locPlayer.distance(locPortal) <= radius) {
                //player in range of PortalLocation cur
                //play fx and attempt a server change
                teleportPlayer(player, cur.getTarget());
                break;
            }
        }
    }

    public void teleportPlayer(Player player, String server)
    {
        Location spawn = plugin.getWorldSpawn(player.getWorld());
        System.out.println(player.getName() + ": requesting portal connection -> " + server);
        Location loc = player.getLocation();
        loc.setY(loc.getY() + 0.5);
        float red = 0.945f;
        float green = 0.261f;
        Color dustColor = Color.fromRGB((int)(red*255), (int)(green*255), 255);
        Color transitionColor = Color.fromRGB(0, 0, 0);
        Particle.DustTransition dustTransition = new Particle.DustTransition(dustColor, transitionColor, 2.0f);
        player.getWorld().spawnParticle(Particle.REDSTONE, loc, 1000, 0.2, 0.5, 0.2, 0, dustTransition);
        player.getWorld().playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 2.0f);
        player.teleport(spawn);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(player.getName() + " " + server);

        player.sendPluginMessage(plugin, "lobbyportals:main", out.toByteArray());

    }
}

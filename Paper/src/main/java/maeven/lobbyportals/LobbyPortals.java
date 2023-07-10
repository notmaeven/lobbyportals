package maeven.lobbyportals;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public final class LobbyPortals extends JavaPlugin implements Listener {
    private ArrayList<PortalLocation> portalLocations;
    FileConfiguration config = getConfig();
    private double portalRange;
    @Override
    public void onEnable()
    {
        // register events
        System.out.println("LobbyPortals v0.1.0 by maeven");
        Bukkit.getPluginManager().registerEvents(new PlayerMovements(this),this);

        // Check if config.yml exists, create it if it doesn't
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            System.out.println("Creating config.yml");
            saveDefaultConfig();
        }

        getServer().getPluginManager().registerEvents(this, this);

        //messaging channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "lobbyportals:main");
        System.out.println("registered OutgoingPluginChannel lobbyportals:main");

        if (!Objects.equals(config.getString("portals"), "server,0.0,0.0,0.0;")) {
            convertConfigToPortals(Objects.requireNonNull(config.getString("portals")));
            System.out.println("calculated portal positions");
        } else {
            System.out.println("didn't calculate portal positions: config not edited");
        }
        portalRange = config.getDouble("portalRange");

        //portal vfx
        BukkitTask portalVfx = new BukkitRunnable() {
            @Override
            public void run() {
                spawnPortalParticles();
            }
        }.runTaskTimer(this, 0L,1L);
        //portal sfx
        BukkitTask portalSfx = new BukkitRunnable() {
            @Override
            public void run() {
                makePortalSounds();
            }
        }.runTaskTimer(this, 0L, 40L);
    }
    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }
    public ArrayList<PortalLocation> getPortalLocations()
    {
        return portalLocations;
    }
    public double getPortalRange()
    {
        return portalRange;
    }
    public Location getWorldSpawn(World w)
    {
        String[] coords = Objects.requireNonNull(config.getString("spawn")).split(",");
        double x = Double.parseDouble(coords[0]); // x coordinate
        double y = Double.parseDouble(coords[1]); // y coordinate
        double z = Double.parseDouble(coords[2]); // z coordinate
        return new Location(w, x, y, z);
    }

    public void convertConfigToPortals(String in)
    {
        portalLocations = new ArrayList<>();
        String[] locations = in.split(";"); //input string into separate portal locations

        // Loop through each location and get values
        for (String val : locations)
        {
            String[] parts = val.split(","); // split location string into components

            String target = parts[0]; // targeted server's name
            double x = Double.parseDouble(parts[1]); // x coordinate
            double y = Double.parseDouble(parts[2]); // y coordinate
            double z = Double.parseDouble(parts[3]); // z coordinate

            // create new portal location from this batch of values
            portalLocations.add(new PortalLocation(target,x,y,z));
        }
    }
    public void spawnPortalParticles() {
        World world = Bukkit.getWorld("world");

        //dust particle color
        float red = 0.645f;
        float red2 = 0.945f;
        float green = 0.261f;
        float blue = 1.000f;
        Particle.DustOptions dustOptions1 = new Particle.DustOptions(Color.fromRGB((int)(red*255), (int)(green*255), (int)(blue*255)), 2);
        Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB((int)(red2*255), (int)(green*255), (int)(blue*255)), 2);

        ArrayList<PortalLocation> ports = getPortalLocations();
        for (PortalLocation cur : ports) {
            //check every portal location
            Location loc = (new Location(world, cur.getXPos(), cur.getYPos(), cur.getZPos()));
            Location locY = (new Location(world, cur.getXPos(), cur.getYPos()+1, cur.getZPos()));
            assert world != null;
            world.spawnParticle(Particle.REDSTONE, loc, 20, 0.1, 1.0, 0.1, 0, dustOptions1);
            world.spawnParticle(Particle.REDSTONE, loc, 20, 0.3, 1.4, 0.3, 0, dustOptions2);
            world.spawnParticle(Particle.PORTAL, locY, 60, 0.5, 1.0, 0.5, 1);
        }
    }
    public void makePortalSounds() {
        World world = Bukkit.getWorld("world");
        ArrayList<PortalLocation> ports = getPortalLocations();
        for (PortalLocation cur : ports) {
            //check every portal location
            Location loc = (new Location(world, cur.getXPos(), cur.getYPos(), cur.getZPos()));
            assert world != null;
            //warden heartbeat
            world.playSound(loc, Sound.ENTITY_WARDEN_NEARBY_CLOSE, 1.5f, 1.0f);
        }
    }


}

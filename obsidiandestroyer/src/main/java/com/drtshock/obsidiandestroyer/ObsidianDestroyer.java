package com.drtshock.obsidiandestroyer;

import com.drtshock.obsidiandestroyer.commands.ODCommand;
import com.drtshock.obsidiandestroyer.listeners.*;
import com.drtshock.obsidiandestroyer.managers.ChunkManager;
import com.drtshock.obsidiandestroyer.managers.ConfigManager;
import com.drtshock.obsidiandestroyer.managers.HookManager;
import com.drtshock.obsidiandestroyer.managers.MaterialManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ObsidianDestroyer extends JavaPlugin {

    public static Logger LOG;
    private static ObsidianDestroyer instance;

    public static ObsidianDestroyer getInstance() {
        return instance;
    }

    public static void debug(String debug) {
        if (ConfigManager.getInstance() == null || ConfigManager.getInstance().getDebug()) {
            LOG.info(debug);
        }
    }

    public static void vdebug(String debug) {
        if (ConfigManager.getInstance() == null || (ConfigManager.getInstance().getDebug() && ConfigManager.getInstance().getVerbose())) {
            LOG.info(debug);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        LOG = getLogger();

        // Initialize managers
        new ConfigManager(false);
        new HookManager();
        new MaterialManager();
        new ChunkManager();

        // Set command executor
        getCommand("od").setExecutor(new ODCommand());

        // Register Event listeners
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new EntityExplodeListener(), this);
        if (HookManager.getInstance().isHookedCannons()) {
            pm.registerEvents(new EntityImpactListener(), this);
        }
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new BlockListener(), this);
        try {
            Class.forName("org.bukkit.event.block.BlockExplodeEvent");
            pm.registerEvents(new SpigotListener(), this);
            LOG.log(Level.INFO, "Because of Spigot's laughable design decisions, we need to provide separate code");
            LOG.log(Level.INFO, "to hook into another one of their breaking changes they like to make. Despite Spigot's grotesqueness,");
            LOG.log(Level.INFO, "we provide compatibility because so many of our users run Spigot; so you're good to go.");
        } catch (ClassNotFoundException e) {
            // YAY
        }
    }

    @Override
    public void onDisable() {
        // Save persistent data
        if (ChunkManager.getInstance() != null) {
            ChunkManager.getInstance().save();
        }
    }

}
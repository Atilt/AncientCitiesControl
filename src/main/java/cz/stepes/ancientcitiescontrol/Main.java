package cz.stepes.ancientcitiescontrol;

import cz.stepes.ancientcitiescontrol.listener.WorldListener;
import cz.stepes.ancientcitiescontrol.manager.GenerationManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private GenerationManager generationManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        generationManager = new GenerationManager(this);
        generationManager.loadSettings();

        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);

        getLogger().info("Plugin has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin has been disabled!");
    }

    public GenerationManager getGenerationManager() {
        return generationManager;
    }
}
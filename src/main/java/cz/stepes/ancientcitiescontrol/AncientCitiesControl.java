package cz.stepes.ancientcitiescontrol;

import cz.stepes.ancientcitiescontrol.command.CommandReload;
import cz.stepes.ancientcitiescontrol.listener.WorldListener;
import cz.stepes.ancientcitiescontrol.manager.GenerationManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AncientCitiesControl extends JavaPlugin {

    private GenerationManager generationManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.generationManager = new GenerationManager(this);
        this.generationManager.loadSettings();

        getCommand("ancientcities").setExecutor(new CommandReload(this));

        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
    }

    public GenerationManager getGenerationManager() {
        return generationManager;
    }
}
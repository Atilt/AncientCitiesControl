package cz.stepes.ancientcitiescontrol.manager;

import cz.stepes.ancientcitiescontrol.Main;
import cz.stepes.ancientcitiescontrol.obj.GenerationSettings;
import org.bukkit.configuration.ConfigurationSection;

public class GenerationManager {

    private final Main plugin;
    private GenerationSettings generationSettings;

    public GenerationManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadSettings() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("generationSettings");
        if (section == null) return;

        int spacing = section.getInt("spacing");
        int separation = section.getInt("separation");
        int salt = section.getInt("salt");
        String spreadType = section.getString("spreadType");

        generationSettings = new GenerationSettings(spacing, separation, spreadType, salt);
    }

    public GenerationSettings getGenerationSettings() {
        return generationSettings;
    }
}

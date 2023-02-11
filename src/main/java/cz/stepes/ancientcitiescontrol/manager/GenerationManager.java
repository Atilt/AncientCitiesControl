package cz.stepes.ancientcitiescontrol.manager;

import cz.stepes.ancientcitiescontrol.AncientCitiesControl;
import cz.stepes.ancientcitiescontrol.obj.GenerationSettings;
import org.bukkit.configuration.ConfigurationSection;

public final class GenerationManager {

    private final AncientCitiesControl plugin;
    private GenerationSettings generationSettings;

    public GenerationManager(AncientCitiesControl plugin) {
        this.plugin = plugin;
    }

    public void loadSettings() {
        ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("generationSettings");
        if (section == null) {
            return;
        }

        int spacing = section.getInt("spacing");
        int separation = section.getInt("separation");
        int salt = section.getInt("salt");
        String spreadType = section.getString("spreadType");

        this.generationSettings = new GenerationSettings(spacing, separation, spreadType, salt);
    }

    public GenerationSettings getGenerationSettings() {
        return this.generationSettings;
    }
}
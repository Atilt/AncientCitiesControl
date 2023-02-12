package cz.stepes.ancientcitiescontrol.command;

import cz.stepes.ancientcitiescontrol.AncientCitiesControl;
import cz.stepes.ancientcitiescontrol.manager.GenerationManager;
import cz.stepes.ancientcitiescontrol.obj.GenerationSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class CommandReload implements CommandExecutor {

    private final AncientCitiesControl plugin;

    public CommandReload(AncientCitiesControl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("ancientcities.admin")) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }
        String option = strings[0];
        if (option.equalsIgnoreCase("reload")) {
            GenerationManager generationManager = this.plugin.getGenerationManager();

            this.plugin.reloadConfig();
            generationManager.loadSettings();

            commandSender.sendMessage(ChatColor.GREEN + "Reloaded configuration.");

            Logger logger = this.plugin.getLogger();
            GenerationSettings settings = generationManager.getGenerationSettings();

            logger.info("--------------------------------------------------------");
            logger.info( "New worlds will now be mapped with these settings:");
            logger.info( " -spacing: " + settings.spacing());
            logger.info( " -separation: " + settings.separation());
            logger.info( " -spreadType: " + settings.spreadType());
            logger.info( " -salt: " + settings.salt());
            logger.info("--------------------------------------------------------");
            return true;
        }
        commandSender.sendMessage(ChatColor.RED + "Usage: /ancientcities reload");
        return true;
    }
}
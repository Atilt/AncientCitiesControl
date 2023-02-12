package cz.stepes.ancientcitiescontrol.listener;

import cz.stepes.ancientcitiescontrol.AncientCitiesControl;
import cz.stepes.ancientcitiescontrol.ReflectionUtil;
import cz.stepes.ancientcitiescontrol.obj.GenerationSettings;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

public final class WorldListener implements Listener {

    private final AncientCitiesControl plugin;

    public WorldListener(AncientCitiesControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldLoad(WorldInitEvent event) {
        if (!(event.getWorld() instanceof CraftWorld craftWorld)) {
            return;
        }
        GenerationSettings settings = this.plugin.getGenerationManager().getGenerationSettings();

        if (settings == null) {
            this.plugin.getLogger().warning("No settings found. Corrupted file?");
            return;
        }
        MappedRegistry<StructureSet> mappedRegistry = (MappedRegistry<StructureSet>) craftWorld.getHandle().registryAccess().registryOrThrow(Registries.STRUCTURE_SET);
        for (Map.Entry<ResourceKey<StructureSet>, StructureSet> entry : mappedRegistry.entrySet()) {
            ResourceLocation resourceLocation = mappedRegistry.getKey(entry.getValue());
            String name = resourceLocation.getPath();
            if (!name.equals("nether_complexes")) {
                continue;
            }

            StructurePlacement structurePlacement = entry.getValue().placement();
            if (!(structurePlacement instanceof RandomSpreadStructurePlacement)) {
                continue;
            }

            RandomSpreadType randomSpreadType = RandomSpreadType.LINEAR;
            if (Objects.equals(settings.spreadType(), "triangular")) {
                randomSpreadType = RandomSpreadType.TRIANGULAR;
            }

            RandomSpreadStructurePlacement current = new RandomSpreadStructurePlacement(
                    settings.spacing(),
                    settings.separation(),
                    randomSpreadType,
                    settings.salt()
            );

            try {
                RandomSpreadStructurePlacement previous = (RandomSpreadStructurePlacement) ReflectionUtil.writeField(entry.getValue(), "d", current);
                logOutSpread("Mapped Ancient Cities in '" + event.getWorld().getName() + "' with settings:", previous, current);
            } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void logOutSpread(String title, RandomSpreadStructurePlacement current, RandomSpreadStructurePlacement replacement) {
        this.plugin.getLogger().info("-------------------------------------------------------");
        this.plugin.getLogger().info(title);
        this.plugin.getLogger().info(" -spacing: " + current.spacing() + (current.spacing() == replacement.spacing() ? " (No changes)" : " -> " + replacement.spacing()));
        this.plugin.getLogger().info(" -separation: " + current.separation() + (current.separation() == replacement.separation() ? " (No changes)" : " -> " + replacement.separation()));
        this.plugin.getLogger().info(" -spreadType: " + current.spreadType() + (current.spreadType() == replacement.spreadType() ? " (No changes)" : " -> " + replacement.spreadType()));
        this.plugin.getLogger().info(" -salt: " + current.salt + (current.salt == replacement.salt ? " (No changes)" : " -> " + replacement.salt));
        this.plugin.getLogger().info("-------------------------------------------------------");
    }
}
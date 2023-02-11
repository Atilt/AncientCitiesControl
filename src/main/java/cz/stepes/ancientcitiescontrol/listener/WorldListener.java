package cz.stepes.ancientcitiescontrol.listener;

import com.mojang.serialization.Lifecycle;
import cz.stepes.ancientcitiescontrol.AncientCitiesControl;
import cz.stepes.ancientcitiescontrol.obj.GenerationSettings;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

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

        MappedRegistry<StructureSet> mappedRegistry = (MappedRegistry<StructureSet>) craftWorld.getHandle().registryAccess().registryOrThrow(Registries.STRUCTURE_SET);

        Field frozen = null;
        try {
            frozen = MappedRegistry.class.getDeclaredField("l");
            frozen.setAccessible(true);
            frozen.setBoolean(mappedRegistry, false);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        } finally {
            if (frozen != null) {
                frozen.setAccessible(false);
            }
        }

        GenerationSettings settings = this.plugin.getGenerationManager().getGenerationSettings();

        if (settings == null) {
            plugin.getLogger().warning("No settings found. Corrupted file?");
            return;
        }

        for (Map.Entry<ResourceKey<StructureSet>, StructureSet> entry : mappedRegistry.entrySet()) {
            String name = mappedRegistry.getKey(entry.getValue()).getPath();
            if (!name.equals("ancient_cities")) {
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

            RandomSpreadStructurePlacement updatedConfig = new RandomSpreadStructurePlacement(
                    settings.spacing(),
                    settings.separation(),
                    randomSpreadType,
                    settings.salt()
            );
            mappedRegistry.register(entry.getKey(), new StructureSet(entry.getValue().structures(), updatedConfig), Lifecycle.stable());

            Logger logger = this.plugin.getLogger();

            logger.info("-------------------------------------------------------");
            logger.info( "Mapped Ancient Cities in '" + event.getWorld().getName() + "' with settings:");
            logger.info( " -spacing: " + settings.spacing());
            logger.info( " -separation: " + settings.separation());
            logger.info( " -spreadType: " + settings.spreadType());
            logger.info( " -salt: " + settings.salt());
            logger.info("-------------------------------------------------------");
        }
    }
}
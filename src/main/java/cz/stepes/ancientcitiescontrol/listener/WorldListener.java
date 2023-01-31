package cz.stepes.ancientcitiescontrol.listener;

import com.mojang.serialization.Lifecycle;
import cz.stepes.ancientcitiescontrol.Main;
import cz.stepes.ancientcitiescontrol.obj.GenerationSettings;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

public class WorldListener implements Listener {

    private final Main plugin;

    public WorldListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldLoad(WorldInitEvent event) {
        if (!(event.getWorld() instanceof CraftWorld)) return;

        GenerationSettings settings = plugin.getGenerationManager().getGenerationSettings();

        CraftWorld craftWorld = (CraftWorld) event.getWorld();
        ChunkGenerator chunkGenerator = craftWorld.getHandle().getChunkSource().getGenerator();
        MappedRegistry<StructureSet> mappedRegistry = (MappedRegistry<StructureSet>) chunkGenerator.structureSets;

        try {
            Field frozenField = MappedRegistry.class.getDeclaredField("ca");
            frozenField.setAccessible(true);
            frozenField.setBoolean(mappedRegistry, false);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        for (Map.Entry<ResourceKey<StructureSet>, StructureSet> entry : chunkGenerator.structureSets.entrySet()) {
            String name = chunkGenerator.structureSets.getKey(entry.getValue()).getPath();
            if (!name.equals("ancient_cities")) continue;

            StructurePlacement structurePlacement = entry.getValue().placement();
            if (!(structurePlacement instanceof RandomSpreadStructurePlacement)) continue;

            RandomSpreadType randomSpreadType = RandomSpreadType.LINEAR;
            if (Objects.equals(settings.getSpreadType(), "triangular")) randomSpreadType = RandomSpreadType.TRIANGULAR;

            RandomSpreadStructurePlacement updatedConfig = new RandomSpreadStructurePlacement(
                    settings.getSpacing(),
                    settings.getSeparation(),
                    randomSpreadType,
                    settings.getSalt()
            );
            mappedRegistry.registerOrOverride(OptionalInt.empty(), entry.getKey(), new StructureSet(entry.getValue().structures(), updatedConfig), Lifecycle.stable());
        }
    }
}
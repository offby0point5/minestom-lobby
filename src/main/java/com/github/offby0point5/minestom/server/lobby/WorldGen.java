package com.github.offby0point5.minestom.server.lobby;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class WorldGen implements ChunkGenerator {
    @Override
    public void generateChunkData(@NotNull ChunkBatch chunkBatch, int x, int z) {
        if (Math.abs(x) > 2 || Math.abs(z) > 2) return;
        for (int ix = 0; ix < Chunk.CHUNK_SIZE_X; ix++)
            for (int iz = 0; iz < Chunk.CHUNK_SIZE_X; iz++)
                chunkBatch.setBlock(ix, 0, iz, Block.GRASS_BLOCK);
    }

    @Override
    public void fillBiomes(@NotNull Biome[] biomes, int x, int z) {
        Arrays.fill(biomes, Biome.PLAINS);
    }

    @Override
    public @Nullable List<ChunkPopulator> getPopulators() {
        return null;
    }
}

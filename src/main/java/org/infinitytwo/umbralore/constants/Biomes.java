package org.infinitytwo.umbralore.constants;

import org.infinitytwo.umbralore.world.generation.Biome;

public enum Biomes {
    DESERT(
            new Biome(
                    "Desert",
                    "desert",
                    1,
                    0.007f,
                    0,1,2,
                    TerrainGenerationType.PLAINS
            ){}
    ),
    PLAINS(
            new Biome(
                    "Plains",
                    "plains",
                    1,
                    0.007f,
                    0,1,2,
                    TerrainGenerationType.PLAINS
            ){}
    ),
    MOUNTAINS(new Biome(
            "Mountains",
            "mountains",
            1.5f,
            0.05f,
            2,2,2,
            TerrainGenerationType.MOUNTAIN
    ){}),

    RIVER(new Biome(
            "River",
            "river",
            0.8f,
            0.5f,
            1,1,2,
            TerrainGenerationType.RIVER
    ) {})
    ;

    public final Biome biome;
    Biomes(Biome biome) {
        this.biome = biome;
    }
}

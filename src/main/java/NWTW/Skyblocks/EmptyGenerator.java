package NWTW.Skyblocks;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import java.util.Map;

public class EmptyGenerator extends Generator {
    private final String NAME = "emptyworld";

    private ChunkManager chunkManager;

    public EmptyGenerator(Map options) {}

    public int getId() {
        return 1;
    }

    public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
        this.chunkManager = chunkManager;
    }

    public void generateChunk(int chX, int chZ) {}

    public void populateChunk(int i, int i1) {}

    public Map<String, Object> getSettings() {
        return null;
    }

    public String getName() {
        return "emptyworld";
    }

    public Vector3 getSpawn() {
        return new Vector3(128.0D, 70.0D, 128.0D);
    }

    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }
}

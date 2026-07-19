package io.github.some_example_name.world;

import com.badlogic.gdx.audio.Music;

import java.util.Random;

public class WorldGenerator {
    private final long seedElevation;
    private final long seedVegetation;
    private final long seedLake;
    private ObjectLayer objectLayer;

    public WorldGenerator(long seed) {
        this.seedElevation  = scrambleSeed(seed);
        this.seedVegetation = scrambleSeed(seed + 1);
        this.seedLake       = scrambleSeed(seed + 2);
    }

    public ObjectLayer getObjectLayer() {
        return objectLayer;
    }

    private void placeTrees(World world) {
        int minDist = 3;
        boolean[][] occupied = new boolean[world.getWidth()][world.getHeight()];
        Random rng = new Random(scrambleSeed(seedVegetation + 13));

        for (int x = 1; x < world.getWidth()  - Tree.W; x++) {
            for (int y = 1; y < world.getHeight() - Tree.H; y++) {
                if (world.getTile(x, y).getType() != TileType.WOOD) continue;
                if (occupied[x][y]) continue;
                if (rng.nextFloat() > 0.05f) continue;

                boolean allWood = true;
                for (int dx = 0; dx < Tree.W && allWood; dx++)
                    for (int dy = 0; dy < Tree.H && allWood; dy++) {
                        Tile t = world.getTile(x + dx, y + dy);
                        if (t == null || t.getType() != TileType.WOOD) allWood = false;
                    }
                if (!allWood) continue;

                for (int dx = -minDist; dx <= minDist + Tree.W; dx++)
                    for (int dy = -minDist; dy <= minDist + Tree.H; dy++) {
                        int nx = x + dx, ny = y + dy;
                        if (nx >= 0 && nx < world.getWidth() && ny >= 0 && ny < world.getHeight())
                            occupied[nx][ny] = true;
                    }

                objectLayer.add(new Tree(x, y));
            }
        }
    }

    public World generate(int width, int height) {
        objectLayer = new ObjectLayer();
        World world = new World(width, height);

        // Pass 1: base terrain
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float elevation = getNoise(x, y, seedElevation, 0.05f, 4);
                float lake      = getNoise(x, y, seedLake,      0.08f, 2);

                TileType type;
                if (elevation < 0.35f) {
                    type = TileType.WATER;
                } else if (elevation < 0.36f) {
                    type = TileType.GRASS;
                } else {
                    type = (lake < 0.20f) ? TileType.WATER : TileType.STONE;
                }

                world.setTile(x, y, new Tile(type));
            }
        }

        // Pass 2: sand next to water
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileType current = world.getTile(x, y).getType();
                if ((current == TileType.GRASS || current == TileType.STONE)
                    && isNextToWater(world, x, y)) {
                    world.setTile(x, y, new Tile(TileType.SAND));
                }
            }
        }

        // Pass 3: wood on grass and stone only (never water, never sand)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileType current = world.getTile(x, y).getType();
                if (current == TileType.GRASS || current == TileType.STONE) {
                    float veg = getNoise(x, y, seedVegetation, 0.09f, 3);
                    if (veg > 0.30f) {
                        world.setTile(x, y, new Tile(TileType.WOOD));
                    }
                }
            }
        }
        placeTrees(world);
        return world;
    }

    private boolean isNextToWater(World world, int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Tile neighbor = world.getTile(x + dx, y + dy);
                if (neighbor != null && neighbor.getType() == TileType.WATER)
                    return true;
            }
        }
        return false;
    }

    private float getNoise(int x, int y, long s, float baseFreq, int octaves) {
        float total     = 0;
        float frequency = baseFreq;
        float amplitude = 1.0f;
        float maxValue  = 0;

        for (int i = 0; i < octaves; i++) {
            total    += interpolatedNoise(x * frequency, y * frequency, s) * amplitude;
            maxValue += amplitude;
            amplitude *= 0.5f;
            frequency *= 2.0f;
        }

        return total / maxValue;
    }

    private float interpolatedNoise(float x, float y, long s) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);
        float xf = x - xi;
        float yf = y - yi;

        float v00 = randomValue(xi,     yi,     s);
        float v10 = randomValue(xi + 1, yi,     s);
        float v01 = randomValue(xi,     yi + 1, s);
        float v11 = randomValue(xi + 1, yi + 1, s);

        float sx = smoothStep(xf);
        float sy = smoothStep(yf);

        return lerp(lerp(v00, v10, sx), lerp(v01, v11, sx), sy);
    }

    private float randomValue(int x, int y, long s) {
        return new Random(s ^ ((long) x * 374761393L) ^ ((long) y * 668265263L)).nextFloat();
    }

    private long scrambleSeed(long seed) {
        seed = (seed ^ (seed >>> 30)) * 0xbf58476d1ce4e5b9L;
        seed = (seed ^ (seed >>> 27)) * 0x94d049bb133111ebL;
        return seed ^ (seed >>> 31);
    }

    private float smoothStep(float t) { return t * t * (3 - 2 * t); }
    private float lerp(float a, float b, float t) { return a + (b - a) * t; }
}

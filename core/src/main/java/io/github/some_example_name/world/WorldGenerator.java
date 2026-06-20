package io.github.some_example_name.world;
import java.util.Random;

public class WorldGenerator {
    private final long seed;

    public WorldGenerator(long seed) {
        this.seed = seed;
    }

    public World generate(int width, int height) {
        World world = new World(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float value = getNoise(x, y);
                TileType type;

                if (value < 0.25f)      type = TileType.WATER;
                else if (value < 0.55f) type = TileType.GRASS;
                else if (value < 0.75f) type = TileType.STONE;
                else                    type = TileType.WOOD;

                world.setTile(x, y, new Tile(type));
            }
        }
        return world;
    }

    private float getNoise(int x, int y) {
        float total     = 0;
        float frequency = 0.05f;
        float amplitude = 1.0f;
        float maxValue  = 0;

        for (int i = 0; i < 4; i++) {
            total    += interpolatedNoise(x * frequency, y * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= 0.5f;
            frequency *= 2.0f;
        }

        return total / maxValue;
    }

    private float interpolatedNoise(float x, float y) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);
        float xf = x - xi;
        float yf = y - yi;

        float v00 = randomValue(xi,     yi);
        float v10 = randomValue(xi + 1, yi);
        float v01 = randomValue(xi,     yi + 1);
        float v11 = randomValue(xi + 1, yi + 1);

        float sx = smoothStep(xf);
        float sy = smoothStep(yf);

        return lerp(lerp(v00, v10, sx), lerp(v01, v11, sx), sy);
    }

    private float randomValue(int x, int y) {
        return new Random(seed ^ ((long) x * 374761393L) ^ ((long) y * 668265263L)).nextFloat();
    }

    private float smoothStep(float t) {
        return t * t * (3 - 2 * t);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

}

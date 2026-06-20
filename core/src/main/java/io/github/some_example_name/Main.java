package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.world.TileType;
import io.github.some_example_name.world.World;
import io.github.some_example_name.world.WorldGenerator;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private World world;

    private static final int TILE_SIZE   = 8;
    private static final int WORLD_WIDTH  = 100;
    private static final int WORLD_HEIGHT = 100;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        WorldGenerator generator = new WorldGenerator(12345L);
        world = generator.generate(WORLD_WIDTH, WORLD_HEIGHT);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                TileType type = world.getTile(x, y).getType();

                if      (type == TileType.WATER) shapeRenderer.setColor(Color.BLUE);
                else if (type == TileType.GRASS) shapeRenderer.setColor(Color.GREEN);
                else if (type == TileType.STONE) shapeRenderer.setColor(Color.GRAY);
                else if (type == TileType.WOOD)  shapeRenderer.setColor(Color.BROWN);

                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}


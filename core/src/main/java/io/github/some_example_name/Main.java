package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.entities.Player;
import io.github.some_example_name.world.Tile;
import io.github.some_example_name.world.TileType;
import io.github.some_example_name.world.World;
import io.github.some_example_name.world.WorldGenerator;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private World world;
    private Player player;

    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Texture[] allTextures;
    private float animTime;

    private static final int TILE_SIZE    = 32;
    private static final int WORLD_WIDTH  = 100;
    private static final int WORLD_HEIGHT = 100;
    private static final int SPRITE_SIZE  = 48;
    private static final int FRAMES       = 4;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        spriteBatch   = new SpriteBatch();
        camera        = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        WorldGenerator generator = new WorldGenerator(12345L);
        world = generator.generate(WORLD_WIDTH, WORLD_HEIGHT);

        loadAnimations();

        float[] spawn = findSpawn();
        player = new Player(spawn[0], spawn[1]);
    }

    private void loadAnimations() {
        String[] dirs = { "down", "up", "left", "right" };
        allTextures = new Texture[dirs.length * FRAMES];
        Animation<TextureRegion>[] anims = new Animation[4];

        for (int d = 0; d < dirs.length; d++) {
            TextureRegion[] frames = new TextureRegion[FRAMES];
            for (int f = 0; f < FRAMES; f++) {
                Texture tex = new Texture(
                    Gdx.files.internal("player_" + dirs[d] + "_" + f + ".png"));
                allTextures[d * FRAMES + f] = tex;
                frames[f] = new TextureRegion(tex);
            }
            anims[d] = new Animation<>(0.15f, frames);
            anims[d].setPlayMode(Animation.PlayMode.LOOP);
        }

        walkDown  = anims[0];
        walkUp    = anims[1];
        walkLeft  = anims[2];
        walkRight = anims[3];
    }

    private float[] findSpawn() {
        int cx = WORLD_WIDTH / 2;
        int cy = WORLD_HEIGHT / 2;
        for (int r = 0; r <= Math.max(WORLD_WIDTH, WORLD_HEIGHT); r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    int tx = cx + dx, ty = cy + dy;
                    Tile t = world.getTile(tx, ty);
                    if (t != null && t.getType().isWalkable())
                        return new float[]{ tx * TILE_SIZE + TILE_SIZE / 2f,
                            ty * TILE_SIZE + TILE_SIZE / 2f };
                }
            }
        }
        return new float[]{ cx * TILE_SIZE, cy * TILE_SIZE };
    }

    private Animation<TextureRegion> getCurrentAnimation() {
        switch (player.getDirection()) {
            case UP:    return walkUp;
            case LEFT:  return walkLeft;
            case RIGHT: return walkRight;
            default:    return walkDown;
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        player.update(delta, world, TILE_SIZE);

        boolean isMoving = player.isMoving();
        if (isMoving) {
            if (!wasMoving) animTime = 0.15f;
            animTime += delta;
        } else {
            animTime = 0;
        }
        wasMoving = isMoving;

        float halfW = 320, halfH = 240;
        float camX = Math.max(halfW, Math.min(player.getX(), WORLD_WIDTH  * TILE_SIZE - halfW));
        float camY = Math.max(halfH, Math.min(player.getY(), WORLD_HEIGHT * TILE_SIZE - halfH));
        camera.position.set(camX, camY, 0);
        camera.update();

        ScreenUtils.clear(0, 0, 0, 1);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                TileType type = world.getTile(x, y).getType();
                if      (type == TileType.WATER) shapeRenderer.setColor(Color.BLUE);
                else if (type == TileType.GRASS) shapeRenderer.setColor(Color.GREEN);
                else if (type == TileType.STONE) shapeRenderer.setColor(Color.GRAY);
                else if (type == TileType.WOOD)  shapeRenderer.setColor(Color.BROWN);
                else if (type == TileType.SAND)  shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        shapeRenderer.end();

        TextureRegion frame = getCurrentAnimation().getKeyFrame(animTime);
        float drawX = player.getX() - SPRITE_SIZE / 2f;
        float drawY = player.getY() - SPRITE_SIZE / 2f;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(frame, drawX, drawY, SPRITE_SIZE, SPRITE_SIZE);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        for (Texture t : allTextures) t.dispose();
    }

    private boolean wasMoving = false;
}

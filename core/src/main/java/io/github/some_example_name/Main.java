package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.entities.Player;
import io.github.some_example_name.world.*;
import java.util.ArrayList;

public class Main extends ApplicationAdapter {
    private SpriteBatch        spriteBatch;
    private OrthographicCamera camera;
    private World              world;
    private ObjectLayer        objects;
    private Player             player;
    private TileRenderer       tileRenderer;

    private Texture grassSheet, waterSheet, stoneSheet, sandSheet, woodSheet;
    private Texture treeTexture;
    private TextureRegion treeCanopy, treeTrunk;

    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Texture[] allTextures;
    private float   animTime;
    private boolean wasMoving = false;

    private static final int TILE_SIZE     = 32;
    private static final int WORLD_WIDTH   = 100;
    private static final int WORLD_HEIGHT  = 100;
    private static final int SPRITE_SIZE   = 48;
    private static final int FRAMES        = 4;
    private static final int TREE_W        = 64;
    private static final int TREE_TRUNK_H  = 22;
    private static final int TREE_CANOPY_H = 55;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        camera      = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        WorldGenerator generator = new WorldGenerator(12345L);
        world   = generator.generate(WORLD_WIDTH, WORLD_HEIGHT);
        objects = generator.getObjectLayer();

        grassSheet   = new Texture(Gdx.files.internal("grass_tile.png"));
        waterSheet   = new Texture(Gdx.files.internal("water_tile.png"));
        stoneSheet   = new Texture(Gdx.files.internal("stone_tile.png"));
        sandSheet    = new Texture(Gdx.files.internal("sand_tile.png"));
        woodSheet = new Texture(Gdx.files.internal("grass_tile.png"));
        tileRenderer = new TileRenderer(grassSheet, waterSheet, stoneSheet, sandSheet, woodSheet);

        treeTexture = new Texture(Gdx.files.internal("tree_1.png"));
        treeTrunk   = new TextureRegion(treeTexture, 0, TREE_CANOPY_H, TREE_W, TREE_TRUNK_H);
        treeCanopy  = new TextureRegion(treeTexture, 0, 0,             TREE_W, TREE_CANOPY_H);

        loadAnimations();

        float[] spawn = findSpawn();
        player = new Player(spawn[0], spawn[1]);
    }

    private void loadAnimations() {
        String[] dirs   = { "down", "up", "left", "right" };
        allTextures     = new Texture[dirs.length * FRAMES];
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
        int cx = WORLD_WIDTH / 2, cy = WORLD_HEIGHT / 2;
        for (int r = 0; r <= Math.max(WORLD_WIDTH, WORLD_HEIGHT); r++)
            for (int dx = -r; dx <= r; dx++)
                for (int dy = -r; dy <= r; dy++) {
                    int tx = cx + dx, ty = cy + dy;
                    Tile t = world.getTile(tx, ty);
                    if (t == null || !t.getType().isWalkable()) continue;
                    float px = tx * TILE_SIZE + TILE_SIZE / 2f;
                    float py = ty * TILE_SIZE + TILE_SIZE / 2f;
                    if (!objects.isSolidAt(px, py, TILE_SIZE))
                        return new float[]{ px, py };
                }
        return new float[]{ cx * TILE_SIZE, cy * TILE_SIZE };
    }

    private Animation<TextureRegion> getCurrentAnim() {
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
        player.update(delta, world, objects, TILE_SIZE);

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
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        // 1. Ground tiles
        for (int x = 0; x < world.getWidth(); x++)
            for (int y = 0; y < world.getHeight(); y++)
                tileRenderer.draw(spriteBatch, world.getTile(x, y).getType(),
                    x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE);

        // 2. Y-sorted layer: trunks and player
        // Higher Y = further north = drawn first = appears behind
        TextureRegion frame    = getCurrentAnim().getKeyFrame(animTime);
        float playerDrawX      = player.getX() - SPRITE_SIZE / 2f;
        float playerDrawY      = player.getY() - SPRITE_SIZE / 2f;

        ArrayList<float[]> sorted = new ArrayList<>();
        // {sortY, type, drawX, drawY}  type: 0 = trunk, 1 = player
        sorted.add(new float[]{ playerDrawY, 1, playerDrawX, playerDrawY });

        for (WorldObject obj : objects.getAll()) {
            if (!obj.isDestroyed())
                sorted.add(new float[]{
                    obj.getTileY() * TILE_SIZE, 0,
                    obj.getTileX() * TILE_SIZE,
                    obj.getTileY() * TILE_SIZE });
        }

        sorted.sort((a, b) -> Float.compare(b[0], a[0]));

        for (float[] op : sorted) {
            if (op[1] == 1)
                spriteBatch.draw(frame, op[2], op[3], SPRITE_SIZE, SPRITE_SIZE);
            else
                spriteBatch.draw(treeTrunk, op[2], op[3], TREE_W, TREE_TRUNK_H);
        }

        // 3. Canopies always on top
        for (WorldObject obj : objects.getAll()) {
            if (obj.isDestroyed()) continue;
            spriteBatch.draw(treeCanopy,
                obj.getTileX() * TILE_SIZE,
                obj.getTileY() * TILE_SIZE + TREE_TRUNK_H,
                TREE_W, TREE_CANOPY_H);
        }

        spriteBatch.end();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        grassSheet.dispose(); waterSheet.dispose();
        stoneSheet.dispose(); sandSheet.dispose();
        woodSheet.dispose();
        treeTexture.dispose();
        tileRenderer.dispose();
        for (Texture t : allTextures) t.dispose();
    }
}

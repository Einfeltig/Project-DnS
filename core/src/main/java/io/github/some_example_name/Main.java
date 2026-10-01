package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.entities.Player;

public class Main extends ApplicationAdapter {
    private SpriteBatch                spriteBatch;
    private OrthographicCamera         camera;
    private TiledMap                   tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMapTileLayer          collisionLayer;
    private Player                     player;

    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Texture[] allTextures;
    private float     animTime;
    private boolean   wasMoving = false;

    private static final int   TILE_SIZE  = 32;
    private static final float UNIT_SCALE = TILE_SIZE / 16f;
    private static final int   SPRITE_W   = 32;
    private static final int   SPRITE_H   = 64;
    private static final int   FRAMES     = 4;

    // Render layer indices
    private static final int[] GROUND_LAYER = { 0 };
    private static final int[] OBJECT_LAYER = { 1 };

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        camera      = new OrthographicCamera();
        camera.setToOrtho(false, 480, 270);

        tiledMap    = new TmxMapLoader().load("starting_room.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, UNIT_SCALE);

        collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Tile Layer 2");

        int mapPixelW = collisionLayer.getWidth()  * TILE_SIZE;
        int mapPixelH = collisionLayer.getHeight() * TILE_SIZE;
        player = new Player(mapPixelW / 2f, mapPixelH / 2f);

        loadAnimations();
    }

    private void loadAnimations() {
        String[] dirs = { "down", "up", "left", "right" };
        allTextures   = new Texture[dirs.length * FRAMES];
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
        player.update(delta, collisionLayer, TILE_SIZE);

        boolean isMoving = player.isMoving();
        if (isMoving) {
            if (!wasMoving) animTime = 0.15f;
            animTime += delta;
        } else {
            animTime = 0;
        }
        wasMoving = isMoving;

        int mapPixelW = collisionLayer.getWidth()  * TILE_SIZE;
        int mapPixelH = collisionLayer.getHeight() * TILE_SIZE;
        float halfW   = 240, halfH = 135;
        float camX = Math.max(halfW, Math.min(player.getX(), mapPixelW - halfW));
        float camY = Math.max(halfH, Math.min(player.getY(), mapPixelH - halfH));
        camera.position.set(camX, camY, 0);
        camera.update();

        ScreenUtils.clear(0, 0, 0, 1);

        mapRenderer.setView(camera);

        // 1. Draw ground layer
        mapRenderer.render(GROUND_LAYER);

        // 2. Draw player on top of ground
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        TextureRegion frame = getCurrentAnim().getKeyFrame(animTime);
        spriteBatch.draw(frame,
            player.getX() - SPRITE_W / 2f,
            player.getY() - SPRITE_H / 2f,
            SPRITE_W, SPRITE_H);
        spriteBatch.end();

        // 3. Draw object layer (trees, fence) in front of player
        mapRenderer.render(OBJECT_LAYER);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 480, 270);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        tiledMap.dispose();
        mapRenderer.dispose();
        for (Texture t : allTextures) t.dispose();
    }
}

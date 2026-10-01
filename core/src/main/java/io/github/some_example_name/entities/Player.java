package io.github.some_example_name.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Player {

    public enum Direction { DOWN, UP, LEFT, RIGHT }

    private float x, y;
    private static final float SPEED    = 150f;
    private static final int   SPRITE_W = 32;
    private static final int   SPRITE_H = 64;
    private boolean   moving;
    private Direction direction = Direction.DOWN;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta, TiledMapTileLayer collisionLayer, int tileSize) {
        float dx = 0, dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        if (dx != 0 && dy != 0) {
            dx *= 0.7071f;
            dy *= 0.7071f;
        }

        moving = dx != 0 || dy != 0;

        if (Math.abs(dx) >= Math.abs(dy)) {
            if (dx < 0) direction = Direction.LEFT;
            else if (dx > 0) direction = Direction.RIGHT;
        } else {
            if (dy > 0) direction = Direction.UP;
            else if (dy < 0) direction = Direction.DOWN;
        }

        float newX = x + dx * SPEED * delta;
        float newY = y + dy * SPEED * delta;

        if (canMoveTo(newX, y,   collisionLayer, tileSize)) x = newX;
        if (canMoveTo(x,   newY, collisionLayer, tileSize)) y = newY;
    }

    private boolean canMoveTo(float px, float py,
                              TiledMapTileLayer layer, int tileSize) {
        // Check three points along the feet — left, center, right
        float footY  = py - SPRITE_H / 2f + 4;
        float left   = px - SPRITE_W / 2f + 4;
        float right  = px + SPRITE_W / 2f - 4;

        return isClear(left,  footY, layer, tileSize)
            && isClear(px,    footY, layer, tileSize)
            && isClear(right, footY, layer, tileSize);
    }

    private boolean isClear(float px, float py,
                            TiledMapTileLayer layer, int tileSize) {
        int tx = (int)(px / tileSize);
        int ty = (int)(py / tileSize);
        if (tx < 0 || ty < 0) return false;
        if (tx >= layer.getWidth() || ty >= layer.getHeight()) return false;
        return layer.getCell(tx, ty) == null;
    }

    public float getX()             { return x; }
    public float getY()             { return y; }
    public boolean isMoving()       { return moving; }
    public Direction getDirection() { return direction; }
}

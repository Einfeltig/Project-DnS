package io.github.some_example_name.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.some_example_name.world.ObjectLayer;
import io.github.some_example_name.world.Tile;
import io.github.some_example_name.world.World;

public class Player {

    public enum Direction { DOWN, UP, LEFT, RIGHT }

    private float x, y;
    private static final float SPEED = 100f;
    private boolean moving;
    private Direction direction = Direction.DOWN;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta, World world, ObjectLayer objects, int tileSize) {
        float dx = 0, dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        if (dx != 0 && dy != 0) { dx *= 0.7071f; dy *= 0.7071f; }

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

        if (canMoveTo(newX, y,  world, objects, tileSize)) x = newX;
        if (canMoveTo(x,  newY, world, objects, tileSize)) y = newY;
    }

    private boolean canMoveTo(float px, float py, World world, ObjectLayer objects, int tileSize) {
        int tileX = (int)(px / tileSize);
        int tileY = (int)(py / tileSize);
        Tile tile = world.getTile(tileX, tileY);
        if (tile == null || !tile.getType().isWalkable()) return false;
        if (objects.isSolidAt(px, py, tileSize)) return false;
        return true;
    }

    public float getX()            { return x; }
    public float getY()            { return y; }
    public boolean isMoving()      { return moving; }
    public Direction getDirection(){ return direction; }
}

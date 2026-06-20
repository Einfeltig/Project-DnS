package io.github.some_example_name.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.some_example_name.world.Tile;
import io.github.some_example_name.world.World;

public class Player {
    private float x, y;
    private static final float SPEED = 100f;
    private boolean moving;
    private boolean facingLeft;


    public Player(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta, World world, int tileSize) {
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
        if (dx < 0) facingLeft = true;
        if (dx > 0) facingLeft = false;

        float newX = x + dx * SPEED * delta;
        float newY = y + dy * SPEED * delta;

        if (isWalkable(newX, y, world, tileSize)) x = newX;
        if (isWalkable(x, newY, world, tileSize)) y = newY;
    }

    private boolean isWalkable(float px, float py, World world, int tileSize) {
        int tileX = (int)(px / tileSize);
        int tileY = (int)(py / tileSize);
        Tile tile = world.getTile(tileX, tileY);
        return tile != null && tile.getType().isWalkable();
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public boolean isMoving()  { return moving; }
    public boolean isFacingLeft() { return facingLeft; }
}

package io.github.some_example_name.world;

public class Tree extends WorldObject {
    public static final int W = 2;
    public static final int H = 3;

    private int health;

    public Tree(int tileX, int tileY) {
        super(tileX, tileY, W, H, 1);
        this.health = 5;
    }

    public void damage(int amount) {
        health -= amount;
        if (health <= 0) destroy();
    }

    public int getHealth() { return health; }
}

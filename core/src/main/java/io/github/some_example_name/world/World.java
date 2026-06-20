package io.github.some_example_name.world;

public class World {
    private final int width;
    private final int height;
    private final Tile[][] tiles;

    public World (int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
    }
    public Tile getTile(int x, int y){
        if (x<0 || x>= width || y<0 || y>= height) return null;
        return tiles[x][y];
    }
    public void setTile(int x, int y, Tile tile){
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        tiles[x][y] = tile;
    }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

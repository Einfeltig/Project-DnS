package io.github.some_example_name.world;

public class Tile {
    private TileType type;

    public Tile(TileType type){
        this.type = type;
    }
    public TileType getType() {
        return this.type;
    }

    public void setType(TileType type){
        this.type = type;
    }
}

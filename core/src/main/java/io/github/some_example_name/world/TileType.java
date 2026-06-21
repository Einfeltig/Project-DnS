package io.github.some_example_name.world;

public enum TileType {
    GRASS(true),
    WOOD(true),
    SAND(true),
    STONE(true),
    WATER(false);

    private final boolean walkable;

    TileType(boolean walkable)
    {
     this.walkable = walkable;
    }

    public boolean isWalkable(){
        return walkable;
    }
}

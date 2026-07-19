package io.github.some_example_name.world;

public class WorldObject {
    private final int tileX, tileY;
    private final int widthTiles, heightTiles;
    private final int solidHeightTiles;
    private boolean destroyed;

    public WorldObject(int tileX, int tileY, int widthTiles, int heightTiles, int solidHeightTiles) {
        this.tileX           = tileX;
        this.tileY           = tileY;
        this.widthTiles      = widthTiles;
        this.heightTiles     = heightTiles;
        this.solidHeightTiles = solidHeightTiles;
        this.destroyed       = false;
    }

    public int getTileX()           { return tileX; }
    public int getTileY()           { return tileY; }
    public int getWidthTiles()      { return widthTiles; }
    public int getHeightTiles()     { return heightTiles; }
    public int getSolidHeightTiles(){ return solidHeightTiles; }
    public boolean isDestroyed()    { return destroyed; }
    public void destroy()           { destroyed = true; }
}

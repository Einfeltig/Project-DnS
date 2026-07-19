package io.github.some_example_name.world;

import java.util.ArrayList;
import java.util.List;

public class ObjectLayer {
    private final List<WorldObject> objects = new ArrayList<>();

    public void add(WorldObject obj) {
        objects.add(obj);
    }

    public List<WorldObject> getAll() {
        return objects;
    }

    public boolean isSolidAt(float px, float py, int tileSize) {
        int tx = (int)(px / tileSize);
        int ty = (int)(py / tileSize);
        for (WorldObject obj : objects) {
            if (obj.isDestroyed()) continue;
            if (tx >= obj.getTileX()
                && tx <  obj.getTileX() + obj.getWidthTiles()
                && ty >= obj.getTileY()
                && ty <  obj.getTileY() + obj.getSolidHeightTiles()) {
                return true;
            }
        }
        return false;
    }
}

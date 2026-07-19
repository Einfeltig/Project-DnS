package io.github.some_example_name.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

public class TileRenderer {
    private static final int SHEET_TILE = 16;

    private TextureRegion grass, water, stone, sand, wood;
    private final List<Texture> fallbacks = new ArrayList<>();

    public TileRenderer(Texture grassSheet, Texture waterSheet,
                        Texture stoneSheet, Texture sandSheet, Texture woodSheet) {
        grass = new TextureRegion(grassSheet, 0,          0, SHEET_TILE, SHEET_TILE);
        water = new TextureRegion(waterSheet, 0,          0, SHEET_TILE, SHEET_TILE);
        stone = new TextureRegion(stoneSheet, 0,          0, SHEET_TILE, SHEET_TILE);
        sand  = new TextureRegion(sandSheet,  0,          0, SHEET_TILE, SHEET_TILE);
        wood  = new TextureRegion(woodSheet,  0, 0, SHEET_TILE, SHEET_TILE);
    }

    public void setTile(TileType type, Texture sheet, int col, int row) {
        TextureRegion r = new TextureRegion(
            sheet, col * SHEET_TILE, row * SHEET_TILE, SHEET_TILE, SHEET_TILE);
        switch (type) {
            case GRASS: grass = r; break;
            case WATER: water = r; break;
            case STONE: stone = r; break;
            case SAND:  sand  = r; break;
            case WOOD:  wood  = r; break;
        }
    }

    public void draw(SpriteBatch batch, TileType type, float x, float y, float size) {
        TextureRegion r;
        switch (type) {
            case WATER: r = water; break;
            case STONE: r = stone; break;
            case SAND:  r = sand;  break;
            case WOOD:  r = wood;  break;
            default:    r = grass; break;
        }
        batch.draw(r, x, y, size, size);
    }

    private TextureRegion colorFallback(Color color) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(color);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        fallbacks.add(t);
        return new TextureRegion(t);
    }

    public void dispose() {
        for (Texture t : fallbacks) t.dispose();
    }
}

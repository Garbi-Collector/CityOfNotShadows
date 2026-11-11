package com.garbarabi.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.garbarabi.GameConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Marco armado con los tiles de una capa de un mapa Tiled. Los objetos de otra capa marcan las áreas
 * donde va el contenido. Medidas en píxeles de interfaz, con Y hacia arriba.
 */
public class TiledFrame {
    private final TiledMap map;
    private final List<Tile> tiles = new ArrayList<>();
    private final float tileSize;   // lado de un tile en píxeles de interfaz
    private final float width;
    private final float height;
    private final float originX;    // esquina inferior izquierda del marco, en píxeles del mapa
    private final float originY;

    public TiledFrame(TiledMap map, String layerName) {
        this.map = map;
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        // El mapa está en píxeles del mundo (16 por tile); la interfaz usa UI_SCALE de ellos por píxel de sprite.
        float mapTile = layer.getTileWidth();
        this.tileSize = mapTile / GameConfig.UI_SCALE;

        // Rectángulo que ocupan los tiles no vacíos, para que el marco quede ajustado al dibujo.
        int minCol = layer.getWidth();
        int minRow = layer.getHeight();
        int maxCol = -1;
        int maxRow = -1;
        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                if (layer.getCell(col, row) == null) continue;
                minCol = Math.min(minCol, col);
                minRow = Math.min(minRow, row);
                maxCol = Math.max(maxCol, col);
                maxRow = Math.max(maxRow, row);
            }
        }
        this.width = (maxCol - minCol + 1) * tileSize;
        this.height = (maxRow - minRow + 1) * tileSize;
        this.originX = minCol * mapTile;
        this.originY = minRow * mapTile;

        // Las celdas de la capa ya están en Y hacia arriba (libGDX invierte la fila de Tiled al cargar).
        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null) continue;
                tiles.add(new Tile(cell.getTile().getTextureRegion(), (col - minCol) * tileSize, (row - minRow) * tileSize));
            }
        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    /** Dibuja el marco con su esquina inferior izquierda en (left, bottom). */
    public void draw(Batch batch, float left, float bottom) {
        for (Tile tile : tiles) {
            batch.draw(tile.region, left + tile.dx, bottom + tile.dy, tileSize, tileSize);
        }
    }

    /** Área de un objeto del mapa, en píxeles de interfaz, con el marco colocado en (left, bottom). */
    public Rectangle area(String objectLayer, String objectName, float left, float bottom) {
        Rectangle rect = ((RectangleMapObject) map.getLayers().get(objectLayer).getObjects().get(objectName)).getRectangle();
        return new Rectangle(
                left + (rect.x - originX) / GameConfig.UI_SCALE,
                bottom + (rect.y - originY) / GameConfig.UI_SCALE,
                rect.width / GameConfig.UI_SCALE,
                rect.height / GameConfig.UI_SCALE);
    }

    /** Borde superior de un área medido desde arriba de la pantalla, como lo usan PixelFont y TextBox. */
    public static float top(Rectangle area) {
        return GameConfig.UI_HEIGHT - (area.y + area.height);
    }

    /** Caja de texto que ocupa un área, con tantas líneas como quepan. */
    public static TextBox textBox(BitmapFont font, Rectangle area) {
        return new TextBox(font, area.x, top(area), area.width, (int) (area.height / PixelFont.LINE_HEIGHT));
    }

    /** Un tile del marco: su textura y su esquina inferior izquierda, medida desde la del marco. */
    private static final class Tile {
        final TextureRegion region;
        final float dx;
        final float dy;

        Tile(TextureRegion region, float dx, float dy) {
            this.region = region;
            this.dx = dx;
            this.dy = dy;
        }
    }
}

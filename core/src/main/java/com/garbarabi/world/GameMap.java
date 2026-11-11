package com.garbarabi.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/** Envuelve el TiledMap (propiedad del AssetManager) y su renderer. 1 unidad de mundo = 1 tile. */
public class GameMap {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
    private final int tileSizePx;  // tamaño de un tile en píxeles (16 en este mapa)
    private final int widthTiles;
    private final int heightTiles;

    /** Lee tamaño y tiles del mapa y prepara el renderer sobre el batch compartido. */
    public GameMap(TiledMap map, Batch batch) {
        this.map = map;
        this.widthTiles = map.getProperties().get("width", Integer.class);
        this.heightTiles = map.getProperties().get("height", Integer.class);
        this.tileSizePx = map.getProperties().get("tilewidth", Integer.class);
        this.renderer = new OrthogonalTiledMapRenderer(map, 1f / tileSizePx, batch);
    }

    /** Dibuja el mapa visible desde la cámara indicada. */
    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public TiledMap getTiledMap() { return map; }
    public int getTileSizePx() { return tileSizePx; }
    public int getWidthTiles() { return widthTiles; }
    public int getHeightTiles() { return heightTiles; }

    /** No libera el TiledMap: eso lo hace Assets.close(). */
    public void dispose() {
        renderer.dispose();
    }
}

package com.garbarabi;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.garbarabi.entities.Direction;

/** Carga y entrega los recursos del juego (mapas y texturas) a través de un AssetManager. */
public class Assets implements AutoCloseable {
    // Rutas relativas a assets/. Cambiar aquí si se mueve un archivo.
    public static final String MAP_TEST = "tiled/mapa_prueba.tmx";
    public static final String UI_BOX = "tiled/UI_Text_Box.tmx";

    // Fotogramas de cada dirección del jugador: quieto y los dos pasos de caminata.
    private static final String[] PLAYER_FRAMES = {"stand", "walk-1", "walk-2"};

    private final AssetManager manager = new AssetManager();

    public Assets() {
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    /** Pone en cola la carga de todos los recursos. Hay que llamar a finishLoading() después. */
    public void loadAll() {
        TmxMapLoader.Parameters mapParams = new TmxMapLoader.Parameters();
        mapParams.textureMinFilter = Texture.TextureFilter.Nearest;  // pixel art sin difuminar
        mapParams.textureMagFilter = Texture.TextureFilter.Nearest;
        manager.load(MAP_TEST, TiledMap.class, mapParams);

        TextureLoader.TextureParameter textureParams = new TextureLoader.TextureParameter();
        textureParams.minFilter = Texture.TextureFilter.Nearest;
        textureParams.magFilter = Texture.TextureFilter.Nearest;
        for (Direction direction : Direction.values()) {
            for (String frame : PLAYER_FRAMES) {
                manager.load(playerFrame(direction, frame), Texture.class, textureParams);
            }
        }
        manager.load(UI_BOX, TiledMap.class, mapParams);
    }

    /** Bloquea hasta que todo lo puesto en cola esté cargado. Carga corta, así que no hace falta pantalla de carga. */
    public void finishLoading() {
        manager.finishLoading();
    }

    public TiledMap getMap(String path) {
        return manager.get(path, TiledMap.class);
    }

    public Texture getTexture(String path) {
        return manager.get(path, Texture.class);
    }

    /** Ruta de un fotograma del jugador, p. ej. sprites/Player/player-top-walk-1.png */
    public static String playerFrame(Direction direction, String frame) {
        return "sprites/Player/player-" + direction.getFileName() + "-" + frame + ".png";
    }

    @Override
    public void close() {
        manager.dispose();
    }
}

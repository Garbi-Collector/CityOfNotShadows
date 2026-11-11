package com.garbarabi;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.garbarabi.i18n.Localization;
import com.garbarabi.screens.GameScreen;
import com.garbarabi.ui.PixelFont;

/** Punto de entrada común de la aplicación: crea los recursos compartidos y maneja la pantalla completa. */
public class CityOfNotShadowsGame extends Game {
    public SpriteBatch batch;   // único batch de dibujo, compartido por todas las pantallas
    public Assets assets;       // mapas y texturas
    public BitmapFont font;     // fuente pixelada de la interfaz
    public Localization text;   // textos en el idioma elegido

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();
        assets.loadAll();
        assets.finishLoading();   // carga bloqueante; mapa pequeño. Pantalla de carga más adelante.
        font = PixelFont.create();
        text = new Localization(GameConfig.LANGUAGE);
        setScreen(new GameScreen(this));
    }

    /** F4 se revisa aquí, fuera de las pantallas, para que funcione en cualquier estado del juego. */
    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
            toggleFullscreen();
        }
        super.render();
    }

    /** Alterna entre pantalla completa y ventana de tamaño fijo. */
    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    @Override
    public void dispose() {
        if (screen != null) screen.dispose();
        batch.dispose();
        font.dispose();
        assets.close();
    }
}

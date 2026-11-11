package com.garbarabi;

/** Constantes globales de configuración del juego. */
public final class GameConfig {
    // Resolución virtual del mundo: el mapa se ve siempre en 320x240 px, sin importar la ventana.
    public static final float VIRTUAL_WIDTH = 320f;
    public static final float VIRTUAL_HEIGHT = 240f;

    // Interfaz (menú y diálogo): cada píxel de sprite ocupa UI_SCALE píxeles virtuales.
    // Con 4, el área de interfaz mide 80x60 "píxeles de sprite".
    public static final int UI_SCALE = 4;
    public static final float UI_WIDTH = VIRTUAL_WIDTH / UI_SCALE;
    public static final float UI_HEIGHT = VIRTUAL_HEIGHT / UI_SCALE;

    // Tamaño de la ventana cuando no está en pantalla completa (F4 vuelve a este tamaño).
    public static final int WINDOW_WIDTH = 640;
    public static final int WINDOW_HEIGHT = 480;

    // Idioma de los textos: se carga assets/i18n/<LANGUAGE>.json ("es" o "en").
    public static final String LANGUAGE = "es";

    private GameConfig() {}
}

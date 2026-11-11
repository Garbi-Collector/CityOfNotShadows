package com.garbarabi.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.garbarabi.GameConfig;

/** Fuente pixelada (Pixolletta 8px) generada a su tamaño nativo, que coincide con la rejilla de píxeles. */
public final class PixelFont {
    /** Separación entre líneas en píxeles del sprite. */
    public static final float LINE_HEIGHT = 2.5f;

    private static final String FONT_PATH = "fonts/Pixolletta8px.ttf";
    private static final int SIZE = 8;  // tamaño nativo de la fuente: es el único que se ve nítido
    // Caracteres que se generan en la textura: ASCII básico más las letras acentuadas y ñ del español.
    private static final String CHARACTERS = FreeTypeFontGenerator.DEFAULT_CHARS + "ÁÉÍÓÚáéíóúÑñÜü¿¡";

    private PixelFont() {}

    /**
     * Dibuja una línea con su borde superior en top, medido desde arriba como los sprites.
     * BitmapFont toma la y como el borde superior del texto, pero usa el eje Y hacia arriba, así que se convierte aquí.
     */
    public static void drawAt(BitmapFont font, Batch batch, String text, float x, float top) {
        font.draw(batch, text, x, GameConfig.UI_HEIGHT - top);
    }

    /** Genera la fuente desde el .ttf. Hay que llamarlo una sola vez y liberarla al cerrar el juego. */
    public static BitmapFont create() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = SIZE;
        parameter.characters = CHARACTERS;
        parameter.minFilter = Texture.TextureFilter.Nearest;  // sin suavizado, como el resto del arte
        parameter.magFilter = Texture.TextureFilter.Nearest;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();  // el generador ya no se necesita; la fuente generada sigue viva
        // Cada píxel de la fuente pasa a ser un píxel de la pantalla virtual (1/UI_SCALE de píxel de interfaz).
        font.getData().setScale(1f / GameConfig.UI_SCALE);
        font.setUseIntegerPositions(false);  // sin redondear a píxeles de interfaz: el espaciado entre letras queda parejo
        return font;
    }
}

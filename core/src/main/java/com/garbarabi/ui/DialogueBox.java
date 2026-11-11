package com.garbarabi.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.garbarabi.GameConfig;
import com.garbarabi.input.PlayerInput;

/**
 * Cuadro de diálogo inferior, dibujado desde la capa "dialogue-ui" de UI_Text_Box.tmx.
 * El texto solo se escribe dentro del objeto "dialogue-box-object" de la capa "dialogue-box".
 */
public class DialogueBox {
    private static final String FRAME_LAYER = "dialogue-ui";
    private static final String AREA_LAYER = "dialogue-box";
    private static final String AREA_OBJECT = "dialogue-box-object";
    private static final float BOTTOM_MARGIN = -1f;  // distancia desde el borde inferior; negativo = sobresale

    private final TiledFrame frame;
    private final float x;       // esquina inferior izquierda del marco, centrado horizontalmente
    private final float bottom;
    private final TextBox text;
    private String[] entries = new String[0];  // comentarios del diálogo, en orden
    private int entryIndex;                    // comentario que se muestra ahora
    private boolean active;                    // true mientras el cuadro está abierto

    public DialogueBox(TiledMap map, BitmapFont font) {
        this.frame = new TiledFrame(map, FRAME_LAYER);
        this.x = (GameConfig.UI_WIDTH - frame.getWidth()) / 2f;
        this.bottom = BOTTOM_MARGIN;
        Rectangle area = frame.area(AREA_LAYER, AREA_OBJECT, x, bottom);
        this.text = TiledFrame.textBox(font, area);
    }

    /** Abre el cuadro con una lista de comentarios; cada uno se muestra en orden al avanzar con Z. */
    public void open(String[] comments) {
        entries = comments;
        entryIndex = 0;
        text.setText(entries[0]);
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    /** Z avanza: completa la escritura, pasa a la página siguiente, o al comentario siguiente; al final cierra. */
    public void update(float delta, PlayerInput input) {
        text.update(delta, input.isCancelHeld());
        if (!input.isConfirmPressed()) return;

        if (!text.isComplete()) {
            text.advance();
        } else if (entryIndex + 1 < entries.length) {
            entryIndex++;
            text.setText(entries[entryIndex]);
        } else {
            active = false;
        }
    }

    public void draw(Batch batch) {
        if (!active) return;
        frame.draw(batch, x, bottom);
        text.draw(batch);
    }
}

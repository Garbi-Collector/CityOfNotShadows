package com.garbarabi.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.garbarabi.Assets;
import com.garbarabi.GameConfig;
import com.garbarabi.entities.Direction;
import com.garbarabi.entities.PlayerProfile;
import com.garbarabi.entities.Stat;
import com.garbarabi.i18n.Localization;
import com.garbarabi.input.PlayerInput;

/**
 * Menú del personaje (C) armado con la capa "menu-player-ui" de UI_Text_Box.tmx.
 * Cada área está en la capa "menu-player-box": nivel, estadísticas, ánimo y la imagen del jugador.
 */
public class PlayerMenu {
    private static final String FRAME_LAYER = "menu-player-ui";
    private static final String AREA_LAYER = "menu-player-box";
    private static final String LEVEL_AREA = "lvl-box";
    private static final String STATS_AREA = "statistics-box";
    private static final String FEELING_AREA = "feeling-box";
    private static final String IMAGE_AREA = "player-img-box";

    // Columnas de estadísticas: FUE / CON / SAB a la izquierda, DES / INT / CAR a la derecha
    private static final Stat[] LEFT_STATS = {Stat.STRENGTH, Stat.CONSTITUTION, Stat.WISDOM};
    private static final Stat[] RIGHT_STATS = {Stat.DEXTERITY, Stat.INTELLIGENCE, Stat.CHARISMA};
    private static final float RIGHT_COLUMN_OFFSET = 14f;  // inicio de la columna derecha, desde el borde del área

    // Caminata hacia adelante: quieto, paso, quieto, paso, cada fotograma dura WALK_FRAME_SECONDS
    private static final String[] WALK_CYCLE = {"walk-1", "stand", "walk-2", "stand"};
    private static final float WALK_FRAME_SECONDS = 0.2f;

    private final TiledFrame frame;
    private final float left;   // esquina inferior izquierda del marco, centrado en pantalla
    private final float bottom;
    private final Assets assets;
    private final BitmapFont font;
    private final Localization localization;
    private final PlayerProfile profile;
    private final Rectangle levelArea;
    private final Rectangle statsArea;
    private final Rectangle imageArea;
    private final TextBox description;  // nombre y descripción del ánimo, con la misma paginación que el diálogo
    private boolean open;
    private float walkTime;             // tiempo de la animación de caminata, desde que se abrió el menú

    public PlayerMenu(TiledMap map, Assets assets, BitmapFont font, Localization localization, PlayerProfile profile) {
        this.frame = new TiledFrame(map, FRAME_LAYER);
        this.left = (GameConfig.UI_WIDTH - frame.getWidth()) / 2f;
        this.bottom = (GameConfig.UI_HEIGHT - frame.getHeight()) / 2f;
        this.assets = assets;
        this.font = font;
        this.localization = localization;
        this.profile = profile;
        this.levelArea = frame.area(AREA_LAYER, LEVEL_AREA, left, bottom);
        this.statsArea = frame.area(AREA_LAYER, STATS_AREA, left, bottom);
        this.imageArea = frame.area(AREA_LAYER, IMAGE_AREA, left, bottom);
        this.description = TiledFrame.textBox(font, frame.area(AREA_LAYER, FEELING_AREA, left, bottom));
    }

    public boolean isOpen() {
        return open;
    }

    /** Abre el menú, reinicia la caminata y empieza a escribir la descripción del ánimo actual. */
    public void open() {
        open = true;
        walkTime = 0f;
        description.setText(localization.get(profile.getMood().nameKey()) + ": "
                + localization.get(profile.getMood().descriptionKey()));
    }

    /** C o X cierran el menú; Z avanza la descripción. */
    public void update(float delta, PlayerInput input) {
        walkTime += delta;
        description.update(delta, false);
        if (input.isMenuPressed() || input.isCancelPressed()) {
            open = false;
        } else if (input.isConfirmPressed()) {
            description.advance();
        }
    }

    public void draw(Batch batch) {
        if (!open) return;
        frame.draw(batch, left, bottom);
        PixelFont.drawAt(font, batch, localization.get("menu.level") + " " + profile.getLevel(), levelArea.x, TiledFrame.top(levelArea));
        drawStats(batch);
        drawWalker(batch);
        description.draw(batch);
    }

    /** Dos columnas de abreviatura y valor, una fila por estadística, dentro del área de estadísticas. */
    private void drawStats(Batch batch) {
        float top = TiledFrame.top(statsArea);
        float rightX = statsArea.x + RIGHT_COLUMN_OFFSET;
        for (int row = 0; row < LEFT_STATS.length; row++) {
            float rowTop = top + row * PixelFont.LINE_HEIGHT;  // desde arriba: las filas bajan
            PixelFont.drawAt(font, batch, statText(LEFT_STATS[row]), statsArea.x, rowTop);
            PixelFont.drawAt(font, batch, statText(RIGHT_STATS[row]), rightX, rowTop);
        }
    }

    private String statText(Stat stat) {
        return localization.get(stat.key()) + " " + profile.getStat(stat);
    }

    /** Dibuja al jugador caminando hacia adelante, a la escala de los tiles, centrado en su área. */
    private void drawWalker(Batch batch) {
        int index = (int) (walkTime / WALK_FRAME_SECONDS) % WALK_CYCLE.length;
        Texture texture = assets.getTexture(Assets.playerFrame(Direction.DOWN, WALK_CYCLE[index]));
        float width = texture.getWidth() / GameConfig.UI_SCALE;
        float height = texture.getHeight() / GameConfig.UI_SCALE;
        batch.draw(texture,
                imageArea.x + (imageArea.width - width) / 2f,
                imageArea.y + (imageArea.height - height) / 2f,
                width, height);
    }
}

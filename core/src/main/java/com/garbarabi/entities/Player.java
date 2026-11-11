package com.garbarabi.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.garbarabi.Assets;
import com.garbarabi.world.CollisionSystem;

import java.util.EnumMap;

/** Posición en tiles (1 unidad = 1 tile). El origen es la esquina inferior izquierda del sprite. */
public class Player {
    private static final float WALK_SPEED = 3.75f;     // tiles/s, equivale a 60 px/s con tiles de 16 px
    private static final float FRAME_DURATION = 0.15f; // s por fotograma de caminata
    private static final float HITBOX_WIDTH = 0.5f;    // tiles, centrada en los pies
    private static final float HITBOX_HEIGHT = 0.25f;  // tiles, solo la franja de los pies

    // Sprites por dirección: uno quieto y una animación de caminata para cada una.
    private final EnumMap<Direction, TextureRegion> standFrames = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, Animation<TextureRegion>> walkAnimations = new EnumMap<>(Direction.class);
    private final Vector2 position = new Vector2();
    private final Rectangle hitbox = new Rectangle();  // reutilizado para no crear objetos en cada frame
    private final float widthTiles;
    private final float heightTiles;

    private Direction facing = Direction.DOWN;
    private PlayerState state = PlayerState.STANDING;
    private float stateTime;  // tiempo en el estado actual, para elegir el fotograma de la animación

    public Player(Assets assets, float x, float y, float unitsPerPixel) {
        for (Direction direction : Direction.values()) {
            TextureRegion stand = new TextureRegion(assets.getTexture(Assets.playerFrame(direction, "stand")));
            TextureRegion walk1 = new TextureRegion(assets.getTexture(Assets.playerFrame(direction, "walk-1")));
            TextureRegion walk2 = new TextureRegion(assets.getTexture(Assets.playerFrame(direction, "walk-2")));

            standFrames.put(direction, stand);
            // Ciclo de 4 pasos: walk-1, stand, walk-2, stand
            walkAnimations.put(direction, new Animation<>(FRAME_DURATION, walk1, stand, walk2, stand));
        }

        // El tamaño del jugador sale del sprite de frente, convertido a tiles.
        TextureRegion reference = standFrames.get(Direction.DOWN);
        widthTiles = reference.getRegionWidth() * unitsPerPixel;
        heightTiles = reference.getRegionHeight() * unitsPerPixel;
        position.set(x, y);
    }

    /** movement debe venir normalizado (o cero). Los ejes se resuelven por separado para deslizarse por las paredes. */
    public void update(float delta, Vector2 movement, CollisionSystem collisions, float mapWidthTiles, float mapHeightTiles) {
        if (movement.isZero()) {
            setState(PlayerState.STANDING);
        } else {
            facing = Direction.fromVector(movement);
            setState(PlayerState.WALKING);

            float step = WALK_SPEED * delta;
            moveAxis(movement.x * step, 0f, collisions);
            moveAxis(0f, movement.y * step, collisions);

            // Impide salir del mapa.
            position.x = MathUtils.clamp(position.x, 0f, mapWidthTiles - widthTiles);
            position.y = MathUtils.clamp(position.y, 0f, mapHeightTiles - heightTiles);
        }
        stateTime += delta;
    }

    public void draw(Batch batch) {
        batch.draw(getCurrentFrame(), position.x, position.y, widthTiles, heightTiles);
    }

    public float getCenterX() {
        return position.x + widthTiles / 2f;
    }

    public float getCenterY() {
        return position.y + heightTiles / 2f;
    }

    /** Mueve un eje y solo confirma el paso si la hitbox no choca con ningún sólido. */
    private void moveAxis(float dx, float dy, CollisionSystem collisions) {
        float nextX = position.x + dx;
        float nextY = position.y + dy;
        if (!collisions.collides(hitboxAt(nextX, nextY))) {
            position.set(nextX, nextY);
        }
    }

    /** Hitbox de los pies en la posición indicada: solo colisiona la franja inferior del sprite. */
    private Rectangle hitboxAt(float x, float y) {
        hitbox.set(x + (widthTiles - HITBOX_WIDTH) / 2f, y, HITBOX_WIDTH, HITBOX_HEIGHT);
        return hitbox;
    }

    /** Cambia de estado y reinicia el contador, para que la animación empiece desde el primer fotograma. */
    private void setState(PlayerState newState) {
        if (state != newState) {
            state = newState;
            stateTime = 0f;
        }
    }

    private TextureRegion getCurrentFrame() {
        return state == PlayerState.WALKING
                ? walkAnimations.get(facing).getKeyFrame(stateTime, true)
                : standFrames.get(facing);
    }
}

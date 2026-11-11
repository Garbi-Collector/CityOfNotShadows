package com.garbarabi.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/** Traduce las teclas del teclado a acciones del juego (movimiento, Z, X, C). */
public final class PlayerInput {
    private final Vector2 movement = new Vector2();  // reutilizado para no crear objetos en cada frame

    /** Movimiento deseado este frame, normalizado para que las diagonales no sean más rápidas. */
    public Vector2 readMovement() {
        movement.set(0f, 0f);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  movement.x -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) movement.x += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  movement.y -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    movement.y += 1f;
        return movement.nor();
    }

    /** Z: confirmar. Avanza o completa el diálogo. */
    public boolean isConfirmPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.Z);
    }

    /** X: cancelar. Cierra el menú. */
    public boolean isCancelPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.X);
    }

    /** X mantenida: acelera la escritura del diálogo. */
    public boolean isCancelHeld() {
        return Gdx.input.isKeyPressed(Input.Keys.X);
    }

    /** C: abre y cierra el menú del personaje. */
    public boolean isMenuPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.C);
    }
}

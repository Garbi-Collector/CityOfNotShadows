package com.garbarabi.entities;

import com.badlogic.gdx.math.Vector2;

/** Las cuatro direcciones en las que puede mirar el jugador. */
public enum Direction {
    DOWN("down"),
    LEFT("left"),
    RIGHT("right"),
    UP("top"); // los sprites llaman "top" a la dirección arriba

    private final String fileName;  // parte del nombre del archivo de sprite para esta dirección

    Direction(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    /** Dirección dominante del vector. En diagonales gana la vertical. */
    public static Direction fromVector(Vector2 movement) {
        if (Math.abs(movement.y) >= Math.abs(movement.x)) {
            return movement.y > 0 ? UP : DOWN;
        }
        return movement.x > 0 ? RIGHT : LEFT;
    }
}

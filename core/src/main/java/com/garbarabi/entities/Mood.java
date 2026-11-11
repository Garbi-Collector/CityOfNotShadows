package com.garbarabi.entities;

/** Estado de ánimo del jugador. Nombre y descripción se leen de los archivos de idioma. */
public enum Mood {
    NEUTRAL,
    HAPPY,
    SAD,
    ANGRY,
    SCARED,
    BLUSHING;

    /** Clave del archivo de idioma para el nombre, p. ej. "mood.BLUSHING.name". */
    public String nameKey() {
        return "mood." + name() + ".name";
    }

    /** Clave del archivo de idioma para la descripción, p. ej. "mood.BLUSHING.description". */
    public String descriptionKey() {
        return "mood." + name() + ".description";
    }
}

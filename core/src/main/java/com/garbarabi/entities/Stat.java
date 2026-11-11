package com.garbarabi.entities;

/** Estadísticas base de D&D. La abreviatura se traduce en el archivo de idioma con la clave {@link #key()}. */
public enum Stat {
    STRENGTH,       // FUE
    DEXTERITY,      // DES
    CONSTITUTION,   // CON
    INTELLIGENCE,   // INT
    WISDOM,         // SAB
    CHARISMA;       // CAR

    /** Clave del archivo de idioma para la abreviatura, p. ej. "stat.STRENGTH". */
    public String key() {
        return "stat." + name();
    }
}

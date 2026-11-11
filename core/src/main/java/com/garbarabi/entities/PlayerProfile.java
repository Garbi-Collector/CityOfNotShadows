package com.garbarabi.entities;

import java.util.EnumMap;

/** Datos del personaje que muestra el menú (C): nivel, estadísticas y estado de ánimo. */
public class PlayerProfile {
    private final EnumMap<Stat, Integer> stats = new EnumMap<>(Stat.class);  // valor de cada estadística
    private int level = 1;
    private Mood mood;

    public PlayerProfile() {
        // Valores de prueba; más adelante vendrán de la partida guardada.
        stats.put(Stat.STRENGTH, 12);
        stats.put(Stat.DEXTERITY, 14);
        stats.put(Stat.CONSTITUTION, 10);
        stats.put(Stat.INTELLIGENCE, 16);
        stats.put(Stat.WISDOM, 8);
        stats.put(Stat.CHARISMA, 13);
        mood = Mood.BLUSHING;
    }

    public int getLevel() {
        return level;
    }

    public int getStat(Stat stat) {
        return stats.get(stat);
    }

    public void setStat(Stat stat, int value) {
        stats.put(stat, value);
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }
}

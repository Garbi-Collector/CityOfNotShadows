package com.garbarabi.i18n;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/** Textos del juego leídos de assets/i18n/&lt;idioma&gt;.json. Si falta una clave, devuelve la propia clave. */
public final class Localization {
    private final JsonValue values;  // contenido completo del archivo de idioma

    /** Carga el archivo del idioma indicado, p. ej. "es" o "en". */
    public Localization(String language) {
        this.values = new JsonReader().parse(Gdx.files.internal("i18n/" + language + ".json"));
    }

    /** Devuelve un texto simple por su clave; si no existe, la propia clave, para notar el faltante. */
    public String get(String key) {
        JsonValue value = values.get(key);
        return value == null ? key : value.asString();
    }

    /** Devuelve una lista de textos (p. ej. los comentarios de un diálogo); vacía si la clave no existe. */
    public String[] getArray(String key) {
        JsonValue value = values.get(key);
        return value == null ? new String[0] : value.asStringArray();
    }
}

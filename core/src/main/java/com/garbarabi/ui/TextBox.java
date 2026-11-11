package com.garbarabi.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Texto dentro de un área fija de píxeles, con paginación y animación de escritura.
 * Si una palabra no cabe en las líneas disponibles, se corta la página antes de ella y se pone "...".
 * La palabra que no entró pasa a la página siguiente. Coordenadas: esquina superior izquierda.
 */
public class TextBox {
    private static final float CHARS_PER_SECOND = 30f;       // ritmo normal, moderadamente rápido
    private static final float FAST_CHARS_PER_SECOND = 120f; // ritmo con X mantenida
    private static final String ELLIPSIS = "...";

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();  // mide el ancho de los textos para saber si entran
    private final float x;
    private final float y;
    private final float width;       // ancho máximo de una línea, en píxeles del sprite
    private final int maxLines;      // cantidad de líneas que caben en el área
    private final List<Page> pages = new ArrayList<>();
    private int pageIndex;           // página que se muestra ahora
    private float typed;             // caracteres ya revelados de la página actual

    public TextBox(BitmapFont font, float x, float y, float width, int maxLines) {
        this.font = font;
        this.x = x;
        this.y = y;
        this.width = width;
        this.maxLines = maxLines;
    }

    /** Reemplaza el texto y empieza a escribirlo desde la primera página. */
    public void setText(String text) {
        pages.clear();
        String[] words = text.trim().split("\\s+");
        int next = 0;
        do {
            next = addPage(words, next);
        } while (next < words.length);
        pageIndex = 0;
        typed = 0f;
    }

    /** Avanza la animación. Con fast = true (X mantenida) se escribe más rápido. */
    public void update(float delta, boolean fast) {
        if (isTyping()) {
            typed += delta * (fast ? FAST_CHARS_PER_SECOND : CHARS_PER_SECOND);
        }
    }

    /** Z: si la página se está escribiendo, la completa; si ya está completa, pasa a la siguiente. */
    public void advance() {
        if (isTyping()) {
            typed = currentPage().length;
        } else if (hasNextPage()) {
            pageIndex++;
            typed = 0f;
        }
    }

    /** True cuando no queda nada por escribir ni por mostrar. */
    public boolean isComplete() {
        return !isTyping() && !hasNextPage();
    }

    /** Dibuja solo los caracteres ya revelados, línea por línea. */
    public void draw(Batch batch) {
        Page page = currentPage();
        int visible = (int) typed;
        for (int i = 0; i < page.lines.length && visible > 0; i++) {
            String line = page.lines[i];
            int count = Math.min(visible, line.length());
            PixelFont.drawAt(font, batch, line.substring(0, count), x, y + i * PixelFont.LINE_HEIGHT);
            visible -= line.length();
        }
    }

    private boolean isTyping() {
        return typed < currentPage().length;
    }

    private boolean hasNextPage() {
        return pageIndex + 1 < pages.size();
    }

    private Page currentPage() {
        return pages.get(pageIndex);
    }

    /** Arma una página desde words[start] y devuelve el índice de la primera palabra que queda para después. */
    private int addPage(String[] words, int start) {
        List<String> lines = new ArrayList<>();
        String line = "";
        int lineStart = start;  // índice de la primera palabra de la línea actual
        int i = start;
        while (i < words.length) {
            String candidate = line.isEmpty() ? words[i] : line + " " + words[i];
            if (fits(candidate)) {
                line = candidate;
                i++;
            } else if (lines.size() + 1 < maxLines) {
                lines.add(line);
                line = "";
                lineStart = i;
            } else {
                break;  // no hay más líneas: la página termina aquí
            }
        }

        if (i >= words.length) {
            lines.add(line);
            pages.add(new Page(lines));
            return words.length;
        }

        // No cabe la palabra words[i]: se suple por "..." y las palabras que sobran van a la siguiente página.
        int end = i;
        String truncated = withEllipsis(words, lineStart, end);
        while (!fits(truncated) && end > lineStart) {
            end--;
            truncated = withEllipsis(words, lineStart, end);
        }
        lines.add(truncated);
        pages.add(new Page(lines));
        return Math.max(end, start + 1);  // siempre avanza, aunque la primera palabra no quepa nunca
    }

    /** Une words[from..to) y agrega "..." al final. */
    private static String withEllipsis(String[] words, int from, int to) {
        StringBuilder text = new StringBuilder();
        for (int i = from; i < to; i++) {
            if (text.length() > 0) text.append(' ');
            text.append(words[i]);
        }
        if (text.length() > 0) text.append(' ');
        return text.append(ELLIPSIS).toString();
    }

    /** True si el texto entra en el ancho de una línea. */
    private boolean fits(String text) {
        layout.setText(font, text);
        return layout.width <= width;
    }

    /** Una página ya partida en líneas. length es la cantidad total de caracteres, para la animación. */
    private static final class Page {
        final String[] lines;
        final int length;

        Page(List<String> lines) {
            this.lines = lines.toArray(new String[0]);
            int total = 0;
            for (String line : this.lines) {
                total += line.length();
            }
            this.length = total;
        }
    }
}

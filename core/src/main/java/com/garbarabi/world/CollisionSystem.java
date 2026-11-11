package com.garbarabi.world;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/** Sólidos del mapa: objetos rectangulares con la propiedad "collides" = true. Se guardan en tiles. */
public class CollisionSystem {
    private static final String COLLIDES_PROPERTY = "collides";

    private final Array<Rectangle> solids = new Array<>();  // rectángulos sólidos, ya convertidos a tiles

    /** Recorre todas las capas del mapa y guarda los objetos marcados como sólidos. */
    public CollisionSystem(TiledMap map, float unitsPerPixel) {
        for (MapLayer layer : map.getLayers()) {
            for (MapObject object : layer.getObjects()) {
                if (object instanceof RectangleMapObject && isSolid(object)) {
                    Rectangle r = ((RectangleMapObject) object).getRectangle();
                    solids.add(new Rectangle(
                            r.x * unitsPerPixel,
                            r.y * unitsPerPixel,
                            r.width * unitsPerPixel,
                            r.height * unitsPerPixel));
                }
            }
        }
    }

    /** True si la hitbox se superpone con algún sólido. */
    public boolean collides(Rectangle hitbox) {
        for (Rectangle solid : solids) {
            if (hitbox.overlaps(solid)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSolid(MapObject object) {
        return Boolean.parseBoolean(String.valueOf(object.getProperties().get(COLLIDES_PROPERTY)));
    }
}

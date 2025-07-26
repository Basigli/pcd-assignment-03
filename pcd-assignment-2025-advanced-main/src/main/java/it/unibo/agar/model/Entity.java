package it.unibo.agar.model;

import java.io.Serializable;

public interface Entity extends Serializable {
    static final long serialVersionUID = 1L;
    String getId();
    double getMass();
    double getX();
    double getY();
    double getRadius();

    default double distanceTo(final Entity other) {
        double dx = getX() - other.getX();
        double dy = getY() - other.getY();
        return Math.hypot(dx, dy);
    }
}

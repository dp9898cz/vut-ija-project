package ija_project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Objects;

/**
 * Coordinate represents one point on the map
 * It has x and y double property
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version %I%, %G%
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Coordinate {
    /**
     * X coordinate
     */
    private double x;
    /**
     * Y coordinate
     */
    private double y;

    /**
     * Default constructor for Jackson
     */
    private Coordinate() {}

    /**
     * Contructor of Coordinate
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * {@link Coordinate#x}
     */
    public double getX() {
        return x;
    }

    /**
     * {@link Coordinate#y}
     */
    public double getY() {
        return y;
    }

    /**
     * {@link Coordinate#x}
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * {@link Coordinate#y}
     */
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.getX(), getX()) == 0 &&
                Double.compare(that.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

package ija_project;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

public class Street implements Drawable{
    private final Coordinate start;
    private final Coordinate end;
    private final String name;

    public Street(String name, Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
        this.name = name;
    }

    @Override
    public List<Shape> getGui() {
        return Arrays.asList(
                new Line(start.getX(), start.getY(), end.getX(), end.getY()),
                new Text(Math.min(start.getX(), end.getX()) + (Math.abs(start.getX() - end.getX()) / 2),
                        Math.min(start.getY(), end.getY())  + (Math.abs(start.getY() - end.getY()) / 2),
                        name)
        );
    }
}

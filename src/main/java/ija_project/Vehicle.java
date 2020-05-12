package ija_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Vehicle implements Drawable, TimerMapUpdate {
    private Coordinate position;
    private final double speed;
    private double distance = 0;
    @JsonIgnore
    private final Path path;
    @JsonIgnore
    private final List<Shape> gui;

    //private Vehicle(){};

    // Constructor
    public Vehicle(Coordinate position, double speed, Path path) {
        this.position = position;
        this.path = path;
        this.speed = speed;
        this.gui = new ArrayList<>();
        this.gui.add(new Circle(position.getX(), position.getY(), 8, Color.RED));

    }


    @Override
    public List<Shape> getGui() {
        return gui;
    }

    private void move(Coordinate c) {
        for (Shape s : gui) {
            s.setTranslateX((c.getX() - position.getX()) + s.getTranslateX());
            s.setTranslateY((c.getY() - position.getY()) + s.getTranslateY());
        }
    }

    @Override
    public void update(LocalTime l) {
        Platform.runLater(() -> {
                distance += speed;
                System.out.println(String.format("distance: %f, vzdálenost: %f", path.getPathDistance(), distance));
                if (distance > path.getPathDistance()) return;
                Coordinate c = path.getDistanceCoordinate(distance);
                move(c);
                position = c;
            }
        );
    }

    public Coordinate getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }
}

package ija_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(converter = Vehicle.VehicleConstruct.class)
public class Vehicle implements Drawable, TimerMapUpdate {
    private Coordinate position;
    private double speed;
    private double distance = 0;
    private Path path;
    @JsonIgnore
    private List<Shape> gui;
    @JsonIgnore
    private String number = "";

    private Vehicle(){}

    // Constructor
    public Vehicle(Coordinate position, double speed, Path path) {
        this.position = position;
        this.path = path;
        this.speed = speed;
        setGui();
        setNumber();
    }

    @Override
    public List<Shape> getGui() {
        return gui;
    }

    private void setGui() {
        this.gui = new ArrayList<>();
        this.gui.add(new Circle(position.getX(), position.getY(), 8, Color.RED));
    }

    private void setNumber() {
        this.number = this.path.getNumber();
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

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "position=" + position +
                ", speed=" + speed +
                ", path=" + path +
                '}';
    }

    static class VehicleConstruct extends StdConverter<Vehicle,Vehicle> {

        @Override
        public Vehicle convert(Vehicle value) {
            value.setGui();
            value.setNumber();
            return value;
        }
    }
}

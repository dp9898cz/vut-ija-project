package ija_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import javafx.event.EventHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

@JsonDeserialize(converter = Vehicle.VehicleConstruct.class)
public class Vehicle implements Drawable, TimerMapUpdate {
    private Coordinate position;
    private double speed;
    private Path path;

    @JsonIgnore
    private int stopWaitCounter = 5;

    @JsonIgnore
    private double distance = 0;

    @JsonIgnore
    private List<Shape> gui;

    @JsonIgnore
    private String number = "";

    @JsonIgnore
    private static final String SQUARE_BUBBLE = "M24 1h-24v16.981h4v5.019l7-5.019h13z";

    // Default constructor for Jackson
    private Vehicle(){}

    // Constructor
    public Vehicle(Coordinate position, double speed, Path path) {
        this.position = position;
        this.path = path;
        this.speed = speed;
        setGui();
        setNumber();
    }

    public Tooltip hackTooltipStartTiming(Tooltip tooltip) {
        tooltip.setStyle("-fx-font-size: 16px; -fx-shape: \"" + SQUARE_BUBBLE + "\";");
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tooltip;
    }

    @Override
    public List<Shape> getGui() {
        return gui;
    }

    private void setGui() {
        this.gui = new ArrayList<>();
        this.gui.add(new Circle(position.getX(), position.getY(), 12, Color.WHITE));
        this.gui.add(new Circle(position.getX(), position.getY(), 10, Color.RED));
        Text text =new Text(position.getX()-6, position.getY()+5,this.path.getNumber());
        this.gui.add(text);
        Circle circle=new Circle(position.getX(), position.getY(), 12,Color.rgb(0,0,0,0));
        this.gui.add(circle);
        circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Tooltip cassava2 = new Tooltip(path.getNumber());
                Tooltip.install(circle, hackTooltipStartTiming(cassava2));
                circle.setStroke(Color.WHITE);
            }
        });
    }

    private void setNumber() {
        this.number = this.path.getNumber();
    }

    // move the bus
    private void move(Coordinate c) {
        for (Shape s : gui) {
            s.setTranslateX((c.getX() - position.getX()) + s.getTranslateX());
            s.setTranslateY((c.getY() - position.getY()) + s.getTranslateY());
        }
    }

    // update according to distance and speed
    @Override
    public void update(LocalTime l) {
        Platform.runLater(() -> {
                distance += speed;
                System.out.println(String.format("distance: %f, vzdálenost: %f", path.getPathDistance(), distance));
                Coordinate c;
                if (distance >= path.getPathDistance()) {
                    // set the last coordinates
                    c = path.getPath().get(path.getPath().size() - 1);
                }
                else {
                    c = path.getDistanceCoordinate(distance, this);
                }
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

    public double getDistance() {
        return distance;
    }

    public int getStopWaitCounter() {
        return stopWaitCounter;
    }

    public void setStopWaitCounter(int stopWaitCounter) {
        this.stopWaitCounter = stopWaitCounter;
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

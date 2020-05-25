package ija_project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.PopupWindow;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Stop class representing one Stop
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version 1.0
 */
@JsonDeserialize(converter = Stop.StopConstruct.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Stop implements Drawable {
    /**
     * Position of the stop
     */
    private Coordinate coordinates;

    /**
     * Name of the stop
     */
    private String name;

    /**
     * Street where the stop is located
     */
    @JsonIgnore
    private Street street;

    /**
     * Elements of the GUI
     */
    @JsonIgnore
    private List<Shape> gui;

    /**
     * Default constructor for Jackson purposes
     */
    private Stop(){}

    /**
     * Constructor of Stop (calls setGui())
     * @param coordinates position of the stop
     * @param name name of the stop
     */
    public Stop(Coordinate coordinates, String name) {
        this.coordinates = coordinates;
        this.name = name;
        this.street = null;
        setGui();
    }


    /**
     * Set the square bubble window
     * @param tooltip Tooltip
     * @return Tooltip
     */
    public Tooltip hackTooltipStartTiming(Tooltip tooltip) {
        tooltip.setStyle("-fx-font-size: 16px; -fx-shape: \"" + "M24 1h-24v16.981h4v5.019l7-5.019h13z" + "\";");
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(10)));
        }
        catch (Exception ignored) {}
        return tooltip;
    }

    /**
     * Get elements of GUI
     * {@link Stop#gui}
     * @return elements of GUI (list)
     */
    @Override
    public List<Shape> getGui() {
        return gui;
    }

    /**
     * Set elements of GUI
     * {@link Stop#gui}
     */
    private void setGui() {
        this.gui = new ArrayList<>();
        this.gui.add(new Circle(coordinates.getX(), coordinates.getY(), 7, Color.YELLOW));
        Circle circle =new Circle(coordinates.getX(), coordinates.getY(), 7, Color.YELLOW);

        circle.setOnMouseEntered(t -> {
            String x1 = name;
            Tooltip stop = new Tooltip(x1);
            Tooltip.install(circle, hackTooltipStartTiming(stop));
            circle.setStroke(Color.WHITE);
        });

        circle.setOnMouseExited(t -> circle.setStroke(Color.YELLOW));

        this.gui.add(circle);
    }

    /**
     * {@link Stop#coordinates}
     * @return Position coordinates
     */
    public Coordinate getCoordinates() {
        return coordinates;
    }

    /**
     * {@link Stop#street}
     * @return Street
     */
    public Street getStreet() {
        return street;
    }

    /**
     * {@link Stop#street}
     * @param street Street to set
     */
    public void setStreet(Street street) {
        this.street = street;
    }

    /**
     * {@link Stop#name}
     * @return Name of the street
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "coordinates=" + coordinates +
                '}';
    }

    /**
     * Static class for Jackson
     * Used for initialisation of gui elements
     * @author Daniel Pátek (xpatek08)
     * @author Daniel Čechák (xcecha06)
     * @version 1.0
     */
    static class StopConstruct extends StdConverter<Stop,Stop> {
        /**
         * Initialize Stop's gui
         * @param value Stop object
         * @return Initialized Stop object
         */
        @Override
        public Stop convert(Stop value) {
            value.setGui();
            return value;
        }

    }
}

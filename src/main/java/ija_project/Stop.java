package ija_project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import javafx.scene.shape.Shape;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(converter = Stop.StopConstruct.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Stop implements Drawable{
    @JsonIgnore
    private static final String SQUARE_BUBBLE = "M24 1h-24v16.981h4v5.019l7-5.019h13z";
    private Coordinate coordinates;
    @JsonIgnore
    private Street street;
    @JsonIgnore
    private List<Shape> gui;

    private Stop(){}
    public Stop(Coordinate coordinates) {
        this.coordinates = coordinates;
        this.street = null;
        setGui();
    }

    private Tooltip makeBubble(Tooltip tooltip) {

        return tooltip;
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
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(10)));
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
        this.gui.add(new Circle(coordinates.getX(), coordinates.getY(), 7, Color.YELLOW));
        Circle circle =new Circle(coordinates.getX(), coordinates.getY(), 7, Color.YELLOW);

        circle.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                double x= coordinates.getX();
                String x1 =String.valueOf(x);
                Tooltip zastavka = new Tooltip(x1);
                Tooltip.install(circle, hackTooltipStartTiming(zastavka));
                circle.setStroke(Color.WHITE);
            }
        });
        circle.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                circle.setStroke(Color.YELLOW);
            }
        });
        this.gui.add(circle);
    }

    public Coordinate getCoordinates() {
        return coordinates;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "coordinates=" + coordinates +
                '}';
    }

    static class StopConstruct extends StdConverter<Stop,Stop> {
        @Override
        public Stop convert(Stop value) {
            value.setGui();
            return value;
        }

    }
}

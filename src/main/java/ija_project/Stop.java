package ija_project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.sun.javafx.fxml.builder.TriangleMeshBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.TriangleMesh;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(converter = Stop.StopConstruct.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Stop implements Drawable{
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

    @Override
    public List<Shape> getGui() {
        return gui;
    }

    private void setGui() {
        this.gui = new ArrayList<>();
        this.gui.add(new Circle(coordinates.getX(), coordinates.getY(), 7, Color.YELLOW));
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

    static class StopConstruct extends StdConverter<Stop,Stop> {
        @Override
        public Stop convert(Stop value) {
            value.setGui();
            return value;
        }

    }
}

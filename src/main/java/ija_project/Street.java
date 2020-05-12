package ija_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

@JsonDeserialize(converter = Street.StreetConstruct.class)
public class Street implements Drawable{
    private Coordinate start;
    private Coordinate end;
    private String name;
    private List<Stop> stops;

    public Street() {}

    public Street(String name, Coordinate start, Coordinate end, List<Stop> stops) {
        this.start = start;
        this.end = end;
        this.name = name;
        this.stops = stops;
        setStops();
    }

    public void setStops() {
        for (Stop s: this.stops) {
            s.setStreet(this);
        }
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public List<Stop> getStops() {
        return stops;
    }

    @Override
    @JsonIgnore
    public List<Shape> getGui() {
        return Arrays.asList(
                new Line(start.getX(), start.getY(), end.getX(), end.getY()),
                new Text(Math.min(start.getX(), end.getX()) + (Math.abs(start.getX() - end.getX()) / 2),
                        Math.min(start.getY(), end.getY())  + (Math.abs(start.getY() - end.getY()) / 2),
                        name)
        );
    }

    static class StreetConstruct extends StdConverter<Street, Street> {

        @Override
        public Street convert(Street value) {
            value.setStops();
            return value;
        }
    }
}

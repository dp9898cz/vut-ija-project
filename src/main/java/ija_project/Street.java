package ija_project;

import javafx.scene.paint.Color;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.beans.property.DoubleProperty;
import javafx.scene.text.TextAlignment;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

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
        Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.setStroke(Color.rgb(100,100,100));
        line.setStrokeWidth(15);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        Line line2 = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line2.setStroke(Color.rgb(75,75,75));
        line2.setStrokeWidth(20);
        line2.setStrokeLineCap(StrokeLineCap.ROUND);

        double distance = pow(pow(end.getX()-start.getX(),2)+pow(end.getY()-start.getY(),2),0.5);
        System.out.println(String.format("distance: %f",  distance));
        int dcase = 0;
        if(distance<300){dcase = 1;}
        if(distance>300){dcase = 2;}
        if(distance>600){dcase = 3;}
        if(distance>900){dcase = 4;}


        double u1 =  end.getX() - start.getX() ;
        double u2 =  end.getY() - start.getY() ;

        double v1 = 1;
        double v2 = 0;

        double x1 = u1*v1+ u2*v2;
        double x2 = pow(pow(u1,2)+pow(u2,2),0.5)*pow(pow(v1,2)+pow(v2,2),0.5);
        double a = x1/x2;
        double b= acos(a);
        double result = toDegrees(b);
        if(u1<0 && u2>0) {
            result =result+180;
        }
        if(u1<0 && u2<0) {
            result =result-90;
        }
        if(u1>0 && u2<0) {
            result =-result;//ok
        }
        //-------------------
        Text text = new Text(Math.min(start.getX(), end.getX()) + (abs(start.getX() - end.getX()) / 2),
                Math.min(start.getY(), end.getY())  + (abs(start.getY() - end.getY()) / 2),
                name);
        text.setRotate(result);

        return Arrays.asList(line2,line,text);

    }

    @Override
    public String toString() {
        return "Street{" +
                "start=" + start +
                ", end=" + end +
                ", name='" + name + '\'' +
                ", stops=" + stops +
                '}';
    }

    static class StreetConstruct extends StdConverter<Street, Street> {

        @Override
        public Street convert(Street value) {
            value.setStops();
            return value;
        }
    }
}

package ija_project;

import javafx.scene.paint.Color;
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
        Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.setStroke(Color.rgb(100,100,100));
        line.setStrokeWidth(15);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        Line line2 = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line2.setStroke(Color.rgb(75,75,75));
        line2.setStrokeWidth(20);
        line2.setStrokeLineCap(StrokeLineCap.ROUND);

        Text text = new Text(Math.min(start.getX(), end.getX()) + (abs(start.getX() - end.getX()) / 2),
                Math.min(start.getY(), end.getY())  + (abs(start.getY() - end.getY()) / 2),
                name);
        double u1 =  end.getX() - start.getX() ;
        double u2 =  end.getY() - start.getY() ;
        double change;


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

        text.setRotate(result);

        return Arrays.asList(line2,line,text);

    }
}

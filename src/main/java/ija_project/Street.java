package ija_project;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.*;
import static javafx.scene.paint.Color.rgb;

@JsonDeserialize(converter = Street.StreetConstruct.class)
public class Street implements Drawable{
    public static double urovenzatizeni;
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
        if(distance>500){dcase = 2;}
        if(distance>700){dcase = 3;}


        double u1 =  end.getX() - start.getX() ;
        double u2 =  end.getY() - start.getY() ;

        double v1 = 1;
        double v2 = 0;

        double x1 = u1*v1+ u2*v2;
        double x2 = pow(pow(u1,2)+pow(u2,2),0.5)*pow(pow(v1,2)+pow(v2,2),0.5);
        double a = x1/x2;
        double b= acos(a);
        double result = toDegrees(b);
        double offsetx =0;
        double offsety = 0;
        if(u1<0 && u2>0) {
            result =result+180;
            offsetx= 0;
            offsety= 0;
        }
        if(u1<0 && u2<0) {
            result =result-90;
            offsetx= 0;
            offsety= 0;
        }
        if(u1>0 && u2<0) {
            result =-result;//ok
            offsetx= 0;
            offsety= +3;
        }
        if(u1>0 && u2>0) {
            offsetx= -3;
            offsety= +3;
        }
        if(u1==0 && u2>0) {
            offsetx= 100;
            offsety= 0;
        }
        if(u1==0 && u2<0) {
            offsetx= +1;
            offsety= 0;
        }
        if(u1<0 && u2==0) {
            offsetx= 0;
            offsety= 0;
        }
        if(u1>0 && u2==0) {
            offsetx= 0;
            offsety= 3;
        }
        //-------------------

        //-------------------
        Line clickable = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        clickable.setStroke(Color.rgb(75,75,75,0));
        clickable.setStrokeWidth(20);
        clickable.setStrokeLineCap(StrokeLineCap.ROUND);
        clickable.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent evemt) {
                Stage popupwindow=new Stage();

                popupwindow.initModality(Modality.APPLICATION_MODAL);
                popupwindow.setTitle("This is a pop up window");


                Label label1= new Label("Vyberte uroven zatizeni ulice");

                Button button= new Button("Uroven 0");
                Button button1= new Button("Uroven 1");
                Button button2= new Button("Uroven 2");
                Button button3= new Button("Uroven 3");
                button.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        new EventHandler<MouseEvent>() {
                            @Override public void handle(MouseEvent e) {
                                urovenzatizeni=0.0;
                                line2.setStroke(rgb(75,75,75));
                                popupwindow.close();

                            }
                        });
                button1.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        new EventHandler<MouseEvent>() {
                            @Override public void handle(MouseEvent e) {
                                urovenzatizeni=1.0;
                                line2.setStroke(Color.YELLOW);
                                popupwindow.close();

                            }
                        });
                button2.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        new EventHandler<MouseEvent>() {
                            @Override public void handle(MouseEvent e) {
                                urovenzatizeni=2.0;
                                line2.setStroke(Color.ORANGE);
                                popupwindow.close();

                            }
                        });
                button3.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        new EventHandler<MouseEvent>() {
                            @Override public void handle(MouseEvent e) {
                                urovenzatizeni= 3.0;
                                line2.setStroke(Color.RED);
                                popupwindow.close();

                            }
                        });
                VBox layout= new VBox(10);


                layout.getChildren().addAll(label1,button, button1,button2,button3);

                layout.setAlignment(Pos.CENTER);

                Scene scene1= new Scene(layout, 300, 250);

                popupwindow.setScene(scene1);

                popupwindow.showAndWait();
            }
        });
        //-------------------------------------
        Text text = new Text(0,0,name);
        Text text2 = new Text(0,0,name);
        Text text3 = new Text(0,0,name);
        Text text4= new Text(0,0,name);


        double setx,sety,setx2,sety2,setx3,sety3,setx4,sety4=0;
        double sirka=text.getBoundsInLocal().getWidth();
        double offsetsirka= (sirka/2);
        switch (dcase){
            case 1:
                text.setX(((start.getX()+end.getX())/2)-offsetsirka+offsetx);
                text.setY(((start.getY()+end.getY())/2)+offsety);
                text.setRotate(result);
                text.setStroke(Color.WHITE);
                return Arrays.asList(line2,line,text,clickable);
            case 2:
                setx=((start.getX()+end.getX())/2);
                sety=((start.getY()+end.getY())/2);

                setx3=((start.getX()+setx)/2);
                sety3=((start.getY()+sety)/2);

                setx=((setx3+setx)/2);
                sety=((sety3+sety)/2);

                setx4=((setx3+setx)/2);
                sety4=((sety3+sety)/2);

                setx=((setx+setx4)/2);
                sety=((sety4+sety)/2);

                text.setX(setx-offsetsirka+offsetx);
                text.setY(sety+offsety);
                text.setRotate(result);
                text.setStroke(Color.WHITE);


                setx2=((start.getX()+end.getX())/2);
                sety2=((start.getY()+end.getY())/2);

                setx3=((end.getX()+setx2)/2);
                sety3=((end.getY()+sety2)/2);

                setx=((setx3+setx2)/2);
                sety=((sety3+sety2)/2);

                setx4=((setx3+setx)/2);
                sety4=((sety3+sety)/2);

                setx2=((setx+setx4)/2);
                sety2=((sety4+sety)/2);


                text2.setX(setx2-offsetsirka+offsetx);
                text2.setY(sety2+offsety);
                text2.setRotate(result);
                text2.setStroke(Color.WHITE);

                return Arrays.asList(line2,line,text,text2,clickable);
            case 3:
                setx=((start.getX()+end.getX())/2);
                sety=((start.getY()+end.getY())/2);
                text.setX(setx-offsetsirka+offsetx);
                text.setY(sety+offsety);
                text.setRotate(result);
                text.setStroke(Color.WHITE);

                setx2=((start.getX()+setx)/2);
                sety2=((start.getY()+sety)/2);
                text2.setX(setx2-offsetsirka+offsetx);
                text2.setY(sety2+offsety);
                text2.setRotate(result);
                text2.setStroke(Color.WHITE);

                setx3=((setx+end.getX())/2);
                sety3=((sety+end.getY())/2);
                text3.setX(setx3-offsetsirka+offsetx);
                text3.setY(sety3+offsety);
                text3.setRotate(result);
                text3.setStroke(Color.WHITE);
                return Arrays.asList(line2,line,text,text2,text3,clickable);

        }



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

package ija_project;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import javafx.scene.AccessibleRole;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;
import static javafx.scene.paint.Color.rgb;

/**
 * Street class represent street
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version 1.0
 */
@JsonDeserialize(converter = Street.StreetConstruct.class)
public class Street implements Drawable{
    /**
     * Actual traffic on the street
     */
    @JsonIgnore
    public double traffic = 1.0;

    @JsonIgnore
    public Controller controller = null;

    @JsonIgnore
    private List<Street> alternativeRoute = new ArrayList<>();

    /**
     * Start point of the street
     */
    private Coordinate start;

    /**
     * End point of the street
     */
    private Coordinate end;

    /**
     * Street name
     */
    private String name;

    /**
     * List of Stops on the street
     */
    private List<Stop> stops;

    /**
     * Default constructor for Jackson purposes
     */
    public Street() {}

    /**
     * Street constructor (calls setStops())
     * @param name Street name
     * @param start Street start coordinates
     * @param end Street end coordinates
     * @param stops List of stops
     */
    public Street(String name, Coordinate start, Coordinate end, List<Stop> stops) {
        this.start = start;
        this.end = end;
        this.name = name;
        this.stops = stops;
        setStops();
    }

    /**
     * Set street of every stop at this street
     * {@link Street#stops}
     */
    public void setStops() {
        for (Stop s: this.stops) {
            s.setStreet(this);
        }
    }

    /**
     * {@link Street#start}
     * @return Start coordinates
     */
    public Coordinate getStart() {
        return start;
    }

    /**
     * {@link Street#end}
     * @return End coordinates
     */
    public Coordinate getEnd() {
        return end;
    }

    /**
     * {@link Street#traffic}
     * @return Current traffic
     */
    public double getTraffic() {
        return traffic;
    }

    /**
     * {@link Street#name}
     * @return Street name
     */
    public String getName() {
        return name;
    }

    /**
     * {@link Street#stops}
     * @return List of stops
     */
    public List<Stop> getStops() {
        return stops;
    }


    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Get gui (set all the elements of Street GUI)
     * @return List of shapes of GUI
     */
    @Override
    @JsonIgnore
    public List<Shape> getGui() {
        Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.setStroke(Color.rgb(100,100,100));
        line.setStrokeWidth(15);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setAccessibleRoleDescription("street-to-color");

        Line line2 = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line2.setStroke(Color.rgb(75,75,75));
        line2.setStrokeWidth(20);
        line2.setStrokeLineCap(StrokeLineCap.ROUND);

        double distance = pow(pow(end.getX() - start.getX(), 2) + pow(end.getY() - start.getY(), 2), 0.5);

        int nameQuantity = (distance > 310) ? 2 : 1;

        double u1 =  end.getX() - start.getX();
        double u2 =  end.getY() - start.getY();

        double v1 = 1;
        double v2 = 0;

        double x1 = u1*v1+ u2*v2;
        double x2 = pow(pow(u1,2)+pow(u2,2),0.5)*pow(pow(v1,2)+pow(v2,2),0.5);
        double angle = toDegrees(acos(x1 / x2));
        double offsetX = 0;
        double offsetY = 0;

        if(u1<0 && u2>0) {
            angle = angle + 180;
        }
        if(u1<0 && u2<0) {
            angle = angle - 90;
        }
        if(u1>0 && u2<0) {
            angle = -angle;
            offsetY = 3;
        }
        if(u1>0 && u2>0) {
            offsetX = -3;
            offsetY = 3;
        }
        if(u1==0 && u2>0) {
            offsetX = 1;
        }
        if(u1==0 && u2<0) {
            offsetX = 1;
        }
        if(u1>0 && u2==0) {
            offsetY = 3;
        }


        Line clickable = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        clickable.setStroke(Color.rgb(75,75,75,0));
        clickable.setStrokeWidth(20);
        clickable.setStrokeLineCap(StrokeLineCap.ROUND);
        clickable.setAccessibleRoleDescription(this.name);

        clickable.setOnMousePressed(event -> {
            if (controller.isInStateOfChoosingRoute()) {
                if (controller.getToBeClosed().equals(this)) return;

                if (line.getStroke().equals(Color.LIMEGREEN)) {
                    controller.deleteStreetFromAltList(this);
                    line.setStroke(Color.rgb(100,100,100));
                } else {
                    line.setStroke(Color.LIMEGREEN);
                    controller.addStreetToAltList(this);
                }

                if (controller.checkCircleCompleted()) {
                    controller.manageAlternativeRoute();
                }
            }
            else {
                Stage popupwindow = new Stage();
                popupwindow.initModality(Modality.APPLICATION_MODAL);
                popupwindow.setTitle("Možnosti ulice");

                Label label1 = new Label("Vyberte úroveň zatížení ulice:");
                Button button = new Button("Úroveň 0");
                Button button1 = new Button("Úroveň 1");
                Button button2 = new Button("Úroveň 2");
                Button button3 = new Button("Úroveň 3");

                Label label2 = new Label("Další možnosti:");
                Button button4;

                if (this.equals(controller.getToBeClosed())) {
                    button4 = new Button("Otevřít ulici a zavřít objížďku.");
                    button4.addEventHandler(MouseEvent.MOUSE_CLICKED,
                            e -> {
                                line.setStroke(Color.rgb(100,100,100));
                                popupwindow.close();

                                controller.destroyAlternativeRoute();
                                //controller.setInStateOfChoosingRoute(true);
                                //controller.setToBeClosed(this);
                            });
                }
                else {
                    button4 = new Button("Zavřít ulici a zvolit objížďku");

                    button4.setAccessibleText("street_lock_down");

                    button4.addEventHandler(MouseEvent.MOUSE_CLICKED,
                            e -> {
                                line.setStroke(Color.RED);
                                popupwindow.close();
                                controller.stopTimer();
                                controller.setInStateOfChoosingRoute(true);
                                controller.setToBeClosed(this);
                            });
                }

                button.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                traffic = 1.0;
                                line2.setStroke(rgb(75, 75, 75));
                                popupwindow.close();
                            }
                        });
                button1.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                traffic = 1.3;
                                line2.setStroke(Color.YELLOW);
                                popupwindow.close();
                            }
                        });
                button2.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                traffic = 1.6;
                                line2.setStroke(Color.ORANGE);
                                popupwindow.close();
                            }
                        });
                button3.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent e) {
                                traffic = 2;
                                line2.setStroke(Color.RED);
                                popupwindow.close();
                            }
                        });
                VBox layout = new VBox(10);
                layout.getChildren().addAll(label1, button, button1, button2, button3, label2, button4);
                layout.setAlignment(Pos.CENTER);
                Scene scene1 = new Scene(layout, 300, 250);
                popupwindow.setScene(scene1);
                popupwindow.showAndWait();
            }
        });

        //-------------------------------------
        Text text = new Text(0,0, name);
        Text text2 = new Text(0,0, name);

        double setX, setY, setX2, setY2, setx3, setx4, sety3, sety4;
        double widthOffset = (text.getBoundsInLocal().getWidth() / 2);

        switch (nameQuantity) {
            case 1:
                text.setX(((start.getX()+end.getX())/2)- widthOffset + offsetX);
                text.setY(((start.getY()+end.getY())/2)+ offsetY);

                text.setRotate(angle);
                text.setStroke(Color.WHITE);
                return Arrays.asList(line2,line,text,clickable);
            case 2:
                setX =((start.getX() + end.getX()) / 2);
                setY =((start.getY() + end.getY()) / 2);

                setx3=((start.getX() + setX) / 2);
                sety3=((start.getY() + setY) / 2);

                setX =((setx3+ setX)/2);
                setY =((sety3+ setY)/2);

                setx4=((setx3+ setX)/2);
                sety4=((sety3+ setY)/2);

                setX =((setX +setx4)/2);
                setY =((sety4+ setY)/2);

                text.setX(setX - widthOffset + offsetX);
                text.setY(setY + offsetY);
                text.setRotate(angle);
                text.setStroke(Color.WHITE);

                setX2 =((start.getX()+end.getX())/2);
                setY2 =((start.getY()+end.getY())/2);

                setx3=((end.getX()+ setX2)/2);
                sety3=((end.getY()+ setY2)/2);

                setX =((setx3+ setX2)/2);
                setY =((sety3+ setY2)/2);

                setx4=((setx3+ setX)/2);
                sety4=((sety3+ setY)/2);

                setX2 =((setX +setx4)/2);
                setY2 =((sety4+ setY)/2);

                text2.setX(setX2 - widthOffset + offsetX);
                text2.setY(setY2 + offsetY);
                text2.setRotate(angle);
                text2.setStroke(Color.WHITE);
                return Arrays.asList(line2,line,text,text2,clickable);
        }
        return Arrays.asList(line2,line);
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

    /**
     * Static class for Jackson
     * Used for initialisation of stops
     * @author Daniel Pátek (xpatek08)
     * @author Daniel Čechák (xcecha06)
     * @version 1.0
     */
    static class StreetConstruct extends StdConverter<Street, Street> {

        /**
         * Initialize Street's stops
         * @param value Street object
         * @return Initialized Street object
         */
        @Override
        public Street convert(Street value) {
            value.setStops();
            return value;
        }
    }
}

package ija_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Field;

import static javafx.scene.paint.Color.rgb;

@JsonDeserialize(converter = Vehicle.VehicleConstruct.class)
public class Vehicle implements Drawable, TimerMapUpdate {
    private Coordinate position;
    private double speed;
    private Path path;
    private List<Integer> stopsTimes; // list of times at stops
    private int goEveryXMinute = 2; // go every 2 minutes
    private int startMinute = 1; // start every :01 :03 :05 atd

    @JsonIgnore
    private int stopsPassed = 0;
    @JsonIgnore
    private Coordinate lastStop;
    @JsonIgnore
    private LocalTime startTime = null;
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
    public Vehicle(Coordinate position, double speed, Path path, List<Integer> times, int goEveryXMinute, int startMinute) {
        this.position = position;
        this.path = path;
        this.speed = speed;
        this.stopsTimes = times;
        this.goEveryXMinute = goEveryXMinute;
        this.startMinute = startMinute;
        setNumber();
        setGui();
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
        Color color = Color.WHITE;
        if (this.number.startsWith("1")) color = Color.INDIANRED;
        if (this.number.startsWith("2")) color = Color.DARKGREEN;
        if (this.number.startsWith("3")) color = Color.ORANGE;
        if (this.number.startsWith("4")) color = Color.BLUE;
        this.gui.add(new Circle(position.getX(), position.getY(), 12, Color.WHITE));
        this.gui.add(new Circle(position.getX(), position.getY(), 10, color));
        Text text = new Text(position.getX()-6, position.getY()+5, this.path.getNumber());
        this.gui.add(text);
        Circle circle=new Circle(position.getX(), position.getY(), 12, rgb(0,0,0,0));
        this.gui.add(circle);

        //--------------------------
        //Lines for lines
        List <Coordinate> list = path.getPath();
        Iterator<Coordinate> iterator = list.iterator();
        Coordinate first_coordinate = list.get(0);
        ArrayList <Line> lines = new ArrayList<>();
        while(iterator.hasNext()) {
            Coordinate coordinate_end = iterator.next();
            Line line = new Line(first_coordinate.getX(), first_coordinate.getY(), coordinate_end.getX(), coordinate_end.getY());
            line.setStrokeWidth(5);
            line.setStroke(rgb(0,0,0,0));
            first_coordinate = coordinate_end;
            lines.add(line);
        }

        circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                Tooltip cassava2 = new Tooltip(path.getNumber());
                Tooltip.install(circle, hackTooltipStartTiming(cassava2));
                circle.setStroke(Color.WHITE);
                Iterator <Line> iterator2 = lines.listIterator();
                while(iterator2.hasNext()){
                    Line line = iterator2.next();
                    line.setStroke(rgb(220,0,0,1));

                }
            }
        });
        circle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {

                Iterator <Line> iterator2 = lines.listIterator();
                while(iterator2.hasNext()){
                    Line line = iterator2.next();
                    line.setStroke(rgb(220,0,0,0));

                }
            }
        });
        Iterator <Line> iterator3 = lines.listIterator();
        while(iterator3.hasNext()){
            Line line = iterator3.next();
            this.gui.add(line);

        }

    }

    private void setNumber() {
        this.number = this.path.getNumber();
    }

    // move the bus
    private void move(Coordinate c) {
        for (Shape s : gui) {
            Class shape = s.getClass();
            Line linus = new Line();
            if( s.getClass() == linus.getClass()){
                continue;
            }
            s.setTranslateX((c.getX() - position.getX()) + s.getTranslateX());
            s.setTranslateY((c.getY() - position.getY()) + s.getTranslateY());
        }
    }

    // update according to distance and speed
    @Override
    public void update(LocalTime l) {
        Platform.runLater(() -> {
                distance += speed;
                //System.out.println(String.format("distance: %f, vzdálenost: %f", path.getPathDistance(), distance));
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

    public List<Integer> getStopsTimes() {
        return stopsTimes;
    }

    public int getGoEveryXMinute() {
        return goEveryXMinute;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getStopsPassed() {
        return stopsPassed;
    }

    public void setStopsPassed(int stopsPassed) {
        this.stopsPassed = stopsPassed;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Coordinate getLastStop() {
        return lastStop;
    }

    public void setLastStop(Coordinate lastStop) {
        this.lastStop = lastStop;
    }

    public String getNumber() {
        return number;
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
            value.setNumber();
            value.setGui();
            return value;
        }
    }
}

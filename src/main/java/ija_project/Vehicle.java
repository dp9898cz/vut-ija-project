package ija_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import javafx.application.Platform;
import javafx.scene.AccessibleRole;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private Street currentStreet;
    @JsonIgnore
    private int currentDelay = 0;

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

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @JsonIgnore
    public String getLineInfo() {
        String completed = "";
        completed += "Linka cislo " + number + "\n";
        // now I have all stops in variable path.stopList
        for (int i = 1; i < this.getPath().getStopList().size(); i++) {
            String line = "";
            if (stopsPassed == i) {
                line += "Aktualni poloha\n";
            }
            line += Integer.toString(i);
            line += ".\t";
            LocalTime time = this.getStartTime();
            time = time.truncatedTo(ChronoUnit.SECONDS);
            for (int j = 0; j < i; j++) {
                time = time.plusSeconds(this.getStopsTimes().get(j));
            }
            line += time.toString(); //get scheduled time
            line += "\t";
            line += "Nejake jmeno zastavky";
            line += "\n";
            completed = completed.concat(line);
        }
        completed += "Zpozdeni: ";
        completed += Integer.toString(currentDelay);
        completed += " s";
        return completed;
    }

    @JsonIgnore
    public long getPathLengthInSeconds() {
        long total = 0;
        for (Integer i : stopsTimes) {
            total += i;
        }
        return total;
    }

    @JsonIgnore
    public Integer findOutHowManyStopsPassedAlready(Integer currentTimePassed) {
        int handle = 0;
        int counter = 0;
        for (Integer i : stopsTimes) {
            handle += i;
            if (handle < currentTimePassed) {
                counter++;
            }
            else {
                break;
            }
        }
        return counter + 1; // plus start point
    }

    @JsonIgnore
    public Coordinate findOutLastStop(Integer currentTimePassed) {
        int lastStop = 0;
        int handle = 0;
        for (Integer i : stopsTimes) {
            handle += i;
            if (handle >= currentTimePassed || currentTimePassed <= stopsTimes.get(0))
                break;
            else
                lastStop++;
        }
        return path.getStopList().get(lastStop).getCoordinates();
    }


    @Override
    public List<Shape> getGui() {
        return gui;
    }

    private void setGui() {
        this.gui = new ArrayList<>();

        //--------------------------
        //Lines for lines
        List <Coordinate> list = path.getPath();
        Iterator<Coordinate> iterator = list.iterator();
        Coordinate first_coordinate = list.get(0);
        ArrayList <Line> lines = new ArrayList<>();
        Line start = new Line(first_coordinate.getX(),first_coordinate.getY(),first_coordinate.getX(),first_coordinate.getY());
        start.setStroke(rgb(0,0,0,0));
        start.setAccessibleRole(AccessibleRole.RADIO_MENU_ITEM);
        start.setStrokeWidth(12);
        lines.add(start);
        while(iterator.hasNext()) {
            Coordinate coordinate_end = iterator.next();
            Line line = new Line(first_coordinate.getX(), first_coordinate.getY(), coordinate_end.getX(), coordinate_end.getY());
            line.setStrokeWidth(5);
            line.setAccessibleRole(AccessibleRole.RADIO_MENU_ITEM);
            line.setStroke(rgb(0,0,0,0));
            first_coordinate = coordinate_end;
            lines.add(line);
        }
        Line end = new Line(first_coordinate.getX(),first_coordinate.getY(),first_coordinate.getX(),first_coordinate.getY());
        end.setStroke(rgb(0,0,0,0));
        end.setAccessibleRole(AccessibleRole.RADIO_MENU_ITEM);
        end.setStrokeWidth(12);
        lines.add(end);

        this.gui.addAll(lines);

        Color color = Color.WHITE;
        if (this.number.startsWith("1")) color = Color.INDIANRED;
        if (this.number.startsWith("2")) color = Color.DARKGREEN;
        if (this.number.startsWith("3")) color = Color.ORANGE;
        if (this.number.startsWith("4")) color = Color.BLUE;
        this.gui.add(new Circle(position.getX(), position.getY(), 12, Color.WHITE));
        this.gui.add(new Circle(position.getX(), position.getY(), 10, color));
        Text text = new Text(position.getX()-6, position.getY()+5, this.path.getNumber());
        if (color == Color.DARKGREEN || color == Color.BLUE) text.setFill(Color.WHITE);
        this.gui.add(text);
        Circle circle=new Circle(position.getX(), position.getY(), 12, rgb(0,0,0,0));
        this.gui.add(circle);
        circle.setAccessibleRole(AccessibleRole.RADIO_BUTTON);
    }

    private void setNumber() {
        this.number = this.path.getNumber();
    }

    private void setCurrentStreet() {
        Street lastStreet = null;
        Street nextStreet;
        try {
          lastStreet = this.getPath().getStopList().get(stopsPassed -1 ).getStreet();
          nextStreet = this.getPath().getStopList().get(stopsPassed ).getStreet();
        }
        catch (IndexOutOfBoundsException e) {
            this.currentStreet = lastStreet;
            return;
        }

        if (path.getDistance(lastStreet.getStart(), this.getPosition()) +
                path.getDistance(this.getPosition(), lastStreet.getEnd()) ==
                path.getDistance(lastStreet.getStart(), lastStreet.getEnd())) {
            this.currentStreet = lastStreet;
        }
        else if (path.getDistance(nextStreet.getStart(), this.getPosition()) +
                path.getDistance(this.getPosition(), nextStreet.getEnd()) ==
                path.getDistance(nextStreet.getStart(), nextStreet.getEnd())) {
            this.currentStreet = nextStreet;
        }
        else {
            this.currentStreet = nextStreet;

        }
    }

    private void updateDelay(LocalTime currentTime) {
        long realTimeOnRoad = (Math.abs(Duration.between(currentTime, startTime).toMillis()) / 1000) - 1;
        long scheduledTime = 0;
        for (int i = 0; i < stopsPassed - 1; i++) {
            try {
                scheduledTime += this.getStopsTimes().get(i);
            }
            catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        this.currentDelay = Math.abs((int) (realTimeOnRoad - scheduledTime));

    }


    // move the bus
    private void move(Coordinate c) {
        for (Shape s : gui) {
            if (s instanceof Line) continue;
            s.setTranslateX((c.getX() - position.getX()) + s.getTranslateX());
            s.setTranslateY((c.getY() - position.getY()) + s.getTranslateY());
        }
    }

    // update according to distance and speed
    @Override
    public void update(LocalTime l) {
        Platform.runLater(() -> {
            setCurrentStreet();
            double streetTraffic = 1.0;
            if (currentStreet != null)
                streetTraffic = currentStreet.getUrovenzatizeni();

            List<Coordinate> stops = new ArrayList<>();
            for (Stop s : path.getStopList()) {
                stops.add(s.getCoordinates());
            }
            if (stops.contains(position)) {
                updateDelay(l);
            }

            distance += ((int) (speed / streetTraffic));
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

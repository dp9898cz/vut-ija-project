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

/**
 * Vehicle class represent vehicle
 * @author Daniel Pátek (xpatek08)
 * @version %I%, %G%
 */
@JsonDeserialize(converter = Vehicle.VehicleConstruct.class)
public class Vehicle implements Drawable, TimerMapUpdate {
    /**
     * Current position
     */
    private Coordinate position;
    /**
     * Default speed of vehicle
     */
    private double speed;
    /**
     * Corresponding path
     */
    private Path path;
    /**
     * List of times at stops
     */
    private List<Integer> stopsTimes;
    /**
     * How often does the vehicle go
     */
    private int goEveryXMinute;
    /**
     * Vehicle start offset (in minutes)
     */
    private int startMinute;

    /**
     * How many stops have passed already
     */
    @JsonIgnore
    private int stopsPassed = 0;

    /**
     * Last known stop
     */
    @JsonIgnore
    private Coordinate lastStop;

    /**
     * Time when the vehicle started.
     */
    @JsonIgnore
    private LocalTime startTime = null; // vehicle's time of start

    /**
     * Current distance of vehicle
     */
    @JsonIgnore
    private double distance = 0;

    /**
     * List of items to display
     */
    @JsonIgnore
    private List<Shape> gui;

    /**
     * Number of the line (bus)
     */
    @JsonIgnore
    private String number = "";

    /**
     * Current street where vehicle is atm
     */
    @JsonIgnore
    private Street currentStreet;

    /**
     * Current delay of vehicle
     */
    @JsonIgnore
    private int currentDelay = 0;


    /**
     * Default constructor for Jackson purposes
     */
    private Vehicle(){}

    /**
     * Vehicle constructor (calls setNumber() and setGui())
     * @param position Starting position
     * @param speed Vehicle default speed
     * @param path Path (list of Coordinates) including crossroads
     * @param times List of timetable times is seconds (how long does it take to go to next stop)
     * @param goEveryXMinute How often does the vehicle go (in minutes, etc. 3 means every 3 minutes)
     * @param startMinute Start offset (in minutes, etc. 5 means the first bus goes :05 )
     */
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


    /**
     * Get string with all information about the vehicle
     * @return Information about vehicle
     */
    @JsonIgnore
    public String getLineInfo() {
        String completed = "";
        completed += "Linka cislo " + number + "\n\n";
        // now I have all stops in variable path.stopList
        for (int i = 0; i < this.getPath().getStopList().size(); i++) {
            String line = "";
            if (stopsPassed == i) {
                line += "Aktualni poloha\n";
            }
            LocalTime time = this.getStartTime();
            time = time.truncatedTo(ChronoUnit.SECONDS);
            for (int j = 0; j < i; j++) {
                time = time.plusSeconds(this.getStopsTimes().get(j));
            }
            line += "\t" + time.toString(); //get scheduled time
            if (i == 0) line += ":00";
            line += "\t";
            line += this.getPath().getStopList().get(i).getName(); // stop name
            line += "\n";
            completed = completed.concat(line);
        }
        completed += "\nZpozdeni: ";
        completed += Integer.toString(currentDelay); // delay
        completed += " s";

        return completed;
    }

    /**
     * Get path length in seconds
     * @return Length of path in seconds
     */
    @JsonIgnore
    public long getPathLengthInSeconds() {
        long total = 0;
        for (Integer i : stopsTimes) {
            total += i;
        }
        return total;
    }

    /**
     * Find out how many stops has the vehicle passed
     * @param currentTimePassed Current time on the road
     * @return Number of stops passed
     */
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

    /**
     * Find out last known stop
     * @param currentTimePassed Current time on the road
     * @return Coordinates of the last stop
     */
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


    /**
     * Get elements of GUI
     * @return GUI elements
     */
    @Override
    public List<Shape> getGui() {
        return gui;
    }

    /**
     * Set up GUI elements
     */
    private void setGui() {
        this.gui = new ArrayList<>();

        //Lines for lines
        List <Coordinate> list = path.getPath();
        Iterator<Coordinate> iterator = list.iterator();
        Coordinate first_coordinate = list.get(0);
        ArrayList <Line> lines = new ArrayList<>();
        Line start = new Line(first_coordinate.getX() +9,first_coordinate.getY()-9,first_coordinate.getX()+9,first_coordinate.getY()-9);
        start.setStroke(rgb(0,0,0,0));
        start.setAccessibleRole(AccessibleRole.RADIO_MENU_ITEM);
        start.setStrokeWidth(12);
        lines.add(start);

        while(iterator.hasNext()) {
            Coordinate coordinate_end = iterator.next();
            Line line = new Line(first_coordinate.getX() + 10, first_coordinate.getY() - 10, coordinate_end.getX() + 10, coordinate_end.getY() - 10);
            line.setStrokeWidth(5);
            line.setAccessibleRole(AccessibleRole.RADIO_MENU_ITEM);
            line.setStroke(rgb(0,0,0,0));
            first_coordinate = coordinate_end;
            lines.add(line);
        }

        Line end = new Line(first_coordinate.getX() +10,first_coordinate.getY()+10,first_coordinate.getX()+10,first_coordinate.getY()+10);
        end.setStroke(rgb(0,0,0,0));
        end.setAccessibleRole(AccessibleRole.RADIO_MENU_ITEM);
        end.setStrokeWidth(12);
        lines.add(end);

        this.gui.addAll(lines);

        Color color = Color.INDIANRED;
        if (this.number.startsWith("2")) color = Color.DARKGREEN;
        if (this.number.startsWith("3")) color = Color.ORANGE;
        if (this.number.startsWith("4")) color = Color.BLUE;

        this.gui.add(new Circle(position.getX(), position.getY(), 12, Color.WHITE));    // outer circle
        this.gui.add(new Circle(position.getX(), position.getY(), 10, color));          // inner circle
        Text text = new Text(position.getX()-6, position.getY()+5, this.path.getNumber());
        if (color == Color.DARKGREEN || color == Color.BLUE) text.setFill(Color.WHITE);
        this.gui.add(text);
        Circle circle=new Circle(position.getX(), position.getY(), 12, rgb(0,0,0,0));
        this.gui.add(circle);
        circle.setAccessibleRole(AccessibleRole.RADIO_BUTTON);
    }

    /**
     * Find out and set vehicle's current street
     */
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

        // count the position
        // if distance(A,B) == distance(A,C) + distance(B,C) -> point C is on the line AB
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

    /**
     * Update current delay
     * @param currentTime Current time on the road
     */
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


    /**
     * Move the vehicle elements to given coordinates
     * @param c Coordinates of destination
     */
    private void move(Coordinate c) {
        for (Shape s : gui) {
            if (s instanceof Line) continue; // ignore lines
            s.setTranslateX((c.getX() - position.getX()) + s.getTranslateX());
            s.setTranslateY((c.getY() - position.getY()) + s.getTranslateY());
        }
    }

    /**
     * Update vehicle status and GUI elements
     * @param l Current time
     */
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

            distance += ((int) (speed / streetTraffic)); // get distance

            Coordinate c;

            if (distance >= path.getPathDistance()) {
                // set the last coordinates
                c = path.getPath().get(path.getPath().size() - 1);
            }
            else {
                // get new coordinates
                c = path.getDistanceCoordinate(distance, this);
            }
            move(c);
            position = c;
            }
        );
    }

    /**
     * {@link Vehicle#position}
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * {@link Vehicle#speed}
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * {@link Vehicle#path}
     */
    public Path getPath() {
        return path;
    }

    /**
     * {@link Vehicle#stopsTimes}
     */
    public List<Integer> getStopsTimes() {
        return stopsTimes;
    }

    /**
     * {@link Vehicle#goEveryXMinute}
     */
    public int getGoEveryXMinute() {
        return goEveryXMinute;
    }

    /**
     * {@link Vehicle#startMinute}
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * {@link Vehicle#stopsPassed}
     */
    public int getStopsPassed() {
        return stopsPassed;
    }

    /**
     * {@link Vehicle#stopsPassed}
     */
    public void setStopsPassed(int stopsPassed) {
        this.stopsPassed = stopsPassed;
    }

    /**
     * {@link Vehicle#startTime}
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * {@link Vehicle#startTime}
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * {@link Vehicle#lastStop}
     */
    public Coordinate getLastStop() {
        return lastStop;
    }

    /**
     * {@link Vehicle#lastStop}
     */
    public void setLastStop(Coordinate lastStop) {
        this.lastStop = lastStop;
    }

    /**
     * {@link Vehicle#number}
     */
    public String getNumber() {
        return number;
    }

    /**
     * {@link Vehicle#number}
     */
    private void setNumber() {
        this.number = this.path.getNumber();
    }

    /**
     * {@link Vehicle#distance}
     */
    public double getDistance() {
        return distance;
    }

    /**
     * {@link Vehicle#distance}
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "position=" + position +
                ", speed=" + speed +
                ", path=" + path +
                '}';
    }


    /**
     * Static class for Jackson
     * Used for initialisation of number and gui elements
     * @author Daniel Pátek (xpatek08)
     * @version %I%, %G%
     */
    static class VehicleConstruct extends StdConverter<Vehicle,Vehicle> {

        /**
         * Initialize Vehicle's gui and number
         * @param value Vehicle object
         * @return Initialized Vehicle object
         */
        @Override
        public Vehicle convert(Vehicle value) {
            value.setNumber();
            value.setGui();
            return value;
        }
    }
}

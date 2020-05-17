package ija_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.AccessibleRole;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static javafx.scene.paint.Color.rgb;

/**
 * Controller class for handling application and timer
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version 1.0
 */
public class Controller {
    /**
     * Timer textfield
     */
    @FXML
    public TextField Timer;

    /**
     * Text area for information about vehicle
     */
    @FXML
    public TextArea textArea;

    /**
     * Hours input field
     */
    @FXML
    public TextField Hours;

    /**
     * Minutes input field
     */
    @FXML
    public TextField Minutes;

    /**
     * Seconds input field
     */
    @FXML
    public TextField Seconds;

    /**
     * Actual Timer
     */
    @FXML
    private Timer timer;

    /**
     * Textfield for scaling time
     */
    @FXML
    private TextField scaleTextField;

    /**
     * Mapcontent pane with GUI elements
     */
    @FXML
    private Pane mapContent;

    /**
     * Local time updating
     */
    private LocalTime time = LocalTime.now();

    /**
     * Start local time not updating
     */
    private LocalTime timeTemp = LocalTime.now();

    /**
     * List of all GUI elements
     */
    private final List<TimerMapUpdate> elementsUpdate = new ArrayList<>();

    /**
     * List of all Vehicles
     */
    private final List<Vehicle> vehicles = new ArrayList<>();

    /**
     * List of all coordinates
     */
    private final List<Coordinate> stops = new ArrayList<>();
    /**
     * List of all Stops
     */
    private final List<Stop> allStops = new ArrayList<>();

    /**
     * MAX Constant for zooming
     */
    private static final double MAX_SCALE = 10;

    /**
     * MIN Constant for zooming
     */
    private static final double MIN_SCALE = -6;

    /**
     * Zoomhandler counter
     */
    public  double zoomhandler = 0;


    /**
     * Get start time of vehicle in the past
     * @param v Vehicle
     * @return Local time of vehicle's start time
     */
    private LocalTime getPastStartTime(Vehicle v) {
        LocalTime fakeTime = time.truncatedTo(ChronoUnit.MINUTES);
        for (int i = 0; i < 4; i++) {
            fakeTime = fakeTime.minusMinutes(i);
            if (((fakeTime.getMinute() == v.getStartMinute()) || (((fakeTime.getMinute() - v.getStartMinute()) % v.getGoEveryXMinute()) == 0))) {
                return fakeTime;
            }
        }
        return null;
    }

    /**
     * Generate vehicles currently running and put them to GUI
     */
    private void generateVehiclesOnTheRoad() {
        for (Vehicle v : vehicles) {
            LocalTime startTime = getPastStartTime(v); // start time
            if (startTime == null) return;
            long timePassedSinceStart = Math.abs(Duration.between(time, startTime).toMillis()) / 1000; // seconds on the run
            long pathLengthInSeconds = v.getPathLengthInSeconds();
            double driven = ((double) timePassedSinceStart) / pathLengthInSeconds; // % driven length
            double distanceDriven = driven * v.getPath().getPathDistance(); // distance
            Coordinate currentPos = v.getPath().getDistanceCoordinate(distanceDriven, v); // current position
            Integer stopsPassed = v.findOutHowManyStopsPassedAlready((int) timePassedSinceStart);
            Coordinate LastStop = v.findOutLastStop((int) timePassedSinceStart);

            //generate
            Vehicle vv = new Vehicle(currentPos, v.getSpeed(), v.getPath(), v.getStopsTimes(), v.getGoEveryXMinute(), v.getStartMinute());
            vv.setStopsPassed(stopsPassed);
            vv.setLastStop(LastStop);
            vv.setStartTime(startTime);
            vv.setDistance(distanceDriven);

            //add it to gui
            setActions(vv.getGui(), vv);
            elementsUpdate.add(vv);
            mapContent.getChildren().addAll(vv.getGui());
        }
    }


    /**
     * Zoom handler function
     * @param e ScrollEvent
     */
    @FXML
    private void onScroll(ScrollEvent e) {
        e.consume();
        // set zoom variable
        double zoom = e.getDeltaY();

        if (zoom > 0) {
            zoomhandler = zoomhandler+1;
            zoom = 1.1;
        }else {
            zoomhandler = zoomhandler-1;
            zoom = 0.9;
        }
        if(zoomhandler>MAX_SCALE){
            zoomhandler=MAX_SCALE;
            zoom =1.0;
        }
        if(zoomhandler<MIN_SCALE){
            zoomhandler=MIN_SCALE;
            zoom =1.0;
        }
        // change scale of the Pane
        mapContent.setScaleX(zoom * mapContent.getScaleX());
        mapContent.setScaleY(zoom * mapContent.getScaleY());
        mapContent.layout();
    }


    /**
     * Handler for time scale change
     */
    @FXML
    private void onScaleChange() {
        try {
            float scale = Float.parseFloat(scaleTextField.getText());
            if (scale <= 0 || scale > 10) showNumberAlert();
            timer.cancel();
            timer(scale);
        }
        catch (NumberFormatException e) {
            showNumberAlert();
        }
    }

    /**
     * Time change handler
     */
    @FXML
    private void onTimeChange() {
        try {
            int hours = Integer.parseInt(Hours.getText());
            int minutes = Integer.parseInt(Minutes.getText());
            int seconds = Integer.parseInt(Seconds.getText());
            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 60 || seconds < 0 || seconds > 60) {
                showTimeNumberAlert();
                return;
            }
            // get time
            LocalTime time = LocalTime.of(hours, minutes, seconds);
            System.out.println(time);
            timer.cancel();

            // remove current elements
            for (TimerMapUpdate s : elementsUpdate) {
                Vehicle veh = (Vehicle) s;
                for (Shape h : veh.getGui()) {
                    mapContent.getChildren().remove(h);
                }
            }

            this.time = time;
            this.timeTemp = time;

            // generate new vehicles
            generateVehiclesOnTheRoad();

            timer(1);

        }
        catch (NumberFormatException e) {
            showTimeNumberAlert();
        }
    }


    /**
     * Show alert for bad number entered
     */
    public void showNumberAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Špatně zadané číslo. Možnosti jsou od 1 do 10.");
        alert.showAndWait();
    }

    /**
     * Show alert for bad number entered for time
     */
    public void showTimeNumberAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Špatně zadaný čas. Možnosti jsou od 0 do 24 pro hodiny, od 0 do 59 pro minuty a sekundy.");
        alert.showAndWait();
    }

    /**
     * Generate vehicle and put it to the GUI
     * @param a Vehicle to generate
     */
    private void generateVehicle(Vehicle a) {
            Vehicle v = new Vehicle(a.getPath().getPath().get(0), a.getSpeed(), a.getPath(), a.getStopsTimes(), a.getGoEveryXMinute(), a.getStartMinute());
            setActions(v.getGui(), v);
            this.elementsUpdate.add(v);
            this.mapContent.getChildren().addAll(v.getGui());
    }

    /**
     * Generate opposite vehicle and put it to the GUI
     * @param v Vehicle
     */
    private void generateOppositeVehicle(Vehicle v) {
        // reverse lists
        List<Coordinate> oppositePath = new ArrayList<>(v.getPath().getPath());
        List<Integer> oppositeStopTimes = new ArrayList<>(v.getStopsTimes());
        Collections.reverse(oppositePath);
        Collections.reverse(oppositeStopTimes);

        // change number
        String pathName = "";
        pathName = pathName.concat(v.getPath().getNumber());
        String newNumber = "";
        switch (pathName) {
            case "11":
                newNumber = "12";
                break;
            case "22":
                newNumber = "23";
                break;
            case "33":
                newNumber = "34";
                break;
            case "44":
                newNumber = "45";
                break;
        }
        // create new vehicle
        Vehicle new_v = new Vehicle(oppositePath.get(0), v.getSpeed(),
                new Path(oppositePath, newNumber),
                oppositeStopTimes, v.getGoEveryXMinute(), v.getStartMinute() + 1);
        vehicles.add(new_v);
    }


    /**
     * Destroy vehicle
     * @param v Vehicle object
     */
    private void destroyBus(Vehicle v) {
        this.elementsUpdate.remove(v);
        this.mapContent.getChildren().removeAll(v.getGui());
    }


    /**
     * Set click actions on the Vehicle objects
     * @param drawables List of all drawables in gui
     * @param v Vehicle object
     */
    void setActions(List<Shape> drawables, Vehicle v) {
        for (Shape s : drawables) {
            // get role (right circle)
            AccessibleRole role = s.getAccessibleRole();
            if (s instanceof Circle) {
                s.setOnMousePressed(t -> {
                    String toWrite = v.getLineInfo();
                    textArea.clear();
                    textArea.appendText(toWrite);
                    deleteVisibleLines();
                    for (Shape ss: v.getGui() ) {
                        if (ss instanceof Line) ss.setStroke(rgb(220,0,0,1));
                    }
                });
            }
        }
    }

    /**
     * Set click actions on the grass (cancel click)
     * @param p Current Pane
     */
    private void setClickOnGrass(Pane p) {
        p.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getTarget() == mapContent) {
                    // delete text in window
                    textArea.clear();
                    deleteVisibleLines();
                }
            }
        });
    }

    /**
     * Delete all visible lines from GUI
     */
    private void deleteVisibleLines() {
        // delete lines which are visible
        for (TimerMapUpdate s : elementsUpdate) {
            Vehicle veh = (Vehicle) s;
            for (Shape h : veh.getGui()) {
                AccessibleRole role = h.getAccessibleRole();
                if (role == AccessibleRole.RADIO_MENU_ITEM) {
                    h.setStroke(rgb(220,0,0,0));
                }
            }
        }
    }

    /**
     * Set elements on the start
     * @param elements List of elements drawables
     */
    public void setElements(List<Drawable> elements) {
        for (Drawable d : elements) {
            if (d.getClass().equals(Vehicle.class)) {
                //elementsUpdate.add((TimerMapUpdate) d);
                vehicles.add((Vehicle) d);
		        generateOppositeVehicle((Vehicle) d);
            }
            else {
                if (d.getClass().equals(Stop.class)) {
                    stops.add(((Stop) d).getCoordinates());
                    allStops.add((Stop) d);
                }
                mapContent.getChildren().addAll(d.getGui());
            }
        }

        // add stops to the all vehicles paths
        for (Vehicle v: vehicles) {
            List<Coordinate> allPointsVehicle = v.getPath().getPath();
            for (Coordinate c : allPointsVehicle) {
                //allPoints.add(p.getCoordinates());
                for (Stop p : allStops) {
                    if (p.getCoordinates().equals(c)) {
                        v.getPath().getStopList().add(p);
                    }
                }
            }

        }
        // there generate vehicles on the road
        generateVehiclesOnTheRoad();

        //set click on grass
        setClickOnGrass(mapContent);
    }

    /**
     * Timer function
     * @param scale Timer scale to set
     */
    public void timer(float scale) {
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                time = time.plusSeconds(1);

                // another bus generator
                for (Vehicle v : vehicles) {
                    Platform.runLater(() -> {
                        if ((time.getSecond() == 0) && ((time.getMinute() == v.getStartMinute()) || (((time.getMinute() - v.getStartMinute()) % v.getGoEveryXMinute()) == 0))) {
                            generateVehicle(v);
                        }
                    });
                }


                // timer LOOP
                Platform.runLater(() -> {

                    List<Vehicle> toDelete = new ArrayList<>();

                    for (TimerMapUpdate u : elementsUpdate) {

                        // variables for timer update
                        Vehicle v = (Vehicle) u;
                        List<Coordinate> p = v.getPath().getPath();
                        Coordinate pos = v.getPosition();
                        LocalTime timeStart = v.getStartTime();
                        long timePassedSinceStart = 0;
                        long scheduledTime = 0;



                        for (int i = 0; i < v.getStopsPassed() - 1; i++) {
                            try {
                                scheduledTime += v.getStopsTimes().get(i);
                            }
                            catch (IndexOutOfBoundsException ignored) {}

                        }

                        // set start time on every vehicle
                        if (timeStart == null) {
                            v.setStartTime(time.minusSeconds(1));
                            v.setLastStop(v.getPosition());
                            v.setStopsPassed(1);
                        }

                        // set the time passed variable
                        if (timeStart != null) {
                            timePassedSinceStart = Math.abs(Duration.between(timeStart, time).toMillis()) / 1000;
                        }

                        // bus got to the Stop -> change lastStop (and quit for next calculation of scheduled Time)
                        if (stops.contains(pos) && v.getLastStop() != pos && timePassedSinceStart != 0) {
                            v.setLastStop(pos);
                            v.setStopsPassed(v.getStopsPassed() + 1);
                            continue;
                        }

                        // keep the bus on the Stop, before releasing it after time schedule is ok
                        if (stops.contains(pos) && v.getLastStop() == pos && scheduledTime >= timePassedSinceStart && scheduledTime != 0) {
                            continue;
                        }

                        // update the GUI
                        u.update(time);

                        // delete the bus if its in the end
                        if (pos.equals(p.get(p.size() - 1)) && v.getDistance() >= v.getPath().getPathDistance()) {
                            toDelete.add(v);
                        }
                    }
                    for (Vehicle v : toDelete) {
                        destroyBus(v);
                    }

                    Timer.setText(time.truncatedTo(ChronoUnit.SECONDS).toString());
                    
                }
                );
            }
        }, 0, (long) (1000 / scale));
    }


    /**
     * Show help
     */
    @FXML
    public void onHelp() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION,"Simulace městské hromadné dopravy\n Po kliknutí na vozidlo se zobrazí trasa a podrobnosti spoje.\n Můžete měnit rychlost simulace a také nastavit výchozí čas.");
        alert.showAndWait();
    }

    /**
     * Close app
     */
    @FXML
    public void onCloseApp() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Start app
     */
    @FXML
    private void OnStart(){
        timer.cancel();
        timer(1);
    }

    /**
     * Pause app
     */
    @FXML
    private void OnPause(){
        timer.cancel();
    }

    /**
     * Primary stage
     */
    private Stage primaryStage;

    /**
     * Set primary stage
     * @param stage Primary stage
     */
    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Maximazed window boolean
     */
    private boolean maximized = false;

    /**
     * Maximize handler
     * @param actionEvent actionEvent
     */
    public void onMinimaze(ActionEvent actionEvent) {
        if (maximized == false) {
            primaryStage.setMaximized(true);
            maximized = true;
        } else {
            primaryStage.setMaximized(false);
            maximized = false;
        }

    }
}

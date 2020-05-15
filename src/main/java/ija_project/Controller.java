package ija_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static javafx.scene.paint.Color.rgb;

public class Controller {
    @FXML
    public TextField Timer;
    @FXML
    public TextField Timer_update;
    @FXML
    public TextField searchbox ;
    @FXML
    public Button searchbutton;
    @FXML
    public TextArea textArea;

    @FXML
    private Timer timer;

    private LocalTime time = LocalTime.now();
    private final LocalTime timeTemp = LocalTime.now();

    private final List<TimerMapUpdate> elementsUpdate = new ArrayList<>();

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Coordinate> stops = new ArrayList<>();
    private final List<Stop> allStops = new ArrayList<>();
    @FXML
    private TextField scaleTextField;
    @FXML
    private Pane mapContent;
    private static final double MAX_SCALE = 10;
    private static final double MIN_SCALE = -6;
    public  double zoomhandler = 0;

    //TODO

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


    @FXML
    private void searchAction() {
        float search = Float.parseFloat(searchbox.getText());
    }


    //Function for zooming
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
    @FXML
    private void onTimeStart(){
            timer(1);
    }
    @FXML
    private void onTimeStop(){
        timer.cancel();
    }
    @FXML
    private void OnTimeReset(){

    }
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

    public void showNumberAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Špatně zadané číslo. Možnosti jsou od 1 do 10.");
        alert.showAndWait();
    }

    private void generateVehicle(Vehicle a) {
            Vehicle v = new Vehicle(a.getPath().getPath().get(0), a.getSpeed(), a.getPath(), a.getStopsTimes(), a.getGoEveryXMinute(), a.getStartMinute());
            setActions(v.getGui(), v);
            this.elementsUpdate.add(v);
            this.mapContent.getChildren().addAll(v.getGui());
    }

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


    private void destroyBus(Vehicle v) {
        this.elementsUpdate.remove(v);
        this.mapContent.getChildren().removeAll(v.getGui());
    }


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
        for (Stop s : allStops) {
            for (Vehicle v : vehicles) {
                if (v.getPath().getPath().contains(s.getCoordinates())) {
                    v.getPath().getStopList().add(s);
                }
            }
        }

        // there generate vehicles on the road
        generateVehiclesOnTheRoad();

        //set click on grass
        setClickOnGrass(mapContent);
    }


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
                            scheduledTime += v.getStopsTimes().get(i);
                        }

                        // set start time on every vehicle
                        if (timeStart == null) {
                            v.setStartTime(time.minusSeconds(1));
                            v.setLastStop(v.getPosition());
                            v.setStopsPassed(1);
                        }

                        // set the time passed variable
                        if (timeStart != null) {
                            if (timeStart.isBefore(timeTemp)) {
                                    scheduledTime += v.getStopsTimes().get(0);
                            }
                            timePassedSinceStart = Math.abs(Duration.between(timeStart, time).toMillis()) / 1000;
                        }

                        //DEBUG todo
                        if (v.getNumber().equals("44")) {
                            //System.out.println(String.format("%s: passed: %d scheduled: %d", v.getNumber(), timePassedSinceStart, scheduledTime));
                            //System.out.println(getLineInfo(v));
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
                    int s = Math.toIntExact(Math.abs(Duration.between(timeTemp, time).toMillis()) / 1000);
                    Timer_update.setText(Integer.toString(s));
                }
                );
            }
        }, 0, (long) (1000 / scale));
    }



    @FXML
    public void clickHelp() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION,"Tohle je help");
        alert.showAndWait();
    }
    @FXML
    public void onCloseapp() {
        Platform.exit();
        System.exit(0);
    }

}

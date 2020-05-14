package ija_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Controller {
    @FXML
    public TextField Timer;
    @FXML
    public TextField Timer_update;
    @FXML
    public TextField searchbox ;
    @FXML
    public Button searchbutton;
    public static TextArea textArea;
    @FXML
    private Timer timer;
    private LocalTime time = LocalTime.now();
    private LocalTime timeTemp = LocalTime.now();

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

    @FXML
    private void searchAction() {
        float search = Float.parseFloat(searchbox.getText());
    }
    //Function that should print info about path
    @FXML
    public static void PrintigLineInfo(String string) {
        //textArea.setText(string);
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
        System.out.println(zoom);
        System.out.println(zoomhandler);
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
        //mapContent.getChildren().addAll(new_v.getGui());
        //elementsUpdate.add(new_v);
    }


    private void destroyBus(Vehicle v) {
        this.elementsUpdate.remove(v);
        this.mapContent.getChildren().removeAll(v.getGui());
    }


    public String getLineInfo(Vehicle v) {
        String completed = "";
        // first get list of stops
        List<Stop> stopsList = new ArrayList<>();
        for (Coordinate c : v.getPath().getPath()) {
            // decide whether the stop is real stop
            if (stops.contains(c)) stopsList.add(allStops.get(stops.indexOf(c)));
        }

        // now I have all stops in variable stopList
        for (int i = 1; i < stopsList.size(); i++) {
            String line = Integer.toString(i);
            line += ".\t";
            LocalTime time = v.getStartTime();
            time = time.truncatedTo(ChronoUnit.SECONDS);
            for (int j = 0; j < i; j++) {
                time = time.plusSeconds(v.getStopsTimes().get(j));
            }
            line += time.toString(); //get scheduled time
            line += "\t";
            line += "Nejake jmeno zastavky";
            line += "\n";
            completed = completed.concat(line);
        }
        return completed;
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
    }

    int counter = 0;
    public void timer(float scale) {
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                time = time.plusSeconds(1);

                // todo DEBUG
                System.out.println(time);

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

                });
                Platform.runLater(() -> {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    Timer.setText(dtf.format(now));
                    counter++;
                    String s=String.valueOf(counter);
                    Timer_update.setText(s);
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

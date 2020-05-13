package ija_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.time.LocalTime;
import java.util.*;

public class Controller {

    private Timer timer;
    private final List<TimerMapUpdate> elementsUpdate = new ArrayList<>();
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Coordinate> stops = new ArrayList<>();
    private LocalTime time = LocalTime.now();
    private LocalTime startTime = LocalTime.now();
    private int minuteCounter = 0;

    @FXML
    private TextField scaleTextField;

    @FXML
    private Pane mapContent;
    private static final double MAX_SCALE = 12;
    private static final double MIN_SCALE = -10;
    public  double zoomhandler = 0;
    @FXML
    private void onScroll(ScrollEvent e) {
        e.consume();
        // set zoom variable
        double zoom = e.getDeltaY();

        if (zoom > 0) {
            zoom = 1.1;
            zoomhandler = zoomhandler+1;
        } else {
            zoom = 0.9;
            zoomhandler = zoomhandler-1;
        }
        if(zoomhandler>MAX_SCALE){
            zoomhandler=MAX_SCALE;
            zoom =1;
        }
        if(zoomhandler<MIN_SCALE){
            zoomhandler=MIN_SCALE;
            zoom =1;
        }
        // change scale of the Pane
        mapContent.setScaleX(zoom * mapContent.getScaleX());
        mapContent.setScaleY(zoom * mapContent.getScaleY());
        mapContent.layout();

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

    private void generateVehicles() {
        for (Vehicle a: vehicles) {
            Vehicle v = new Vehicle(a.getPath().getPath().get(0), a.getSpeed(), a.getPath());
            this.elementsUpdate.add((TimerMapUpdate) v);
            this.mapContent.getChildren().addAll(v.getGui());
        }
    }

    private void destroyBus(Vehicle v) {
        this.elementsUpdate.remove(v);
        this.mapContent.getChildren().removeAll(v.getGui());
    }

    public void setElements(List<Drawable> elements) {
        for (Drawable d : elements) {
            mapContent.getChildren().addAll(d.getGui());
            if (d instanceof TimerMapUpdate) {
                elementsUpdate.add((TimerMapUpdate) d);
                vehicles.add((Vehicle) d);
            }
            if (d.getClass().equals(Stop.class)) {
                stops.add(((Stop) d).getCoordinates());
            }
        }
    }

    public void timer(float scale) {
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time = time.plusSeconds(1);
                minuteCounter++;

                // every minute bus generator
                if (minuteCounter == 180) {
                    Platform.runLater(() -> {
                        generateVehicles();
                    });
                    minuteCounter = 0;
                }

                // variables for timer update
                Vehicle v;
                List<Coordinate> p;
                Coordinate pos;
                int cntr = 0;

                for (TimerMapUpdate u : elementsUpdate) {
                    v = (Vehicle) u;
                    p = v.getPath().getPath();
                    pos = v.getPosition();
                    cntr = v.getStopWaitCounter();
                    // pokud je bus na zastavce
                    if (stops.contains(pos) && cntr > 0) {
                        v.setStopWaitCounter(cntr - 1);
                        continue;
                    }
                    u.update(time);
                    if (pos.equals(p.get(p.size() - 1)) && v.getDistance() >= v.getPath().getPathDistance()) {
                        //System.out.println("NOW NOW NOW");
                        Vehicle finalV = v;
                        Platform.runLater(() -> {
                            destroyBus(finalV);
                        });
                    }
                    if (cntr != 5) {
                        v.setStopWaitCounter(5);
                    }
                }
            }
        }, 1000, (long) (1000 / scale));
    }
    @FXML
    public void clickHelp(javafx.event.ActionEvent actionEvent) {
        Alert alert=new Alert(Alert.AlertType.INFORMATION,"Tohle je help");
        alert.showAndWait();
    }
    @FXML
    public void onCloseapp(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }
}

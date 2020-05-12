package ija_project;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    private Timer timer;
    private final List<TimerMapUpdate> elementsUpdate = new ArrayList<>();
    private LocalTime time = LocalTime.now();

    @FXML
    private TextField scaleTextField;

    @FXML
    private Pane mapContent;

    @FXML
    private void onScroll(ScrollEvent e) {
        e.consume();
        // set zoom variable
        double zoom = e.getDeltaY();
        if (zoom > 0) {
            zoom = 1.1;
        } else {
            zoom = 0.9;
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

    public void setElements(List<Drawable> elements) {
        for (Drawable d : elements) {
            mapContent.getChildren().addAll(d.getGui());
            if (d instanceof TimerMapUpdate) {
                elementsUpdate.add((TimerMapUpdate) d);
            }
        }
    }

    public void timer(float scale) {
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time = time.plusSeconds(1);
                for (TimerMapUpdate u : elementsUpdate) {
                    u.update(time);
                }
            }
        }, 1000, (long) (1000 / scale));
    }
}

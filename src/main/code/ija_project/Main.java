package ija_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version 1.0
 */
public class Main extends Application {
    /**
     * xOffset
     */
    private double xOffset = 0;

    /**
     * yOffset
     */
    private double yOffset = 0;

    /**
     * Start main function
     * @param primaryStage primaryStage
     * @throws Exception Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // load the scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        BorderPane root = loader.load();
        primaryStage.setTitle("Public Transport");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

        primaryStage.show();

        // get the controller to use timer and import data
        Controller controller = loader.getController();
        controller.setStage(primaryStage);
        YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(factory);

        // serialization
        //mapper.writeValue(new File("test.yml"), data);

        // deserialization
        Data data1 = mapper.readValue(new File("data/data.yml"), Data.class);

        // import elements to gui
        List<Drawable> elements = new ArrayList<>(data1.getStreets());
        elements.addAll(new ArrayList<>(data1.getStops()));
        elements.addAll(new ArrayList<>(data1.getVehicles()));
        controller.setElements(elements);

        // start the timer with the scale 1
        controller.timer(1);
    }


    /**
     * Main
     * @param args params
     */
    public static void main(String[] args) {
        launch(args);
    }
}

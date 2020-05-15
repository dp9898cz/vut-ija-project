package ija_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // load the scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        BorderPane root = loader.load();
        primaryStage.setTitle("Public Transport");
        primaryStage.setScene(new Scene(root, 800, 600,Color.GREEN));
        primaryStage.show();

        // get the controller to use timer and import data
        Controller controller = loader.getController();

        YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(factory);

        // serialization
        //mapper.writeValue(new File("test.yml"), data);

        // deserialization
        Data data1 = mapper.readValue(new File("data.yml"), Data.class);
//        System.out.println(data1.getCoordinates());
//        System.out.println(data1.getStops());
//        System.out.println(data1.getStreets());
//        System.out.println(data1.getVehicles());

        // import elements to gui
        List<Drawable> elements = new ArrayList<>(data1.getStreets());
        elements.addAll(new ArrayList<>(data1.getStops()));
        elements.addAll(new ArrayList<>(data1.getVehicles()));
        controller.setElements(elements);

        // start the timer with the scale 1
        controller.timer(1);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

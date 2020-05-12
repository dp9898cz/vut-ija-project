package ija_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

        // sample data
//        List<Coordinate> coordinates = new ArrayList<>();
//        List<Vehicle> vehicles = new ArrayList<>();
//        coordinates.add(new Coordinate(100, 100));
//
//        vehicles.add(new Vehicle(coordinates.get(0), 10, new Path(Arrays.asList(
//                coordinates.get(0),
//                new Coordinate(500, 500),
//                coordinates.get(0)
//        ))));
//        elements.add(vehicles.get(0));
//        vehicles.add(new Vehicle(new Coordinate(50, 320), 10, new Path(Arrays.asList(
//                new Coordinate(100, 300),
//                new Coordinate(500, 50)
//        ))));
//        elements.add(vehicles.get(1));
//        List<Stop> stops = new ArrayList<>();
//        stops.add(new Stop(new Coordinate(200, 200)));
//        stops.add(new Stop(new Coordinate(300, 300)));
//        stops.add(new Stop(new Coordinate(200, 150)));
//        List<Street> streets = new ArrayList<>();
//        streets.add(new Street("Nádražní 2", new Coordinate(100, 100), new Coordinate(500, 500), Arrays.asList(stops.get(0), stops.get(1))));
//        streets.add(new Street("Nádražní", new Coordinate(50, 320), new Coordinate(200, 150), Arrays.asList(stops.get(2))));

        //Data data = new Data(coordinates, vehicles, streets, stops);

        YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(factory);

        // serialization
        //mapper.writeValue(new File("test.yml"), data);

        // deserialization
        Data data1 = mapper.readValue(new File("data.yml"), Data.class);
        System.out.println(data1);

        // import elements to gui
        List<Drawable> elements = new ArrayList<>(data1.getVehicles());
        elements.addAll(new ArrayList<>(data1.getStreets()));
        elements.addAll(new ArrayList<>(data1.getStops()));
        controller.setElements(elements);

        // start the timer with the scale 1
        controller.timer(1);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

package ija_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        // get the controller to use timer and import data
        Controller controller = loader.getController();

        // sample data
        List<Drawable> elements = new ArrayList<>();
        Vehicle vehicle = new Vehicle(new Coordinate(100, 100), 10, new Path(Arrays.asList(
                new Coordinate(100, 100),
                new Coordinate(500, 500),
                new Coordinate(100, 100)
        )));
        elements.add(vehicle);
        elements.add(new Vehicle(new Coordinate(50, 320), 10, new Path(Arrays.asList(
                new Coordinate(100, 300),
                new Coordinate(500, 50)
        ))));
        elements.add(new Street("Nádražní 2", new Coordinate(100, 100), new Coordinate(500, 500)));
        elements.add(new Street("Nádražní", new Coordinate(50, 320), new Coordinate(200, 150)));

        // import elements to gui
        controller.setElements(elements);

        // start the timer with the scale 1
        controller.timer(1);

        YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.writeValue(new File("test.yml"), vehicle);
    }


    public static void main(String[] args) {
        launch(args);
    }
}

package ija_project;

import java.util.List;

/**
 * Data blueprint for Jackson
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version %I%, %G%
 */
public class Data {
    /**
     * List of all Coordinates
     */
    private List<Coordinate> coordinates;
    /**
     * List of all vehicles
     */
    private List<Vehicle> vehicles;
    /**
     * List of all streets
     */
    private List<Street> streets;
    /**
     * List of all stops
     */
    private List<Stop> stops;
    /**
     * List of all Paths
     */
    private List<Path> paths;

    /**
     * Default constructor for Jackson
     */
    private Data(){}

    /**
     * Constructor Data
     * @param coordinates List of coordinates
     * @param vehicles List of Vehicles
     * @param streets List of Streets
     * @param stops List of stops
     * @param paths List of Paths
     */
    public Data(List<Coordinate> coordinates, List<Vehicle> vehicles, List<Street> streets, List<Stop> stops, List<Path> paths) {
        this.coordinates = coordinates;
        this.vehicles = vehicles;
        this.streets = streets;
        this.stops = stops;
        this.paths = paths;
    }

    /**
     * {@link Data#coordinates}
     */
    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    /**
     * {@link Data#vehicles}
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * {@link Data#streets}
     */
    public List<Street> getStreets() {
        return streets;
    }

    /**
     * {@link Data#stops}
     */
    public List<Stop> getStops() {
        return stops;
    }

    /**
     * {@link Data#paths}
     */
    public List<Path> getPaths() {
        return paths;
    }

    @Override
    public String toString() {
        return "Data{" +
                "coordinates=" + coordinates +
                ", vehicle=" + vehicles +
                '}';
    }
}

package ija_project;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<Coordinate> coordinates;
    private List<Vehicle> vehicles;
    private List<Street> streets;
    private List<Stop> stops;

    private Data(){}

    public Data(List<Coordinate> coordinates, List<Vehicle> vehicles, List<Street> streets, List<Stop> stops) {
        this.coordinates = coordinates;
        this.vehicles = vehicles;
        this.streets = streets;
        this.stops = stops;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Stop> getStops() {
        return stops;
    }

    @Override
    public String toString() {
        return "Data{" +
                "coordinates=" + coordinates +
                ", vehicle=" + vehicles +
                '}';
    }
}

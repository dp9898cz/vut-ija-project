package ija_project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;

/**
 * Path class representing one Path
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version 1.0
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Path {
    /**
     * List of coordinates vehicle follows
     */
    private List<Coordinate> path;

    /**
     * Number of the line
     */
    private String number;

    /**
     * List of stops which are real Stops
     */
    @JsonIgnore
    private List<Stop> stopList = new ArrayList<>();

    /**
     * Default constructor for Jackson.
     */
    private Path(){}

    /**
     * Path constructor
     * @param path List of coordinates of path
     * @param number Number of the line
     */
    public Path(List<Coordinate> path, String number, List<Stop> stopList) {
        this.path = path;
        this.number = number;
        this.stopList = stopList;
    }

    /**
     * Get total distance of the Path
     * @return Total path distance
     */
    @JsonIgnore
    public double getPathDistance() {
        double finalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            finalDistance += getDistance(path.get(i), path.get(i + 1));
        }
        return finalDistance;
    }

    /**
     * Get distance between two coordinates
     * @param a coordinate A
     * @param b coordinate B
     * @return distance between coordinate A and coordinate B
     */
    @JsonIgnore
    public double getDistance(Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * Get next Coordinate depending on distance moved
     * @param distance Current distance of vehicle
     * @param v Vehicle object
     * @return New position coordinates
     */
    @JsonIgnore
    public Coordinate getDistanceCoordinate(double distance, Vehicle v) {
        double nextDistance = 0;    // distance to the next stop
        double length = 0;          // dummy distance - distance of the previous segments (helpful for the next for loop)
        Coordinate x = null;
        Coordinate y = null;

        for (int i = 0; i < path.size() - 1; i++) {
            x = path.get(i);
            y = path.get(i + 1);
            nextDistance = getDistance(x, y); // distance of two points
            if (length + nextDistance >= distance && (distance - v.getSpeed()) < length) {
                // we have to go to stop coordinates
                return x;
            }
            if (length + nextDistance >= distance) break;
            length += nextDistance;
        }
        double driven = (distance - length) / nextDistance; // driven % to the next stop

        if (x == null) return null;

        // new coordinates
        return new Coordinate(x.getX() + (y.getX() - x.getX()) * driven,
                x.getY() + (y.getY() - x.getY()) * driven);
    }

    /**
     * {@link Path#path}
     * @return List of coordinates Path
     */
    public List<Coordinate> getPath() {
        return path;
    }

    /**
     * {@link Path#stopList}
     * @return List of Stops
     */
    public List<Stop> getStopList() {
        return stopList;
    }

    /**
     * {@link Path#number}
     * @return Number of line
     */
    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "Path{" +
                "path=" + path +
                '}';
    }
}

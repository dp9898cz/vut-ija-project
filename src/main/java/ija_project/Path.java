package ija_project;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Path {
    private List<Coordinate> path;
    private String number;

    private Path(){}

    // Constructor
    public Path(List<Coordinate> path, String number) {
        this.path = path;
        this.number = number;
    }

    @JsonIgnore
    public double getPathDistance() {
        double finalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            finalDistance += getDistance(path.get(i), path.get(i + 1));
        }
        return finalDistance;
    }

    @JsonIgnore
    private double getDistance(Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    @JsonIgnore
    // get next Coordinate depending on distance moved
    // distance = new distance
    public Coordinate getDistanceCoordinate(double distance, Vehicle v) {
        double nextDistance = 0;    // distance to the next stop
        double length = 0;          // dummy distance - distance of the previous segments (helpful for the next for loop)
        Coordinate x = null;
        Coordinate y = null;

        //checkDistance(distance, distance - v.getSpeed());

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

        return new Coordinate(x.getX() + (y.getX() - x.getX()) * driven,
                x.getY() + (y.getY() - x.getY()) * driven);
    }

    public List<Coordinate> getPath() {
        return path;
    }

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

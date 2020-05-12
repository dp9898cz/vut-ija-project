package ija_project;

import java.util.List;

public class Path {
    private final List<Coordinate> path;

    // Constructor
    public Path(List<Coordinate> path) {
        this.path = path;
    }

    public double getPathDistance() {
        double finalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            finalDistance += getDistance(path.get(i), path.get(i + 1));
        }
        return finalDistance;
    }

    private double getDistance(Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    // get next Coordinate depending on distance moved
    public Coordinate getDistanceCoordinate(double distance) {
        double nextDistance = 0;    // distance to the next stop
        double length = 0;          // dummy distance - distance of the previous segments (helpful for the next for loop)
        Coordinate x = null;
        Coordinate y = null;

        for (int i = 0; i < path.size() - 1; i++) {
            x = path.get(i);
            y = path.get(i + 1);
            nextDistance = getDistance(x, y); // distance of two points
            if (length + nextDistance >= distance) break;
            length += nextDistance;
        }
        double driven = (distance - length) / nextDistance; // driven % to the next stop

        if (x == null) return null;

        return new Coordinate(x.getX() + (y.getX() - x.getX()) * driven,
                x.getY() + (y.getY() - x.getY()) * driven);
    }
}

package tsp.delaunay;

import java.awt.geom.Point2D;

public class MyPoint extends Point2D.Double {
    int pointNumber;

    public MyPoint(int pointNumber, double x, double y) {
        super(x, y);
        this.pointNumber = pointNumber;

    }
}

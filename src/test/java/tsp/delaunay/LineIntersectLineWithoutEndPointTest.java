package tsp.delaunay;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.geom.Line2D;

public class LineIntersectLineWithoutEndPointTest {


    @Test
    public void test1() {

        Line2D.Double line = new Line2D.Double(0, 0, 10, 10);
        Line2D.Double line2 = new Line2D.Double(0, 10, 10, 0);

        boolean result = TriangulationBuilder.areLinesIntersectingWithoutEndpoints(line, line2);

        Assertions.assertTrue(result);

    }

    @Test
    public void test2() {

        Line2D.Double line = new Line2D.Double(0, 0, 10, 0);
        Line2D.Double line2 = new Line2D.Double(0, 10, 10, 10);

        boolean result = TriangulationBuilder.areLinesIntersectingWithoutEndpoints(line, line2);

        Assertions.assertFalse(result);

    }

    @Test
    public void test3() {

        Line2D.Double line = new Line2D.Double(0, 0, 10, 0);
        Line2D.Double line2 = new Line2D.Double(0, 0, 0, 10);

        boolean result = TriangulationBuilder.areLinesIntersectingWithoutEndpoints(line, line2);
        //System.out.println("This test method should be run");
        Assertions.assertFalse(result);

    }


}
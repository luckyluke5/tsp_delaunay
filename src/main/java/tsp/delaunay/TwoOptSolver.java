package tsp.delaunay;

import org.jgrapht.alg.connectivity.ConnectivityInspector;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Objects;

public class TwoOptSolver {

    public final Instance instance;

    public TwoOptSolver(Instance instance) {
        this.instance = instance;
    }

    public void towOptForNonIntersectingEdges() {
        int counter = 0;
        int numberOfLoopsNotEdit = 0;

        while (numberOfLoopsNotEdit < getInstance().tourSubgraphMask.edgeSet().size() * 2) {

            counter += 1;
            System.out.println(counter);
            //i=0;
            Object[] array = getInstance().tourSubgraphMask.edgeSet().toArray();
            for (int i = 0; i < array.length; i++) {
                numberOfLoopsNotEdit += 1;
                ModifiedWeightedEdge edge1 = (ModifiedWeightedEdge) array[i];
                Point2D p1 = getInstance().graph.getEdgeSource(edge1);
                Point2D p2 = getInstance().graph.getEdgeTarget(edge1);
                //System.out.println(i);
                for (int j = i + 1; j < array.length; j++) {
                    ModifiedWeightedEdge edge2 = (ModifiedWeightedEdge) array[j];
                    Point2D p3 = getInstance().graph.getEdgeSource(edge2);
                    Point2D p4 = getInstance().graph.getEdgeTarget(edge2);
                    if (Line2D.linesIntersect(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY())) {
                        if (!Objects.equals(edge1, edge2)) {
                            if (!Objects.equals(p1, p3) && !Objects.equals(p1, p4) && !Objects.equals(p2, p3) && !Objects.equals(p2, p4)) {

                                double result = solveEdgeCrossing(edge1, edge2);
                                if (result < -0.0001) {
                                    //System.out.println(submask.edgeSet().size());
                                    //System.out.println(result);
                                }
                                numberOfLoopsNotEdit = 0;
                            }

                        }
                    }
                }
            }

        }
    }

    public Instance getInstance() {
        return instance;
    }

    public double solveEdgeCrossing(ModifiedWeightedEdge edge1, ModifiedWeightedEdge edge2) {
        Instance instance = getInstance();
        ModifiedWeightedEdge add1First = instance.graph.getEdge(instance.graph.getEdgeSource(edge1), instance.graph.getEdgeSource(edge2));

        ModifiedWeightedEdge add2First = instance.graph.getEdge(instance.graph.getEdgeTarget(edge1), instance.graph.getEdgeTarget(edge2));

        ModifiedWeightedEdge add1Second = instance.graph.getEdge(instance.graph.getEdgeSource(edge1), instance.graph.getEdgeTarget(edge2));

        ModifiedWeightedEdge add2Second = instance.graph.getEdge(instance.graph.getEdgeTarget(edge1), instance.graph.getEdgeSource(edge2));

        try {
            if ((add1First.isInTour() || add2First.isInTour()) && (add1Second.isInTour() || add2Second.isInTour())) {
                return 0;
            }
        } catch (NullPointerException e) {
            return 0;
        }

        if (!edge1.isInTour() || !edge2.isInTour()) {
            return 0;
        }

        double result = 0;
        ConnectivityInspector<Point2D, ModifiedWeightedEdge> connectivityInspector = new ConnectivityInspector<>(instance.tourSubgraphMask);
        double result1 = 0;
        double result2 = result1;
        double result3 = result1;
        if (connectivityInspector.isConnected()) {
            //System.out.println(result);
        }


        result -= instance.forceRemoveEdge(edge1);
        result -= instance.forceRemoveEdge(edge2);


        if (!add1First.isInTour() && !add2First.isInTour()) {
            try {


                result += instance.forceAddEdge(add1First);

                result += instance.forceAddEdge(add2First);

                connectivityInspector = new ConnectivityInspector<>(instance.tourSubgraphMask);
                if (connectivityInspector.isConnected() && result < 0) {
                    //System.out.println(result);
                    result2 = result;
                }
                result -= instance.forceRemoveEdge(add1First);
                result -= instance.forceRemoveEdge(add2First);
            } catch (NullPointerException ignore) {

            }
        }

        if (!add1Second.isInTour() && !add2Second.isInTour()) {

            try {

                result += instance.forceAddEdge(add1Second);
                result += instance.forceAddEdge(add2Second);
                connectivityInspector = new ConnectivityInspector<>(instance.tourSubgraphMask);
                if (connectivityInspector.isConnected() && result < 0) {
                    //System.out.println(result);
                    result3 = result;
                }
                result -= instance.forceRemoveEdge(add1Second);
                result -= instance.forceRemoveEdge(add2Second);
            } catch (NullPointerException ignore) {

            }

        }

        result += instance.forceAddEdge(edge1);
        result += instance.forceAddEdge(edge2);


        if (Math.min(result2, result3) < 0) {
            result -= instance.forceRemoveEdge(edge1);
            result -= instance.forceRemoveEdge(edge2);
            if (result2 < result3) {
                result += instance.forceAddEdge(add1First);

                result += instance.forceAddEdge(add2First);
            } else {
                result += instance.forceAddEdge(add1Second);
                result += instance.forceAddEdge(add2Second);
            }
        }
        return result;

    }
}

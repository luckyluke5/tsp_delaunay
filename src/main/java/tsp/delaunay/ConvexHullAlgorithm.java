package tsp.delaunay;


import org.jgrapht.Graphs;
import org.jgrapht.graph.MaskSubgraph;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConvexHullAlgorithm {

    private final MaskSubgraph<Point2D, ModifiedWeightedEdge> firstOrderDelaunayEdgesSubgraphMask;
    private final MaskSubgraph<Point2D, ModifiedWeightedEdge> zeroOrderDelaunayEdgesSubgraphMask;
    private final MaskSubgraph<Point2D, ModifiedWeightedEdge> secoundOrderDelaunayEdgesSubgraphMask;
    private final TriangulationBuilder triangulationBuilder;
    Instance instance;


    public ConvexHullAlgorithm(Instance instance) {
        this.instance = instance;

        triangulationBuilder = new TriangulationBuilder(instance);

        zeroOrderDelaunayEdgesSubgraphMask = new MaskSubgraph<>(instance.graph, (Point2D p) -> false, (ModifiedWeightedEdge edge) -> edge.getUsefulDelaunayOrder() != 0);

        firstOrderDelaunayEdgesSubgraphMask = new MaskSubgraph<>(instance.graph, (Point2D p) -> false, (ModifiedWeightedEdge edge) -> edge.getUsefulDelaunayOrder() != 1);
        secoundOrderDelaunayEdgesSubgraphMask = new MaskSubgraph<>(instance.graph, (Point2D p) -> false, (ModifiedWeightedEdge edge) -> edge.getUsefulDelaunayOrder() != 2);

    }

    void startAlgorithm() {
        setDelaunayTriangulationAsTriangulation();

        //insertFirstOrderDelaunayEdgesInTriangulation();

        //insertSecondOrderDelaunayEdgesInTriangulationWitchAreNotIntersectingFirstOrder();

        setConvexHullAsTour();

        expandTourWithTriangleWithLargestAngel();

        //flipEdgesUntilAllPointsAreInTour();

        if (!findUnreachedPoints().isEmpty()) {
            //throw new ArithmeticException("The tour does not contain all points");
        }

    }

    private void setDelaunayTriangulationAsTriangulation() {
        for (ModifiedWeightedEdge edge : instance.graph.edgeSet()
        ) {

            edge.setInTriangulation(edge.getUsefulDelaunayOrder() == 0);

        }
    }

    private void setConvexHullAsTour() {
        for (ModifiedWeightedEdge edge : instance.triangulationSubgraphMask.edgeSet()
        ) {
            edge.setInTour(edge.isPartOfConvexHull());
        }
    }

    private void expandTourWithTriangleWithLargestAngel() {

        Stream<ModifiedWeightedEdge> sortedEdges = instance.tourSubgraphMask.edgeSet().stream().sorted(Comparator.comparing(this::angel));


        PriorityQueue<ModifiedWeightedEdge> edgeQueue = new PriorityQueue<>(instance.tourSubgraphMask.edgeSet().size(), Comparator.comparing(this::angel));
        edgeQueue.addAll(sortedEdges.collect(Collectors.toList()));
        //sortedEdges.to

        while (!edgeQueue.isEmpty()) {
            ModifiedWeightedEdge edge = edgeQueue.poll();
            List<Point2D> points = getNeighborsWhichAreNotInTour(edge);

            if (!points.isEmpty()) {
                includePoint(edge, points.get(0));
                edgeQueue.add(instance.graph.getEdge(instance.graph.getEdgeSource(edge), points.get(0)));
                edgeQueue.add(instance.graph.getEdge(instance.graph.getEdgeTarget(edge), points.get(0)));
            }

        }

    }

    private List<Point2D> findUnreachedPoints() {
        return instance.tourSubgraphMask.vertexSet().stream().filter(this::isInTour).collect(Collectors.toList());

    }

    private double angel(ModifiedWeightedEdge edge) {

        Point2D source = instance.graph.getEdgeSource(edge);
        Point2D target = instance.graph.getEdgeTarget(edge);


        List<Point2D> potentialExpandingPoints = getNeighborsWhichAreNotInTour(edge);


        if (potentialExpandingPoints.size() > 1) {
            //throw new ArithmeticException("This should not happen in a triangulation");
        }


        if (potentialExpandingPoints.size() == 1) {
            return computeAngel(source, target, potentialExpandingPoints.get(0));
        } else {
            return 180;
        }

    }

    private List<Point2D> getNeighborsWhichAreNotInTour(ModifiedWeightedEdge edge) {

        Point2D source = instance.graph.getEdgeSource(edge);
        Point2D target = instance.graph.getEdgeTarget(edge);

        List<Point2D> sourceNeighbor = Graphs.neighborListOf(instance.triangulationSubgraphMask, source);
        List<Point2D> targetNeighbor = Graphs.neighborListOf(instance.triangulationSubgraphMask, target);


        sourceNeighbor.retainAll(targetNeighbor);

        return sourceNeighbor.stream().filter(point2D -> instance.tourSubgraphMask.edgesOf(point2D).isEmpty()).collect(Collectors.toList());

    }

    private void includePoint(ModifiedWeightedEdge edge, Point2D point) {
        edge.setInTour(false);


        ModifiedWeightedEdge sourceEdge = instance.graph.getEdge(instance.graph.getEdgeSource(edge), point);
        ModifiedWeightedEdge targetEdge = instance.graph.getEdge(instance.graph.getEdgeTarget(edge), point);

        ModifiedWeightedEdge.setAllInTour(new ModifiedWeightedEdge[]{sourceEdge, targetEdge}, true);

    }

    private boolean isInTour(Point2D point2D) {
        return instance.tourSubgraphMask.inDegreeOf(point2D) == 0;
    }

    private double computeAngel(Point2D source, Point2D target, Point2D expandingPoint) {

        double angel = angleBetween2Lines(new Line2D.Double(expandingPoint, source), new Line2D.Double(expandingPoint, target));
        //System.out.println(angel);

        return 360 - angel;

    }

    public static double angleBetween2Lines(Line2D line1, Line2D line2) {
        Point2D line1P1 = line1.getP1();
        Point2D line1P2 = line1.getP2();
        Point2D line2P1 = line2.getP1();
        Point2D line2P2 = line2.getP2();
        float angle1 = (float) Math.atan2(line1P2.getY() - line1P1.getY(), line1P1.getX() - line1P2.getX());
        float angle2 = (float) Math.atan2(line2P2.getY() - line2P1.getY(), line2P1.getX() - line2P2.getX());
        float calculatedAngle = (float) Math.toDegrees(angle1 - angle2);
        if (calculatedAngle < 0) calculatedAngle += 360;
        return Math.min(calculatedAngle - 0, 360 - calculatedAngle);
    }

    private void insertSecondOrderDelaunayEdgesInTriangulationWitchAreNotIntersectingFirstOrder() {
        for (ModifiedWeightedEdge edge : secoundOrderDelaunayEdgesSubgraphMask.edgeSet()) {
            if (!triangulationBuilder.isLineIntersectAnyOtherLine(edge, firstOrderDelaunayEdgesSubgraphMask.edgeSet())) {
                insertEdgeAndRemoveIntersecting(edge);
            }
        }
    }

    private void insertEdgeAndRemoveIntersecting(ModifiedWeightedEdge edgeToInsert) {


        for (ModifiedWeightedEdge edge : instance.triangulationSubgraphMask.edgeSet()) {
            if (TriangulationBuilder.areLinesIntersectingWithoutEndpoints(instance.lineFromEdge(edgeToInsert), instance.lineFromEdge(edge))) {
                edge.setInTriangulation(false);

            }
        }
        edgeToInsert.setInTriangulation(true);
    }

    private void insertFirstOrderDelaunayEdgesInTriangulation() {
        for (ModifiedWeightedEdge edge : firstOrderDelaunayEdgesSubgraphMask.edgeSet()) {
            insertEdgeAndRemoveIntersecting(edge);
        }
    }

    private void flipEdgesUntilAllPointsAreInTour() {
        List<Point2D> points = findUnreachedPoints();
        for (Point2D point : points
        ) {

            flipZeroOrderEdgesUntilPointIsInTour(point);

        }
        expandTourWithTriangleWithLargestAngel();
        points = findUnreachedPoints();
        for (Point2D point : points
        ) {

            flipFirstOrderEdgesUntilPointIsInTour(point);

        }
        expandTourWithTriangleWithLargestAngel();
    }

    private void flipZeroOrderEdgesUntilPointIsInTour(Point2D point) {
        for (ModifiedWeightedEdge edge : zeroOrderDelaunayEdgesSubgraphMask.edgesOf(point)) {


            edge.setInTriangulation(true);

        }

    }

    private void flipFirstOrderEdgesUntilPointIsInTour(Point2D point) {

        for (ModifiedWeightedEdge edge : firstOrderDelaunayEdgesSubgraphMask.edgesOf(point)) {
            edge.setInTriangulation(true);

        }
    }

}
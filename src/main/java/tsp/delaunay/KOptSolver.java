package tsp.delaunay;

import org.jgrapht.alg.connectivity.ConnectivityInspector;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class KOptSolver {


    public final Instance instance;

    public KOptSolver(Instance instance) {
        this.instance = instance;
    }

    AugmentedCircle cheapestAugmentingCircle(ModifiedWeightedEdge edge, int k) {

        ArrayList<Point2D> array = new ArrayList<>();
        array.add(getInstance().graph.getEdgeSource(edge));
        array.add(getInstance().graph.getEdgeTarget(edge));
        edge.setInTour(false);
        edge.inAugmentingCircle.set(true);
        AugmentedCircle result = searchFromWithEdges(array, (double) 0 - getInstance().graph.getEdgeWeight(edge), k);
        edge.setInTour(true);
        edge.inAugmentingCircle.set(false);
        if (result.points.isEmpty()) {
            //throw new Exception("no optimisation possible");
        }
        //augment_tour(result.points);
        System.out.println(result.length + " " + result.points.size());
        return result;


        //


    }

    AugmentedCircle searchFromWithEdges(ArrayList<Point2D> points, Double length, int k) {
        int numberOfPoints = points.size();
        boolean isOdd = numberOfPoints % 2 == 1;


        AugmentedCircle minSoFar = new AugmentedCircle(new ArrayList<>(), (double) 1000);

        if (numberOfPoints > k) {
            return minSoFar;
        }

        for (ModifiedWeightedEdge edge : getInstance().graph.outgoingEdgesOf(points.get(numberOfPoints - 1))
        ) {
            if (!edge.inAugmentingCircle.get()) {
                if (isOdd) {
                    if (edge.isInTour()) {

                        length -= getInstance().graph.getEdgeWeight(edge);
                        edge.inAugmentingCircle.set(true);
                        edge.setInTour(false);

                        if (getInstance().graph.getEdgeSource(edge).equals(points.get(numberOfPoints - 1))) {
                            points.add(getInstance().graph.getEdgeTarget(edge));
                        } else {
                            points.add(getInstance().graph.getEdgeSource(edge));
                        }

                        AugmentedCircle result = searchFromWithEdges(points, length, k);

                        if (result.length < minSoFar.length) {

                            minSoFar = result;
                        }

                        points.remove(numberOfPoints);

                        length += getInstance().graph.getEdgeWeight(edge);
                        edge.inAugmentingCircle.set(false);
                        edge.setInTour(true);
                    }

                } else {
                    if (!edge.isInTour() && edge.inModifiedTriangulation.get()) {

                        length += getInstance().graph.getEdgeWeight(edge);
                        edge.inAugmentingCircle.set(true);
                        edge.setInTour(true);

                        if (getInstance().graph.getEdgeSource(edge).equals(points.get(numberOfPoints - 1))) {
                            points.add(getInstance().graph.getEdgeTarget(edge));
                        } else {
                            points.add(getInstance().graph.getEdgeSource(edge));
                        }
                        AugmentedCircle result = searchFromWithEdges(points, length, k);

                        if (points.get(numberOfPoints).equals(points.get(0))) {
                            ConnectivityInspector<Point2D, ModifiedWeightedEdge> connectivityInspector = new ConnectivityInspector<>(getInstance().tourSubgraphMask);
                            if (connectivityInspector.isConnected() && length < minSoFar.length) {
                                //System.out.println(result);


                                minSoFar = new AugmentedCircle((ArrayList<Point2D>) points.clone(), length);
                            }

                        }

                        if (result.length < minSoFar.length) {

                            minSoFar = result;
                        }


                        points.remove(numberOfPoints);

                        length -= getInstance().graph.getEdgeWeight(edge);
                        edge.inAugmentingCircle.set(false);
                        edge.setInTour(false);
                    }

                }
            }
        }
        //System.out.println("in_augmentation");
        return minSoFar;

    }

    void augmentTour(ArrayList<Point2D> tour) {
        for (int i = 0; i < tour.size() - 1; i++) {
            ModifiedWeightedEdge edge = getInstance().graph.getEdge(tour.get(i), tour.get(i + 1));
            edge.setInTour(!edge.isInTour());

            if (edge.isInTour() && !edge.isInTriangulation()) {

                //System.out.println("adding edge which is not in triangulation");
                //throw new ValueException("why is it");
            }

        }

    }

    void modifyTriangulationAndForceEdge(ModifiedWeightedEdge forceEdge) {

        forceEdge.inModifiedTriangulation.set(true);
        ArrayList<ModifiedWeightedEdge> modifiedEdges = new ArrayList<>();
        ArrayList<ModifiedWeightedEdge> deletedEdges = new ArrayList<>();
        modifiedEdges.add(forceEdge);

        for (ModifiedWeightedEdge edge : getInstance().triangulationSubgraphMask.edgeSet()
        ) {
            if (TriangulationBuilder.areLinesIntersectingWithoutEndpoints(getInstance().lineFromEdge(forceEdge), getInstance().lineFromEdge(edge))) {
                edge.inModifiedTriangulation.set(false);
                //edge.set_in_tour(false);


                if (edge.isInTour()) {
                    deletedEdges.add(edge);
                }

                modifiedEdges.add(edge);

            }
        }
        ArrayList<ModifiedWeightedEdge> added = addEdgesToModifiedTriangulation();
        modifiedEdges.addAll(added);

        ArrayList<AugmentedCircle> results = new ArrayList<>();
        int i = 5;
        for (ModifiedWeightedEdge edge : deletedEdges
        ) {


            AugmentedCircle result = cheapestAugmentingCircle(edge, 2 * i);
            if (!result.points.isEmpty()) {
                //augment_tour(result.points);
                results.add(result);
            }
            //augment_tour(result.points);

        }


        try {
            AugmentedCircle circle = results.stream().min(Comparator.comparing(AugmentedCircle::getLength)).orElseThrow(NoSuchElementException::new);
            if (circle.length < 100) {
                new KOptSolver(getInstance()).augmentTour(circle.points);
                for (ModifiedWeightedEdge edge : modifiedEdges
                ) {
                    edge.setInTriangulation(edge.inModifiedTriangulation.get());
                    if (edge.isInTour() && !edge.isInTriangulation()) {

                        System.out.println("adding edge which is not in triangulation");
                        //throw new ValueException("why is it");
                        //Thread.sleep(100);
                    }
                    //Thread.sleep(100);
                }
            } else {
                for (ModifiedWeightedEdge edge : modifiedEdges
                ) {
                    edge.inModifiedTriangulation.set(edge.isInTriangulation());
                    if (edge.isInTour() && !edge.isInTriangulation()) {

                        System.out.println("adding edge which is not in triangulation");
                        //throw new ValueException("why is it");
                        //Thread.sleep(100);
                    }
                    //Thread.sleep(100);
                }
            }

        } catch (NoSuchElementException e) {

            if (!deletedEdges.isEmpty()) {
                for (ModifiedWeightedEdge edge : modifiedEdges
                ) {
                    edge.inModifiedTriangulation.set(edge.isInTriangulation());
                    if (edge.isInTour() && !edge.isInTriangulation()) {

                        System.out.println("adding edge which is not in triangulation");
                        //throw new ValueException("why is it");
                        //Thread.sleep(100);
                    }
                    //Thread.sleep(100);
                }
            } else {


                for (ModifiedWeightedEdge edge : modifiedEdges
                ) {
                    edge.setInTriangulation(edge.inModifiedTriangulation.get());
                    if (edge.isInTour() && !edge.isInTriangulation()) {

                        System.out.println("adding edge which is not in triangulation");
                        //throw new ValueException("why is it");
                        //Thread.sleep(100);
                        //
                    }

                }

            }

        }

    }

    public Instance getInstance() {
        return instance;
    }

    private ArrayList<ModifiedWeightedEdge> addEdgesToModifiedTriangulation() {
        //MaskSubgraph mask=new MaskSubgraph(this.graph,)

        ArrayList<ModifiedWeightedEdge> result = new ArrayList<>();
        for (ModifiedWeightedEdge edge : getInstance().graph.edgeSet()) {
            Line2D line = getInstance().lineFromEdge(edge);
            List<Line2D> lineArray = getInstance().modifiedTriangulationSubgraphMask.edgeSet().stream().map(getInstance()::lineFromEdge).collect(Collectors.toList());

            if (TriangulationBuilder.isLineIntersectAnyOtherLine(line, lineArray)) {
                //edge.in_triangulation.set(false);
            } else {
                //if (!edge.get_in_triangulation()) {
                result.add(edge);
                //}

                edge.inModifiedTriangulation.set(true);

            }

        }
        return result;
    }
}

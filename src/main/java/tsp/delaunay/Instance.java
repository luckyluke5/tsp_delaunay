package tsp.delaunay;


//import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.MaskSubgraph;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;


public class Instance {


    ArrayList<MyPoint> points;
    DefaultUndirectedWeightedGraph<Point2D, ModifiedWeightedEdge> graph;
    MaskSubgraph<Point2D, ModifiedWeightedEdge> tourSubgraphMask;
    MaskSubgraph<Point2D, ModifiedWeightedEdge> triangulationSubgraphMask;
    MaskSubgraph<Point2D, ModifiedWeightedEdge> modifiedTriangulationSubgraphMask;


    public Instance(File file) {


        readPointsFromFile(file);


        createGraphAndEdgesFromPoints();

    }

    public void readPointsFromFile(File file) {
        points = new ArrayList<>();

        try {
            //File myObj = new File("filename.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                try {
                    int pointNumber = Integer.parseInt(scanner.next());
                    double x = Double.parseDouble(scanner.next());
                    double y = Double.parseDouble(scanner.next());
                    points.add(new MyPoint(pointNumber, x, y));
                } catch (Exception e) {
                    System.out.println("One row which can not be read.");
                    //break;
                    //e.printStackTrace();
                }

            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    void createGraphAndEdgesFromPoints() {
        graph = new DefaultUndirectedWeightedGraph<>(ModifiedWeightedEdge.class);
        tourSubgraphMask = new MaskSubgraph<>(graph, (Point2D p) -> false, (ModifiedWeightedEdge edge) -> !edge.isInTour());
        triangulationSubgraphMask = new MaskSubgraph<>(graph, (Point2D p) -> false, (ModifiedWeightedEdge edge) -> !edge.isInTriangulation());
        modifiedTriangulationSubgraphMask = new MaskSubgraph<>(graph, (Point2D p) -> false, (ModifiedWeightedEdge edge) -> !edge.inModifiedTriangulation.get());
        points.forEach(graph::addVertex);


        points = points.stream().unordered()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        //boolean new_array =new ArrayList < Point2D >;

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                ModifiedWeightedEdge edge = graph.addEdge(points.get(i), points.get(j));
                graph.setEdgeWeight(edge, points.get(i).distance(points.get(j)));
                int order = setUsefulOrderOfEdge(edge);
                edge.setUsefulDelaunayOrder(order);
            }
        }
    }

    int setUsefulOrderOfEdge(ModifiedWeightedEdge edge) throws ArithmeticException {
        Point2D source = graph.getEdgeSource(edge);
        Point2D target = graph.getEdgeTarget(edge);
        Line2D line = new Line2D.Double(source, target);

        double length = source.distance(target);

        //length=10;

        ArrayList<Point2D> leftPoints = new ArrayList<>();
        ArrayList<Point2D> rightPoints = new ArrayList<>();
        int onLine = 0;

        for (Point2D point : points
        ) {

            if (point.equals(source) || point.equals(target)) {
                continue;
            }

            if (line.ptLineDist(point) > 0) {


                if (line.relativeCCW(point) > 0) {
                    rightPoints.add(point);
                } else {
                    leftPoints.add(point);
                }
            } else {
                if (line.ptSegDist(point) > 0) {

                } else {
                    return points.size();
                }

            }

        }

        double[] leftPointsSorted = leftPoints.stream().mapToDouble((Point2D point) -> {
            Point2D.Double center = calculateCircleCenter(edge, point);
            return line.ptLineDist(center) * line.relativeCCW(center);
        }).sorted().toArray();
        double[] rightPointsSorted = rightPoints.stream().mapToDouble((Point2D point) -> {
            Point2D.Double center = calculateCircleCenter(edge, point);
            return line.ptLineDist(center) * line.relativeCCW(center);
        }).sorted().toArray();


        int rightIndex = 0;
        int leftIndex = 0;


        if (leftPointsSorted.length > 0) {
            try {
                double leftMax = leftPointsSorted[leftPointsSorted.length - 1];
                //rightIndex = 0;
                while (leftMax >= rightPointsSorted[rightIndex]) {
                    rightIndex += 1;
                }
                if (leftMax == rightPointsSorted[rightIndex]) {
                    System.out.println("More then tree points on circle");
                    rightIndex = this.points.size();
                    ;
                } else {
                    //edge.rightDistance=Math.abs(rightPointsSorted[rightIndex] - rightPointsSorted[rightIndex - 1])/length;
                    edge.rightDistance = Math.abs(rightPointsSorted[rightIndex] - rightPointsSorted[rightIndex - 1]);
                    edge.distanceToNextCircleCenterString += String.valueOf(Math.round(Math.abs(rightPointsSorted[rightIndex] - rightPointsSorted[rightIndex - 1]) / (length / 10))) + " ";
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                //rightIndex = this.points.size();
            }
        } else {
            edge.setPartOfConvexHull(true);
        }
        if (rightPointsSorted.length > 0) {

            try {

                double rightMin = rightPointsSorted[0];
                //leftIndex = 0;
                while (rightMin <= leftPointsSorted[leftPointsSorted.length - (leftIndex + 1)]) {
                    leftIndex += 1;
                }
                if (rightMin == leftPointsSorted[leftPointsSorted.length - (leftIndex + 1)]) {
                    System.out.println("More then tree points on circle");
                    leftIndex = this.points.size();
                } else {
                    //edge.leftDistance=Math.abs(leftPointsSorted[leftPointsSorted.length - (leftIndex + 1)] - leftPointsSorted[leftPointsSorted.length - (leftIndex)])/length;
                    edge.leftDistance = Math.abs(leftPointsSorted[leftPointsSorted.length - (leftIndex + 1)] - leftPointsSorted[leftPointsSorted.length - (leftIndex)]);

                    edge.distanceToNextCircleCenterString += String.valueOf(Math.round(Math.abs(leftPointsSorted[leftPointsSorted.length - (leftIndex + 1)] - leftPointsSorted[leftPointsSorted.length - (leftIndex)]) / (length / 10))) + " ";
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                //leftIndex = this.points.size();
            }
        } else {
            edge.setPartOfConvexHull(true);
        }
        try {
            Point2D leftMinPoint = leftPoints.stream().max(Comparator.comparing((Point2D point) -> {
                Point2D.Double center = calculateCircleCenter(edge, point);
                return line.ptLineDist(center) * line.relativeCCW(center);
            })).orElseThrow(NoSuchElementException::new);


            Point2D rightMinPoint = rightPoints.stream().min(Comparator.comparing((Point2D point) -> {
                Point2D.Double center = calculateCircleCenter(edge, point);
                return line.ptLineDist(center) * line.relativeCCW(center);
            })).orElseThrow(NoSuchElementException::new);

            edge.distanceToNextCircleCenterString += String.valueOf(Math.round(10 * length / rightMinPoint.distance(leftMinPoint)));
        } catch (NoSuchElementException ignored) {

        }


        int result = Math.max(leftIndex, rightIndex);
        return result;

    }

    Point2D.Double calculateCircleCenter(ModifiedWeightedEdge edge, Point2D point) throws ArithmeticException {
        Point2D p1 = point;
        Point2D p2 = graph.getEdgeSource(edge);
        Point2D p3 = graph.getEdgeTarget(edge);

        double temp = p2.getX() * p2.getX() + p2.getY() * p2.getY();
        double bc = (p1.getX() * p1.getX() + p1.getY() * p1.getY() - temp) / 2;
        double cd = (temp - p3.getX() * p3.getX() - p3.getY() * p3.getY()) / 2;

        double det = (p1.getX() - p2.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX()) * (p1.getY() - p2.getY());

        if (Math.abs(det) < 1.0e-6) {
            throw new ArithmeticException("It could be that three points are on one line.");
        }

        // Center of circle
        double cx = (bc * (p2.getY() - p3.getY()) - cd * (p1.getY() - p2.getY())) / det;
        double cy = ((p1.getX() - p2.getX()) * cd - (p2.getX() - p3.getX()) * bc) / det;

        return new Point2D.Double(cx, cy);
    }

    public void readSolution(File file) {
        try {
            //File myObj = new File("filename.txt");
            Scanner scanner = new Scanner(file);
            Point2D lastPoint = null;
            while (scanner.hasNextLine()) {
                try {


                    int pointNumber = Integer.parseInt(scanner.next());

                    Point2D point = points.stream().filter(myPoint -> myPoint.pointNumber == pointNumber).findAny().orElseThrow(NoSuchElementException::new);

                    if (lastPoint != null) {
                        graph.getEdge(lastPoint, point).setInOptTour(true);
                    }

                    lastPoint = point;

                } catch (Exception e) {
                    System.out.println("One row which can not be read.");
                    //break;
                    //e.printStackTrace();
                }

            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public double minX() {
        return points.stream().min(Comparator.comparing(Point2D::getX)).orElseThrow(NoSuchElementException::new).getX();

    }

    public double maxX() {
        return points.stream().max(Comparator.comparing(Point2D::getX)).orElseThrow(NoSuchElementException::new).getX();

    }

    public double minY() {
        return points.stream().min(Comparator.comparing(Point2D::getY)).orElseThrow(NoSuchElementException::new).getY();

    }

    public double maxY() {
        return points.stream().max(Comparator.comparing(Point2D::getY)).orElseThrow(NoSuchElementException::new).getY();

    }


    //void setAllEdgesInTour

    public void setTour(GraphPath<Point2D, ModifiedWeightedEdge> walk) {
        //DepthFirstIterator<Point2D,DefaultEdge> iterator = new DepthFirstIterator<>((Graph<Point2D, DefaultEdge>) this.getMST());


        /*for (ModifiedWeightedEdge edge : graph.edgeSet()
        ) {
            edge.setInTour(false);

        }*/

        graph.edgeSet().stream().forEach((ModifiedWeightedEdge edge) -> edge.setInTour(false));

        for (ModifiedWeightedEdge edge : walk.getEdgeList()
        ) {
            edge.setInTour(true);
        }

    }

    public void optimisation(int order, boolean reverse) {

        int from = 1;
        int to = order;
        if (reverse) {
            from = -order;
            to = -1;
        }


        for (int j = from; j <= to; j++) {

            int finalJ = Math.abs(j);
            //System.out.println(finalJ);

            boolean edit = true;
            int i = 0;
            while (i < 20) {
                edit = false;
                System.out.println(i);
                i++;

                for (ModifiedWeightedEdge edgeWithOrder : graph.edgeSet().stream().filter(myEdge -> myEdge.getUsefulDelaunayOrder() == finalJ).collect(Collectors.toCollection(ArrayList<ModifiedWeightedEdge>::new))
                ) {
                    Line2D.Double lineWithOrder = new Line2D.Double(graph.getEdgeSource(edgeWithOrder), graph.getEdgeTarget(edgeWithOrder));
                    for (ModifiedWeightedEdge edgeFromTour : tourSubgraphMask.edgeSet()
                    ) {
                        Line2D.Double lineFromTour = new Line2D.Double(graph.getEdgeSource(edgeFromTour), graph.getEdgeTarget(edgeFromTour));

                        optimizeIfLinesAreIntersectingAndNotEqual(edgeWithOrder, lineWithOrder, edgeFromTour, lineFromTour);

                    }
                }
            }
        }
    }

    private void optimizeIfLinesAreIntersectingAndNotEqual(ModifiedWeightedEdge edgeWithOrder, Line2D.Double lineWithOrder, ModifiedWeightedEdge edgeFromTour, Line2D.Double lineFromTour) {
        //boolean edit;
        if (lineWithOrder.intersectsLine(lineFromTour)) {
            if (!Objects.equals(edgeWithOrder, edgeFromTour)) {
                if (!Objects.equals(lineWithOrder.getP1(), lineFromTour.getP1()) && !Objects.equals(lineWithOrder.getP1(), lineFromTour.getP2()) && !Objects.equals(lineWithOrder.getP2(), lineFromTour.getP1()) && !Objects.equals(lineWithOrder.getP2(), lineFromTour.getP2())) {
                    double result = 0;
                    try {
                        result = findPathWhichDoNotCrossingEdge(edgeWithOrder, edgeFromTour);

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        //e.;
                    }
                    if (!(result == 0)) {
                        System.out.println(result);

                    }

                }
            }
        }
    }

    double findPathWhichDoNotCrossingEdge(ModifiedWeightedEdge doNotCrossingEdge, ModifiedWeightedEdge toAvoidEdge) {
        Point2D edgeSource = graph.getEdgeSource(doNotCrossingEdge);
        Point2D edgeTarget = graph.getEdgeTarget(doNotCrossingEdge);
        ModifiedWeightedEdge ab = graph.getEdge(edgeSource, graph.getEdgeSource(toAvoidEdge));

        ModifiedWeightedEdge cd = graph.getEdge(edgeTarget, graph.getEdgeTarget(toAvoidEdge));

        ModifiedWeightedEdge bd = graph.getEdge(edgeSource, graph.getEdgeTarget(toAvoidEdge));

        ModifiedWeightedEdge ac = graph.getEdge(edgeTarget, graph.getEdgeSource(toAvoidEdge));

        if (!toAvoidEdge.isInTour()) {

            return 0;
        }

        if (ab.isInTour() || ac.isInTour() || bd.isInTour() || cd.isInTour()) {

            //return 0;
        }

        List<Point2D> bN = Graphs.neighborListOf(tourSubgraphMask, edgeSource);
        List<Point2D> cN = Graphs.neighborListOf(tourSubgraphMask, edgeTarget);
        ModifiedWeightedEdge bStar = graph.getEdge(bN.get(0), bN.get(1));
        ModifiedWeightedEdge cStar = graph.getEdge(cN.get(0), cN.get(1));
        ModifiedWeightedEdge bN0B = graph.getEdge(edgeSource, bN.get(0));
        ModifiedWeightedEdge bN1B = graph.getEdge(edgeSource, bN.get(1));
        ModifiedWeightedEdge cN0C = graph.getEdge(edgeTarget, cN.get(0));
        ModifiedWeightedEdge cN1C = graph.getEdge(edgeTarget, cN.get(1));


        double result = 0;

        //double resultOnlyLeft = 0;


        double resultOnlyLeft = addAndRemoveEdgesWithCleanUp(new ModifiedWeightedEdge[]{bStar, ab, bd}, new ModifiedWeightedEdge[]{toAvoidEdge, bN0B, bN1B}, true);
        double resultOnlyRight = addAndRemoveEdgesWithCleanUp(new ModifiedWeightedEdge[]{cStar, ac, cd}, new ModifiedWeightedEdge[]{toAvoidEdge, cN0C, cN1C}, true);
        double resultFromLeftToRight = addAndRemoveEdgesWithCleanUp(new ModifiedWeightedEdge[]{bStar, ab, cStar, cd, doNotCrossingEdge}, new ModifiedWeightedEdge[]{toAvoidEdge, bN0B, bN1B, cN0C, cN1C}, true);
        double resultFromRightToLeft = addAndRemoveEdgesWithCleanUp(new ModifiedWeightedEdge[]{bStar, bd, cStar, ac, doNotCrossingEdge}, new ModifiedWeightedEdge[]{toAvoidEdge, bN0B, bN1B, cN0C, cN1C}, true);
        //if (Arrays.stream(new double[]{resultFromLeftToRight, resultFromRightToLeft, resultOnlyLeft, resultOnlyRight}).min().orElseThrow(NoSuchElementException::new) < 0) {

        if (Math.min(resultOnlyLeft, resultOnlyRight) < Math.min(resultFromRightToLeft, resultFromLeftToRight)) {
            if (resultOnlyLeft < resultOnlyRight) {
                return addAndRemoveEdgesWithCleanUp(new ModifiedWeightedEdge[]{bStar, ab, bd}, new ModifiedWeightedEdge[]{toAvoidEdge, bN0B, bN1B}, false);

            } else {
                return addAndRemoveEdgesWithCleanUp(new ModifiedWeightedEdge[]{cStar, ac, cd}, new ModifiedWeightedEdge[]{toAvoidEdge, cN0C, cN1C}, false);

            }
        } else {
            if (resultFromLeftToRight < resultFromRightToLeft) {
                return addAndRemoveEdgesWithCleanUp(new ModifiedWeightedEdge[]{bStar, ab, cStar, cd, doNotCrossingEdge}, new ModifiedWeightedEdge[]{toAvoidEdge, bN0B, bN1B, cN0C, cN1C}, false);

            } else {
                return addAndRemoveEdgesWithCleanUp(new ModifiedWeightedEdge[]{bStar, bd, cStar, ac, doNotCrossingEdge}, new ModifiedWeightedEdge[]{toAvoidEdge, bN0B, bN1B, cN0C, cN1C}, false);

            }
        }
        //} else {
        //   return 0;
        //}
    }

    double addAndRemoveEdgesWithCleanUp(ModifiedWeightedEdge[] addEdges, ModifiedWeightedEdge[] removeEdges, boolean cleanUp) {

        double result = 0;


        for (ModifiedWeightedEdge edge : removeEdges
        ) {
            result -= forceRemoveEdge(edge);
        }

        for (ModifiedWeightedEdge edge : addEdges
        ) {
            result += forceAddEdge(edge);
        }

        if (cleanUp) {
            for (ModifiedWeightedEdge edge : addEdges
            ) {
                forceRemoveEdge(edge);
            }

            for (ModifiedWeightedEdge edge : removeEdges
            ) {
                forceAddEdge(edge);
            }
        }


        return result;
    }

    public double forceRemoveEdge(ModifiedWeightedEdge edge) {
        double result = graph.getEdgeWeight(edge);
        edge.setInTour(false);
        return result;
    }

    public double forceAddEdge(ModifiedWeightedEdge edge) {
        double result = graph.getEdgeWeight(edge);
        edge.setInTour(true);
        return result;
    }

    Line2D lineFromEdge(ModifiedWeightedEdge edge) {
        return new Line2D.Double(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));

    }

    void printWeightOfTourAndMSTRatio() {
        System.out.println(weightOfTour());

        System.out.println("2-OPT=" + weightOfTour() / getWeightOfMinimumSpanningTree()
        );
    }

    double weightOfTour() {
        return tourSubgraphMask
                .edgeSet()
                .stream()
                .mapToDouble(graph::getEdgeWeight)
                .sum();
    }

    public double getWeightOfMinimumSpanningTree() {
        return getMST().getWeight();
    }

    public SpanningTreeAlgorithm.SpanningTree<ModifiedWeightedEdge> getMST() {
        return new KruskalMinimumSpanningTree<>(graph).getSpanningTree();
    }
}


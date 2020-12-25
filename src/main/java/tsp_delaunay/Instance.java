package tsp_delaunay;


import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.MaskSubgraph;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

class MyEdge extends DefaultWeightedEdge {
    private final ReadOnlyBooleanWrapper in_tour_v;
    private boolean in_tour;


    public MyEdge() {
        super();
        this.in_tour_v = new ReadOnlyBooleanWrapper(false);
        this.in_tour = false;
    }

    public boolean get_in_tour() {
        return in_tour;
    }

    public void set_in_tour(boolean in_tour) {
        this.in_tour = in_tour;
        Platform.runLater(() -> in_tour_v.set(in_tour));

    }

    public ReadOnlyBooleanProperty get_read_only_in_tour_property() {
        return in_tour_v.getReadOnlyProperty();
    }
}

public class Instance {


    ArrayList<Point2D> points;
    DefaultUndirectedWeightedGraph<Point2D, MyEdge> graph;
    MaskSubgraph<Point2D, MyEdge> subgraphMask;


    public Instance(File file) {


        readPointsFromFile(file);

        this.graph = new DefaultUndirectedWeightedGraph<>(MyEdge.class);
        subgraphMask = new MaskSubgraph<>(graph, (Point2D p) -> false, (MyEdge edge) -> !edge.get_in_tour());


        for (Point2D point : points
        ) {
            graph.addVertex(point);

        }

        this.points = this.points.stream().unordered().distinct().collect(Collectors
                .toCollection(ArrayList::new));

        //boolean new_array =new ArrayList < Point2D >;

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                MyEdge edge = graph.addEdge(points.get(i), points.get(j));
                graph.setEdgeWeight(edge, points.get(i).distance(points.get(j)));
            }
        }


    }

    public void tow_opt_for_non_intersecting_edges() {
        int counter = 0;
        int unaddid = 0;
        while (unaddid < this.subgraphMask.edgeSet().size() * 2) {

            counter += 1;
            System.out.println(counter);
            //i=0;
            Object[] array = this.subgraphMask.edgeSet().toArray();
            for (int i = 0; i < array.length; i++) {
                unaddid += 1;
                MyEdge edge1 = (MyEdge) array[i];
                Point2D p1 = this.graph.getEdgeSource(edge1);
                Point2D p2 = this.graph.getEdgeTarget(edge1);
                //System.out.println(i);
                for (int j = i + 1; j < array.length; j++) {
                    MyEdge edge2 = (MyEdge) array[j];
                    Point2D p3 = this.graph.getEdgeSource(edge2);
                    Point2D p4 = this.graph.getEdgeTarget(edge2);
                    if (Line2D.linesIntersect(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY())) {
                        if (!Objects.equals(edge1, edge2)) {
                            if (!Objects.equals(p1, p3) && !Objects.equals(p1, p4) && !Objects.equals(p2, p3) && !Objects.equals(p2, p4)) {

                                double result = this.solveEdgeCrossing(edge1, edge2);
                                if (result < -0.0001) {
                                    //System.out.println(submask.edgeSet().size());
                                    //System.out.println(result);
                                }
                                unaddid = 0;
                            }

                        }
                    }
                }
            }


        }
    }

    public void readPointsFromFile(File file) {
        this.points = new ArrayList<>();

        try {
            //File myObj = new File("filename.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                try {
                    int point_number = Integer.parseInt(scanner.next());
                    double x = Double.parseDouble(scanner.next());
                    double y = Double.parseDouble(scanner.next());
                    this.points.add(new Point2D.Double(x, y));
                } catch (Exception e) {
                    System.out.println("Es gibt eine Zeile in dem File die man nicht lesen kann.");
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

    public double min_x() {
        return points.stream().min(Comparator.comparing(Point2D::getX)).orElseThrow(NoSuchElementException::new).getX();

    }

    public double max_x() {
        return points.stream().max(Comparator.comparing(Point2D::getX)).orElseThrow(NoSuchElementException::new).getX();


    }

    public double min_y() {
        return points.stream().min(Comparator.comparing(Point2D::getY)).orElseThrow(NoSuchElementException::new).getY();

    }

    public double max_y() {
        return points.stream().max(Comparator.comparing(Point2D::getY)).orElseThrow(NoSuchElementException::new).getY();


    }

    public SpanningTreeAlgorithm.SpanningTree<MyEdge> getMST() {
        return new KruskalMinimumSpanningTree<>(graph).getSpanningTree();
    }

    public void setTour(GraphPath<Point2D, MyEdge> walk) {
        //DepthFirstIterator<Point2D,DefaultEdge> iterator = new DepthFirstIterator<>((Graph<Point2D, DefaultEdge>) this.getMST());


        for (MyEdge edge : this.graph.edgeSet()
        ) {
            edge.set_in_tour(false);

        }

        for (MyEdge edge : walk.getEdgeList()
        ) {
            edge.set_in_tour(true);
        }


    }


    public double addEdge(MyEdge edge) {
        double result = graph.getEdgeWeight(edge);
        edge.set_in_tour(true);
        return result;
    }

    public double removeEdge(MyEdge edge) {
        double result = graph.getEdgeWeight(edge);
        edge.set_in_tour(false);
        return result;
    }


    public double solveEdgeCrossing(MyEdge edge1, MyEdge edge2) {
        MyEdge add1_first = this.graph.getEdge(this.graph.getEdgeSource(edge1), this.graph.getEdgeSource(edge2));

        MyEdge add2_first = this.graph.getEdge(this.graph.getEdgeTarget(edge1), this.graph.getEdgeTarget(edge2));

        MyEdge add1_secound = this.graph.getEdge(this.graph.getEdgeSource(edge1), this.graph.getEdgeTarget(edge2));

        MyEdge add2_secound = this.graph.getEdge(this.graph.getEdgeTarget(edge1), this.graph.getEdgeSource(edge2));

        try {
            if ((add1_first.get_in_tour() || add2_first.get_in_tour()) && (add1_secound.get_in_tour() || add2_secound.get_in_tour())) {
                return 0;
            }
        } catch (NullPointerException e) {
            return 0;
        }

        if (!edge1.get_in_tour() || !edge2.get_in_tour()) {
            return 0;
        }

        double result = 0;
        ConnectivityInspector<Point2D, MyEdge> connectivityInspector = new ConnectivityInspector<>(this.subgraphMask);
        double result1 = 0;
        double result2 = result1;
        double result3 = result1;
        if (connectivityInspector.isConnected()) {
            //System.out.println(result);
        }


        result -= this.removeEdge(edge1);
        result -= this.removeEdge(edge2);


        if (!add1_first.get_in_tour() && !add2_first.get_in_tour()) {
            try {


                result += this.addEdge(add1_first);

                result += this.addEdge(add2_first);

                connectivityInspector = new ConnectivityInspector<>(this.subgraphMask);
                if (connectivityInspector.isConnected() && result < 0) {
                    //System.out.println(result);
                    result2 = result;
                }
                result -= this.removeEdge(add1_first);
                result -= this.removeEdge(add2_first);
            } catch (NullPointerException ignore) {

            }
        }

        if (!add1_secound.get_in_tour() && !add2_secound.get_in_tour()) {

            try {

                result += this.addEdge(add1_secound);
                result += this.addEdge(add2_secound);
                connectivityInspector = new ConnectivityInspector<>(this.subgraphMask);
                if (connectivityInspector.isConnected() && result < 0) {
                    //System.out.println(result);
                    result3 = result;
                }
                result -= this.removeEdge(add1_secound);
                result -= this.removeEdge(add2_secound);
            } catch (NullPointerException ignore) {

            }

        }

        result += this.addEdge(edge1);
        result += this.addEdge(edge2);


        if (Math.min(result2, result3) < 0) {
            result -= this.removeEdge(edge1);
            result -= this.removeEdge(edge2);
            if (result2 < result3) {
                result += this.addEdge(add1_first);

                result += this.addEdge(add2_first);
            } else {
                result += this.addEdge(add1_secound);
                result += this.addEdge(add2_secound);
            }
        }
        return result;

    }


}


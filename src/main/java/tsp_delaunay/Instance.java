package tsp_delaunay;


//import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
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

        this.points = this.points.stream().unordered()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        //boolean new_array =new ArrayList < Point2D >;

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                MyEdge edge = graph.addEdge(points.get(i), points.get(j));
                graph.setEdgeWeight(edge, points.get(i).distance(points.get(j)));
                int order = this.set_usefull_order_off_edge(edge, 5);
                edge.set_use_full_order(order);
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

    int set_usefull_order_off_edge(MyEdge edge, int limit) {
        Point2D source = this.graph.getEdgeSource(edge);
        Point2D target = this.graph.getEdgeTarget(edge);
        Line2D line = new Line2D.Double(source, target);

        ArrayList<Point2D> left_points = new ArrayList<Point2D>();
        ArrayList<Point2D> right_points = new ArrayList<Point2D>();
        int on_line = 0;

        for (Point2D point : this.points
        ) {

            if (point.equals(source) || point.equals(target)) {
                continue;
            }

            if (line.ptLineDist(point) > 0) {


                if (line.relativeCCW(point) > 0) {
                    right_points.add(point);
                } else {
                    left_points.add(point);
                }
            } else {
                if (line.ptSegDist(point) > 0) {

                } else {
                    on_line += 1;
                }

            }


        }

        double[] left_points_sorted = left_points.stream().mapToDouble((Point2D point) -> {
            Point2D.Double center = this.calculate_circle_center(edge, point);
            return line.ptLineDist(center) * line.relativeCCW(center);
        }).sorted().toArray();
        double[] right_points_sorted = right_points.stream().mapToDouble((Point2D point) -> {
            Point2D.Double center = this.calculate_circle_center(edge, point);
            return line.ptLineDist(center) * line.relativeCCW(center);
        }).sorted().toArray();


        int right_index = 0;
        int left_index = 0;
        try {
            double left_max = left_points_sorted[left_points_sorted.length - 1];
            right_index = 0;
            while (left_max > right_points_sorted[right_index]) {
                right_index += 1;
            }

            double test_r = right_points_sorted[right_index + 1];
            double right_min = right_points_sorted[0];
            left_index = 0;
            while (right_min < left_points_sorted[left_points_sorted.length - (left_index + 1)]) {
                left_index += 1;
            }
            double test_l = left_points_sorted[left_points_sorted.length - (left_index + 1 + 1)];
        } catch (Exception e) {
            return limit;
        }


        return Math.max(left_index, right_index);


    }

    Point2D.Double calculate_circle_center(MyEdge edge, Point2D point) {
        Point2D p1 = point;
        Point2D p2 = this.graph.getEdgeSource(edge);
        Point2D p3 = this.graph.getEdgeTarget(edge);

        double temp = p2.getX() * p2.getX() + p2.getY() * p2.getY();
        double bc = (p1.getX() * p1.getX() + p1.getY() * p1.getY() - temp) / 2;
        double cd = (temp - p3.getX() * p3.getX() - p3.getY() * p3.getY()) / 2;

        double det = (p1.getX() - p2.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX()) * (p1.getY() - p2.getY());

        if (Math.abs(det) < 1.0e-6) {
            throw new ValueException("Die drei Punkte liegen warscheinlich auf einer Line");
        }

        // Center of circle
        double cx = (bc * (p2.getY() - p3.getY()) - cd * (p1.getY() - p2.getY())) / det;
        double cy = ((p1.getX() - p2.getX()) * cd - (p2.getX() - p3.getX()) * bc) / det;

        return new Point2D.Double(cx, cy);
    }


}


package tsp_delaunay;


import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

/*public class Edge extends DefaultEdge{
    boolean in_tour;

    public Edge() {
        this.in_tour = false;
    }
}*/

public class Instance {


    ArrayList<Point2D> points;
    DefaultUndirectedWeightedGraph<Point2D, DefaultEdge> graph;


    public Instance(File file) {


        readPointsFromFile(file);

        this.graph = new DefaultUndirectedWeightedGraph<>(DefaultEdge.class);

        for (Point2D point : points
        ) {
            graph.addVertex(point);

        }

        this.points = this.points.stream().unordered().distinct().collect(Collectors
                .toCollection(ArrayList::new));

        //boolean new_array =new ArrayList < Point2D >;

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                DefaultEdge edge = graph.addEdge(points.get(i), points.get(j));
                graph.setEdgeWeight(edge, points.get(i).distance(points.get(j)));
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

    public SpanningTreeAlgorithm.SpanningTree<DefaultEdge> getMST() {
        return new KruskalMinimumSpanningTree<>(graph).getSpanningTree();
    }

    public void produceTour() {
        //DepthFirstIterator<Point2D,DefaultEdge> iterator = new DepthFirstIterator<>((Graph<Point2D, DefaultEdge>) this.getMST());


    }


}


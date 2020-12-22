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
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                DefaultEdge edge = graph.addEdge(points.get(i), points.get(j));
                graph.setEdgeWeight(edge, points.get(i).distance(points.get(j)));
            }
        }


    }

    public void readPointsFromFile(File file) {
        this.points = new ArrayList<Point2D>();

        try {
            //File myObj = new File("filename.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                try {
                    int point_number = Integer.parseInt(scanner.next());
                    double x = Double.parseDouble(scanner.next());
                    double y = Double.parseDouble(scanner.next());
                    this.points.add(new Point2D.Double(x, y) {
                    });
                } catch (Exception e) {
                    System.out.println("Es gibt eine Zeile in dem File die man nicht lesen kann.");
                    //e.printStackTrace();
                }

                //String data = myReader.nextLine();
                //System.out.println(data);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public Point2D min_x() {
        return points.stream().min(Comparator.comparing(Point2D::getX)).orElseThrow(NoSuchElementException::new);

    }

    public Point2D max_x() {
        return points.stream().max(Comparator.comparing(Point2D::getX)).orElseThrow(NoSuchElementException::new);


    }

    public Point2D min_y() {
        return points.stream().min(Comparator.comparing(Point2D::getY)).orElseThrow(NoSuchElementException::new);

    }

    public Point2D max_y() {
        return points.stream().max(Comparator.comparing(Point2D::getY)).orElseThrow(NoSuchElementException::new);


    }

    public SpanningTreeAlgorithm.SpanningTree<DefaultEdge> getMST() {
        return ((KruskalMinimumSpanningTree<Point2D, DefaultEdge>) new KruskalMinimumSpanningTree(graph)).getSpanningTree();
    }
}


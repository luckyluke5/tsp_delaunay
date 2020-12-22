package tsp_delaunay;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;
import org.jgrapht.graph.DefaultEdge;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Random;

public class tsp_view extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        File instance_file = new File("Beispiel2(280).txt");
        Instance instance = new Instance(instance_file);
        Group subview_root = new Group();
        StackPane mainview_root = new StackPane();
        Scene scene = new Scene(mainview_root, 640, 480);


        SubScene subScene = new SubScene(subview_root, instance.max_x() - instance.min_x(), instance.max_y() - instance.min_y());
        subScene.setFill(Color.LIGHTGRAY);

        NumberBinding scale_height = Bindings.divide(scene.heightProperty(), subScene.heightProperty());
        NumberBinding scale_width = Bindings.divide(scene.widthProperty(), subScene.widthProperty());

        //double x_scale = (scene.getWidth() / subScene.getWidth());

        NumberBinding scale = Bindings.min(scale_height, scale_width);

        subScene.scaleXProperty().bind(scale);
        subScene.scaleYProperty().bind(scale);

        //subScene.setLayoutX(0);
        //subScene.setLayoutX(0);

        //double scale = Math.min(x_scale, y_scale);

        //subScene.getTransforms().add(new Scale(scale, scale, 0, 0));
        //subScene.getTransforms().add(new Scale(1, -1, 0, subScene.getHeight() / 2));
        //subScene.getTransforms().add(new Translate(0,-20));
        subview_root.getTransforms().add(new Translate(-instance.min_x(), -instance.min_y()));
        subview_root.getTransforms().add(new Scale(-0.9, 0.9, instance.min_x() + (instance.max_x() - instance.min_x()) / 2, instance.min_y() + (instance.max_y() - instance.min_y()) / 2));
        //ew Scale(1, 2)

        mainview_root.getChildren().add(subScene);
        //mainview_root.setMargin(subScene,new Insets(15));


        //x=x_min+a*(x_max-x_min)
        //       xd=a*x_diff


        for (Point2D point : instance.points
        ) {


            subview_root.getChildren().add(new Circle(point.getX(), point.getY(), 3));


        }

        for (DefaultEdge edge : instance.getMST().getEdges()
        ) {
            Point2D source = instance.graph.getEdgeSource(edge);
            Point2D target = instance.graph.getEdgeTarget(edge);
            subview_root.getChildren().add(new Line(source.getX(), source.getY(), target.getX(), target.getY()));

        }

        GraphPath<Point2D, DefaultEdge> mst_tour = new TwoApproxMetricTSP<Point2D, DefaultEdge>().getTour(instance.graph);
        GraphPath<Point2D, DefaultEdge> christofides_tour = new ChristofidesThreeHalvesApproxMetricTSP<Point2D, DefaultEdge>().getTour(instance.graph);

        for (DefaultEdge edge : mst_tour.getEdgeList()
        ) {
            Point2D source = instance.graph.getEdgeSource(edge);
            Point2D target = instance.graph.getEdgeTarget(edge);
            Line line = new Line(source.getX(), source.getY(), target.getX(), target.getY());
            line.setStroke(Color.GREEN);

            subview_root.getChildren().add(line);

        }

        for (DefaultEdge edge : christofides_tour.getEdgeList()
        ) {
            Point2D source = instance.graph.getEdgeSource(edge);
            Point2D target = instance.graph.getEdgeTarget(edge);
            Line line = new Line(source.getX(), source.getY(), target.getX(), target.getY());
            line.setStroke(Color.RED);

            subview_root.getChildren().add(line);

        }


        /*
        Scene scene = new Scene(root, 200, 150);
        scene.setFill(Color.LIGHTGRAY);
        */

        Circle circle = new Circle(60, 40, 30, Color.GREEN);

        Text text = new Text(10, 90, "JavaFX Scene");
        text.setFill(Color.DARKRED);

        Font font = new Font(20);
        text.setFont(font);

        //root.getChildren().add(circle);
        //root.getChildren().add(text);
//      stage.setScene(scene);
        String[] points = {"Ag", "cd", "xy"};
        Random wuerfel = new Random();

        for (String x : points
        ) {
            //System.out.println(x);
            Text text_new = new Text(wuerfel.nextInt(640), wuerfel.nextInt(480), x);
            text.setFill(Color.DARKRED);

            text.setFont(font);
            //root.getChildren().add(text_new);

        }


        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        //root.getChildren().add(l);


        ObservableList<Node> list = subview_root.getChildren();
        //Text first_text = (Text) list.get(1);


        primaryStage.setScene(scene);

        primaryStage.show();


    }
}

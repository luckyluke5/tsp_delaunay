package tsp_delaunay;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;

import java.awt.geom.Point2D;
import java.io.File;


class Two_Opt_Class extends Task<Void> {
    final Instance instance;


    public Two_Opt_Class(Instance instance) {
        this.instance = instance;

    }

    @Override
    protected Void call() {
        this.instance.tow_opt_for_non_intersecting_edges();

        System.out.println("2-OPT=" + this
                .instance
                .subgraphMask
                .edgeSet()
                .stream()
                .mapToDouble((MyEdge edge) -> instance.graph.getEdgeWeight(edge))
                .sum() / instance.getMST().getWeight()
        );

        return null;
    }
}

public class tsp_view extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        File instance_file = new File("Beispiel3(52).txt");
        Instance instance = new Instance(instance_file);
        Group subview_root = new Group();
        StackPane mainview_root = new StackPane();
        Scene scene = new Scene(mainview_root, 640, 480);


        double x_diff = instance.max_x() - instance.min_x();
        double y_diff = instance.max_y() - instance.min_y();
        double diff = Math.min(x_diff, y_diff);

        SubScene subScene = new SubScene(subview_root, x_diff, y_diff);
        subScene.setFill(Color.LIGHTGRAY);

        NumberBinding scale_height = Bindings.divide(scene.heightProperty(), subScene.heightProperty());
        NumberBinding scale_width = Bindings.divide(scene.widthProperty(), subScene.widthProperty());
        NumberBinding scale = Bindings.min(scale_height, scale_width);

        subScene.scaleXProperty().bind(scale);
        subScene.scaleYProperty().bind(scale);

        subview_root.getTransforms().add(new Translate(-instance.min_x(), -instance.min_y()));
        subview_root.getTransforms().add(new Scale(0.9, -0.9, instance.min_x() + (instance.max_x() - instance.min_x()) / 2, instance.min_y() + (instance.max_y() - instance.min_y()) / 2));

        mainview_root.getChildren().add(subScene);
        CheckBox order0 = new CheckBox("Order 0");
        CheckBox order1 = new CheckBox("Order 1");
        CheckBox order2 = new CheckBox("Order 2");
        CheckBox order3 = new CheckBox("Order 3");
        StackPane hbox_groop = new StackPane();
        hbox_groop.getChildren().add(order0);
        hbox_groop.getChildren().add(order1);
        hbox_groop.getChildren().add(order2);
        hbox_groop.getChildren().add(order3);
        HBox hbox = new HBox(order0, order1, order2, order3);
        mainview_root.getChildren().add(hbox);

        int factor = 300;

        for (Point2D point : instance.points
        ) {


            subview_root.getChildren().add(new Circle(point.getX(), point.getY(), diff / factor * 2));


        }

        for (MyEdge edge : instance.graph.edgeSet()
        ) {
            Point2D source = instance.graph.getEdgeSource(edge);
            Point2D target = instance.graph.getEdgeTarget(edge);
            Line line = new Line(source.getX(), source.getY(), target.getX(), target.getY());
            Line line1 = new Line(source.getX(), source.getY(), target.getX(), target.getY());
            Line line2 = new Line(source.getX(), source.getY(), target.getX(), target.getY());

            line.setStrokeWidth(diff / factor);
            line1.setStrokeWidth(diff / factor);
            line2.setStrokeWidth(diff / factor);


            //new SimpleBooleanProperty(edge.in_tour);
            //ReadOn
            line.visibleProperty().bind(edge.get_read_only_in_tour_property());

            line1.visibleProperty().bind(edge.get_read_only_use_full_order_property().isEqualTo(1).and(order1.selectedProperty()));
            line1.setStroke(Color.RED);

            line2.visibleProperty().bind(edge.get_read_only_use_full_order_property().isEqualTo(2).and(order2.selectedProperty()));
            line2.setStroke(Color.BLUE);

            subview_root.getChildren().add(line);
            subview_root.getChildren().add(line1);
            subview_root.getChildren().add(line2);

        }


        SpanningTreeAlgorithm.SpanningTree<MyEdge> mst = instance.getMST();
//

        GraphPath<Point2D, MyEdge> mst_tour = new TwoApproxMetricTSP<Point2D, MyEdge>().getTour(instance.graph);
        GraphPath<Point2D, MyEdge> christofides_tour = new ChristofidesThreeHalvesApproxMetricTSP<Point2D, MyEdge>().getTour(instance.graph);

        instance.setTour(mst_tour);


        Task<Void> task = new Two_Opt_Class(instance);
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();


        double mst_ratio = mst_tour.getWeight() / mst.getWeight();
        double chr_ratio = christofides_tour.getWeight() / mst.getWeight();

        System.out.println("MST=" + mst.getWeight());
        System.out.println("MST-Tour=" + mst_ratio);
        System.out.println("Christopides-Tour=" + chr_ratio);






        primaryStage.setScene(scene);

        primaryStage.show();
//

    }

}

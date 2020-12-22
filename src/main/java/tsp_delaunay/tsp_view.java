package tsp_delaunay;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
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

        File instance_file = new File("Beispiel3(52).txt");
        Instance instance = new Instance(instance_file);
        Group subview_root = new Group();
        Group mainview_root = new Group();
        Scene scene = new Scene(mainview_root, 640, 480);

        DoubleProperty min_x_properti = new SimpleDoubleProperty();
        min_x_properti.setValue(instance.min_x().getX());

        DoubleProperty max_x_properti = new SimpleDoubleProperty();
        max_x_properti.setValue(instance.max_x().getX());

        DoubleProperty min_y_properti = new SimpleDoubleProperty();
        min_y_properti.setValue(instance.min_y().getY());

        DoubleProperty max_y_properti = new SimpleDoubleProperty();
        max_y_properti.setValue(instance.max_y().getY());

        ReadOnlyDoubleProperty y_diff = scene.heightProperty();
        ReadOnlyDoubleProperty x_diff = scene.widthProperty();

        NumberBinding x_max_minus_x_min = Bindings.subtract(max_x_properti, min_x_properti);
        NumberBinding y_max_minus_y_min = Bindings.subtract(max_y_properti, min_y_properti);

        SubScene subScene = new SubScene(subview_root, x_max_minus_x_min.intValue(), y_max_minus_y_min.intValue());
        subScene.setFill(Color.LIGHTGRAY);

        double y_scale = (scene.getHeight() / subScene.getHeight());
        double x_scale = (scene.getWidth() / subScene.getWidth());

        double scale = Math.max(x_scale, y_scale);

        //subScene.setScaleY(-1*scale*0.9);
        //subScene.setScaleX(scale*0.9);
        subScene.getTransforms().add(new Scale(scale, scale, 0, 0));
        subScene.getTransforms().add(new Scale(1, -1, 0, subScene.getHeight() / 2));
        //subScene.getTransforms().add(new Translate(0,-20));

        subview_root.getTransforms().add(new Scale(0.9, 0.9));
        subview_root.getTransforms().add(new Translate(0, 100));
        //subScene.setTranslateX(400);
        //subScene.setTranslateY(400);

        mainview_root.getChildren().add(subScene);


        //x=x_min+a*(x_max-x_min)
        //       xd=a*x_diff


        for (Point2D point : instance.points
        ) {

            /*DoubleProperty x=new SimpleDoubleProperty();
            x.setValue(point.getX());

            DoubleProperty y=new SimpleDoubleProperty();
            y.setValue(point.getY());

            NumberBinding x_minus_x_min = Bindings.subtract(x, min_x_properti);
            NumberBinding a = Bindings.divide(x_minus_x_min, x_max_minus_x_min);

            NumberBinding y_minus_y_min = Bindings.subtract(y, min_y_properti);
            NumberBinding b = Bindings.divide(y_minus_y_min, y_max_minus_y_min);*/

            subview_root.getChildren().add(new Circle(point.getX(), point.getY(), 3));
            
            
            /*Circle circle = new Circle(point.getX(), point.getY(), 3, Color.GREEN);
            circle.centerXProperty().bind( Bindings.multiply(a,x_diff));
            circle.centerYProperty().bind(Bindings.multiply(b,y_diff));
            subview_root.getChildren().add(circle);*/
        }

        for (DefaultEdge edge : instance.getMST().getEdges()
        ) {
            Point2D source = instance.graph.getEdgeSource(edge);
            Point2D target = instance.graph.getEdgeTarget(edge);
            subview_root.getChildren().add(new Line(source.getX(), source.getY(), target.getX(), target.getY()));

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

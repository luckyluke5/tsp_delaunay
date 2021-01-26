package tsp.delaunay;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.alg.tour.TwoApproxMetricTSP;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.function.Function;


public class MainView extends Application {
    Instance instance;
    CheckBox triangulation;
    CheckBox computedTour;
    CheckBox order0;
    CheckBox order1;
    CheckBox order2;
    CheckBox order3;
    CheckBox order4;
    CheckBox order10;
    CheckBox order20;
    CheckBox order30;
    CheckBox order40;
    CheckBox convexHull;
    CheckBox optTour;
    Group points;
    Group edges;
    Pane visualisationPane;
    double scale;
    private double dragGroupStartX;
    private double dragGroupStartY;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        File instanceFile = new File("Beispiel1(7).txt");
        instance = new Instance(instanceFile);
        instance.readSolution(new File("Bsp1.txt"));

        points = new Group();
        edges = new Group();


        Pane clipPane = new Pane();

        visualisationPane = new Pane();
        visualisationPane.getChildren().add(edges);
        visualisationPane.getChildren().add(points);


        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(clipPane);
        clipPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                new CornerRadii(3), BorderStroke.THIN)));

        visualisationPane.setOnZoomStarted(event -> {
            System.out.println(event.getX());
            System.out.println(event.getY());
            double factor = Math.pow(event.getTotalZoomFactor(), 50);

            visualisationPane.setScaleX(visualisationPane.getScaleX() * factor);
            visualisationPane.setScaleY(visualisationPane.getScaleY() * factor);

            // TODO: 25.01.21 den Zoom so gestalten, dass er an dem ort zoomt wo man den zoom ansetzt

            //visualisationPane.setTranslateX(visualisationPane.getTranslateX() - event.getX()*(factor-1));
            //visualisationPane.setTranslateY(visualisationPane.getTranslateY() - event.getY()*(factor-1));
        });

        visualisationPane.setOnScroll(event -> {

            visualisationPane.setTranslateX(visualisationPane.getTranslateX() + event.getDeltaX());
            visualisationPane.setTranslateY(visualisationPane.getTranslateY() + event.getDeltaY());
        });


        visualisationPane.setOnMousePressed(event -> {

            dragGroupStartX = event.getX();
            dragGroupStartY = event.getY();
        });

        visualisationPane.setOnMouseDragged(event -> {

            javafx.geometry.Point2D startTranslate = visualisationPane.localToParent(dragGroupStartX, dragGroupStartY);
            javafx.geometry.Point2D endTranslate = visualisationPane.localToParent(event.getX(), event.getY());
            visualisationPane.setTranslateX(visualisationPane.getTranslateX() + (endTranslate.getX() - startTranslate.getX()));
            visualisationPane.setTranslateY(visualisationPane.getTranslateY() + (endTranslate.getY() - startTranslate.getY()));

        });

        visualisationPane.setOnMouseClicked(event -> {
            System.out.println("point::mouse::clicked");
            //System.out.println(event.getY())

            if (dragGroupStartX == event.getX() && dragGroupStartY == event.getY()) {

                MyPoint newPoint = new MyPoint(-1, event.getX(), event.getY());
                instance.points.add(newPoint);
                Circle circle = new Circle(event.getX(), event.getY(), scale * 4);
                points.getChildren().add(circle);

                makeCircleDraggable(newPoint, circle);
                instance.createGraphAndEdgesFromPoints();
                generateLinesForEachCheckboxAndEdge();
            }


            event.consume();

        });


        //ScrollPane scrollPane=new ScrollPane();
        //scrollPane.setContent(visualisationPane);




        /*pane2.setCenter(new Text("test"));
        pane2.setLeft(new Text("Left"));
        pane2.setRight(new Text("Right"));*/
        //visualisationPane.setBackground(new Background(new BackgroundFill(Color.Re)));

        /*Circle circle2=new Circle(10);
        circle2.setCenterX(100);
        circle2.setCenterY(100);
        visualisationPane.getChildren().add(circle2);
        circle2.setFill(Color.GREEN);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                System.out.println("Hello World");
                circle2.setFill(Color.DARKSLATEBLUE);
            }
        };
        //Registering the event filter
        circle2.addEventFilter(MouseEvent.MOUSE_ENTERED, eventHandler);
*/
        //visualisationPane.pref
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(anchorPane);

        Rectangle region = new Rectangle();
        region.widthProperty().bind(clipPane.widthProperty());
        region.heightProperty().bind(clipPane.heightProperty());
        clipPane.setClip(region);
        //borderPane.se
        //borderPane
        //borderPane.mouseTransparentProperty().setValue(true);
        Scene scene = new Scene(borderPane, 640, 480);
        //scene.setFill(Color.RED);


        double xDiff = instance.maxX() - instance.minX();
        double yDiff = instance.maxY() - instance.minY();
        double diff = Math.min(xDiff, yDiff);

        //SubScene subScene = new SubScene(visualisationPane, xDiff, yDiff);
        //Region subScene = new Region();

        //subScene.setPrefSize(xDiff,yDiff);

        //subScene.setFill(Color.LIGHTGRAY);


        //subScene.setPickOnBounds(true);

        //NumberBinding scaleHeight = Bindings.divide(scene.heightProperty(), visualisationPane.heightProperty());

        //scene.getHeight()/ clipPane.getHeight();


        //NumberBinding scaleWidth = Bindings.divide(scene.widthProperty(), visualisationPane.widthProperty());
        //NumberBinding scale = Bindings.min(scaleHeight, scaleWidth);

        //visualisationPane.scaleXProperty().bind(scale);
        //visualisationPane.scaleYProperty().bind(scale);

        //visualisationPane.setMinHeight(480);
        //visualisationPane.setMinWidth(640);


        //visualisationPane.getTransforms().add(new Translate(-instance.minX(), -instance.minY()));
        //visualisationPane.getTransforms().add(new Scale(0.9, -0.9, instance.minX() + (instance.maxX() - instance.minX()) / 2, instance.minY() + (instance.maxY() - instance.minY()) / 2));
        //visualisationPane

        //clipPane.getTransforms().add(new Translate(10,10));

        EventHandler<MouseEvent> a = event -> System.out.println("mouse click detected! " + event.getSource());
        //visualisationPane.addEventFilter(MOUSE_ENTERED,a);
        //visualisationPane.addEventHandler(MOUSE_ENTERED,a);


        int factor = 300;

        scale = diff / factor;

        computedTour = new CheckBox("Tour");
        triangulation = new CheckBox("Triangulation");
        computedTour.setSelected(true);
        //triangulation.setSelected(true);
        order0 = new CheckBox("Order 0");
        order1 = new CheckBox("Order 1");
        order2 = new CheckBox("Order 2");
        order3 = new CheckBox("Order 3");
        order4 = new CheckBox("Order 4");

        //Function<ModifiedWeightedEdge, ReadOnlyBooleanProperty> a = ;
        order10 = new CheckBox("Order 10");
        order20 = new CheckBox("Order 20");
        order30 = new CheckBox("Order 30");
        order40 = new CheckBox("Order 40");

        convexHull = makeCheckboxWithLines(diff / factor, Color.RED, ModifiedWeightedEdge::getReadOnlyPartOfConvexHullProperty, "ConvexHull", visualisationPane);
        optTour = makeCheckboxWithLines(diff / factor, Color.GREEN, ModifiedWeightedEdge::inOptTourProperty, "OptTour", visualisationPane);

        CheckBox smallMaxDist = makeCheckboxWithLines(diff / factor, Color.CORAL, modifiedWeightedEdge -> {
            if (modifiedWeightedEdge.leftDistance != null && modifiedWeightedEdge.rightDistance != null) {
                modifiedWeightedEdge.maxDistance = Math.min(modifiedWeightedEdge.leftDistance, modifiedWeightedEdge.rightDistance);
            } else {
                modifiedWeightedEdge.maxDistance = 100.0;
            }
            if (modifiedWeightedEdge.getUsefulDelaunayOrder() == 1) {
                System.out.println(modifiedWeightedEdge.maxDistance);
            }

            //if (modifiedWeightedEdge.maxDistance!=null) {
            // bei order 1 ist 1 eine gute grenze
            return new ReadOnlyBooleanWrapper(modifiedWeightedEdge.maxDistance < 25 && modifiedWeightedEdge.getUsefulDelaunayOrder() == 1);
            //}
        }, "smallMaxDist", visualisationPane);

        VBox vBox = new VBox(computedTour, triangulation, order0, order1, order2, order3, order4, order10, order20, order30, order40, convexHull, optTour, smallMaxDist);
        borderPane.setLeft(vBox);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        //vBox.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        //visualisationPane.setPadding(new Insets(100,100,100,100));


        for (Point2D point : instance.points
        ) {
            Circle circle = new Circle(point.getX(), point.getY(), diff / factor * 4);


            makeCircleDraggable(point, circle);

            points.getChildren().add(circle);

        }


        generateLinesForEachCheckboxAndEdge();

        clipPane.getChildren().add(visualisationPane);
        //BorderPane pane2 = new BorderPane();
        //clipPane.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        //visualisationPane.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        /*clipPane.setPadding(new Insets(50,50,50,50));
        clipPane.setLayoutX(10);
        clipPane.setLayoutY(10);*/

        System.out.println(clipPane.isResizable());



        /*System.out.println(visualisationPane.getLayoutBounds());
        System.out.println(visualisationPane.getBoundsInLocal());
        System.out.println(visualisationPane.getBoundsInParent());
        System.out.println(visualisationPane.getLayoutX());
        System.out.println(visualisationPane.getLayoutY());*/

        //visualisationPane.getTransforms().add(new )

        //visualisationPane.setLayoutX(100);
        //visualisationPane.setLayoutY(100);

        //Region.layoutInArea(visualisationPane,100,100,10,10,0,new Insets(10,10,10,10),false,false,HPos.LEFT, VPos.TOP,false);

        //visualisationPane.getTransforms().add(new Translate(visualisationPane.getBoundsInLocal().getMinX(), visualisationPane.getBoundsInLocal().getMinY()+200));

        double min = Math.max(scene.getWidth() / visualisationPane.getBoundsInLocal().getWidth(), scene.getHeight() / visualisationPane.getBoundsInLocal().getHeight());
        visualisationPane.getTransforms().add(new Scale(min, min, -visualisationPane.getBoundsInLocal().getMinX(), -visualisationPane.getBoundsInLocal().getMinY()));
        visualisationPane.getTransforms().add(new Scale(1, -1, visualisationPane.getBoundsInLocal().getCenterX(), visualisationPane.getBoundsInLocal().getCenterY()));
        SpanningTreeAlgorithm.SpanningTree<ModifiedWeightedEdge> mst = instance.getMST();
//

        AnchorPane.setBottomAnchor(clipPane, 10.0);
        AnchorPane.setTopAnchor(clipPane, 10.0);
        AnchorPane.setLeftAnchor(clipPane, 10.0);
        AnchorPane.setRightAnchor(clipPane, 10.0);

        GraphPath<Point2D, ModifiedWeightedEdge> mstTour = new TwoApproxMetricTSP<Point2D, ModifiedWeightedEdge>().getTour(instance.graph);
        GraphPath<Point2D, ModifiedWeightedEdge> christofidesTour = new ChristofidesThreeHalvesApproxMetricTSP<Point2D, ModifiedWeightedEdge>().getTour(instance.graph);
        ConvexHullAlgorithm algo = new ConvexHullAlgorithm(instance);
        //algo.startAlgorithm();
        instance.setTour(christofidesTour);
        new TwoOptSolver(instance).towOptForNonIntersectingEdges();

        //new TriangulationBuilder(instance).initialTriangulationWithSetEdges();

        //System.out.println(instance.weightOfTour());

        /*for (MyEdge edge: instance.graph.edgeSet()
        ) {
            if (edge.get_use_full_order()==1 && !edge.get_in_triangulation()){
                instance.modify_triangulation_and_force_edge(edge);
            }

        }

         */


        //instance.tow_opt_for_non_intersecting_edges();

        /*    int i = 1;

        KOptSolver kOptSplver = new KOptSolver(instance);

        for (ModifiedWeightedEdge edge: instance.graph.edgeSet()
        ) {
            if (edge.getUsefulDelaunayOrder()== i && !edge.isInTriangulation()){
                //try {
                kOptSplver.modifyTriangulationAndForceEdge(edge);
                //} catch (InterruptedException e) {
                //    e.printStackTrace();
                //}
            }

        }

        for (ModifiedWeightedEdge edge: instance.graph.edgeSet()
        ) {
            if (edge.getUsefulDelaunayOrder()==2 && !edge.isInTriangulation()){
                //kOptSplver.modify_triangulation_and_force_edge(edge);
            }

        }

        for (ModifiedWeightedEdge edge: instance.graph.edgeSet()
        ) {
            if (edge.getUsefulDelaunayOrder()==3 && !edge.isInTriangulation()){
                //instance.modify_triangulation_and_force_edge(edge);
            }

        }

        for (ModifiedWeightedEdge edge: instance.graph.edgeSet()
        ) {
            if (edge.getUsefulDelaunayOrder()==4 && !edge.isInTriangulation()){
                //instance.modify_triangulation_and_force_edge(edge);
            }

        }

        for (ModifiedWeightedEdge edge: instance.graph.edgeSet()
        ) {
            if (edge.getUsefulDelaunayOrder()==0 && !edge.isInTriangulation()){
                //instance.modify_triangulation_and_force_edge(edge);
            }

        }


        for (ModifiedWeightedEdge edge: instance.triangulationSubgraphMask.edgeSet()
        ) {
            //System.out.println(instance.graph.getEdgeSource(edge)+" "+instance.graph.getEdgeTarget(edge));
        }

        */

        Task<Void> task = new TwoOptClass(instance);
        Thread th = new Thread(task);
        th.setDaemon(true);
        //th.start();


        //Platform.runLater(th::start);


        double mstRatio = mstTour.getWeight();
        double chrRatio = christofidesTour.getWeight();

        System.out.println("MST=" + mst.getWeight());
        System.out.println("MST-Tour=" + mstRatio);
        System.out.println("Christopides-Tour=" + chrRatio);


        System.out.println(instance.weightOfTour());


        primaryStage.setScene(scene);

        primaryStage.show();


//

    }

    public Line getLineFromPoints(Point2D source, Point2D target) {
        return new Line(source.getX(), source.getY(), target.getX(), target.getY());
    }

    private void makeCircleDraggable(Point2D point, Circle circle) {
        circle.setOnMouseReleased(event -> {
            System.out.println("point::mouse::release");
            //System.out.println(event.getY())


            circle.setFill(Color.BLACK);

            event.consume();

            instance.createGraphAndEdgesFromPoints();
            generateLinesForEachCheckboxAndEdge();
        });

        circle.setOnMouseClicked(event -> {
            if (event.isAltDown()) {
                points.getChildren().remove(circle);
                instance.points.remove(point);

                event.consume();

                instance.createGraphAndEdgesFromPoints();
                generateLinesForEachCheckboxAndEdge();
            }
        });


        circle.setOnMouseDragged(event -> {
            circle.setFill(Color.BROWN);

            point.setLocation(event.getX(), event.getY());

            circle.setCenterX(event.getX());
            circle.setCenterY(event.getY());

            event.consume();

            instance.createGraphAndEdgesFromPoints();
            generateLinesForEachCheckboxAndEdge();

        });

    }

    private void generateLinesForEachCheckboxAndEdge() {
        //edges = new Group();
        //edges.getChildren().forEach(node -> node.visibleProperty().unbind());
        //edges.getChildren().forEach(node -> edges.getChildren().remove(node));
        //visualisationPane.getChildren().remove(edges);
        edges.setVisible(false);
        edges = new Group();
        visualisationPane.getChildren().add(edges);


        for (ModifiedWeightedEdge edge : instance.graph.edgeSet()) {
            Point2D source = instance.graph.getEdgeSource(edge);
            Point2D target = instance.graph.getEdgeTarget(edge);
            Line line = getLineFromPoints(source, target);

            Line triangulationLine = getLineFromPoints(source, target);

            Line line0 = getLineFromPoints(source, target);
            Line line1 = getLineFromPoints(source, target);
            Line line2 = getLineFromPoints(source, target);

            Text text1 = new Text((source.getX() + target.getX()) / 2.0, (source.getY() + target.getY()) / 2.0, edge.distanceToNextCircleCenterString);
            text1.setFont(new Font(scale * 10));
            text1.setFill(Color.RED);
            //text1.setStroke(Color.RED);
            //text1.setScaleX(diff / factor);
            text1.setScaleY(-1);
            text1.visibleProperty().bind(line1.visibleProperty());
            edges.getChildren().add(text1);

            Text text2 = new Text((source.getX() + target.getX()) / 2.0, (source.getY() + target.getY()) / 2.0, edge.distanceToNextCircleCenterString);
            text2.setFont(new Font(scale * 10));
            text2.setFill(Color.RED);
            //text1.setScaleX(diff / factor);
            text2.setScaleY(-1);
            text2.visibleProperty().bind(line2.visibleProperty());
            edges.getChildren().add(text2);

            //line.addEventFilter(MOUSE_ENTERED, a);


            Line line3 = getLineFromPoints(source, target);
            Line line4 = getLineFromPoints(source, target);

            Line line10 = getLineFromPoints(source, target);
            Line line20 = getLineFromPoints(source, target);

            Line line30 = getLineFromPoints(source, target);
            Line line40 = getLineFromPoints(source, target);

            triangulationLine.setStrokeWidth(scale);

            line.setStrokeWidth(scale);
            line0.setStrokeWidth(scale);
            line1.setStrokeWidth(scale);
            line2.setStrokeWidth(scale);
            line3.setStrokeWidth(scale);
            line4.setStrokeWidth(scale);

            line10.setStrokeWidth(scale);
            line20.setStrokeWidth(scale);
            line30.setStrokeWidth(scale);
            line40.setStrokeWidth(scale);


            //new SimpleBooleanProperty(edge.in_tour);
            //ReadOn
            triangulationLine.visibleProperty().bind(edge.getReadOnlyInTriangulationProperty().and(triangulation.selectedProperty()));
            triangulationLine.setStroke(Color.GREEN);
            line.visibleProperty().bind(edge.getReadOnlyInTourProperty().and(computedTour.selectedProperty()));

            line0.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(0).and(order0.selectedProperty()));
            line0.setStroke(Color.RED);

            line1.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(1).and(order1.selectedProperty()));
            line1.setStroke(Color.RED);

            line2.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(2).and(order2.selectedProperty()));
            line2.setStroke(Color.BLUE);

            line3.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(3).and(order3.selectedProperty()));
            line3.setStroke(Color.BLUE);

            line4.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(4).and(order4.selectedProperty()));
            line4.setStroke(Color.BLUE);

            line10.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(10).and(order10.selectedProperty()));
            line10.setStroke(Color.RED);

            line20.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(20).and(order20.selectedProperty()));
            line20.setStroke(Color.BLUE);

            line30.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(30).and(order30.selectedProperty()));
            line30.setStroke(Color.BLUE);

            line40.visibleProperty().bind(edge.getReadOnlyUseFullOrderProperty().isEqualTo(40).and(order40.selectedProperty()));
            line40.setStroke(Color.BLUE);


            edges.getChildren().add(line);
            edges.getChildren().add(line0);
            edges.getChildren().add(line1);
            edges.getChildren().add(line2);
            edges.getChildren().add(line3);
            edges.getChildren().add(line4);
            edges.getChildren().add(line10);
            edges.getChildren().add(line20);
            edges.getChildren().add(line30);
            edges.getChildren().add(line40);


            edges.getChildren().add(triangulationLine);

        }
    }

    private CheckBox makeCheckboxWithLines(double strokeWidth, Color color, Function<ModifiedWeightedEdge, ReadOnlyBooleanProperty> function, String label, Pane subviewRoot) {
        CheckBox result = new CheckBox(label);

        for (ModifiedWeightedEdge edge : instance.graph.edgeSet()
        ) {
            Point2D source = instance.graph.getEdgeSource(edge);
            Point2D target = instance.graph.getEdgeTarget(edge);
            Line line = getLineFromPoints(source, target);
            line.setStrokeWidth(strokeWidth);
            line.setStroke(color);
            line.visibleProperty().bind(function.apply(edge).and(result.selectedProperty()));

            subviewRoot.getChildren().add(line);

        }

        return result;

    }

}

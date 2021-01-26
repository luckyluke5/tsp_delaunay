package tsp.delaunay;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import org.jgrapht.graph.DefaultWeightedEdge;

class ModifiedWeightedEdge extends DefaultWeightedEdge {
    private final ReadOnlyBooleanWrapper inTour;
    private final ReadOnlyBooleanWrapper inTriangulation;
    private final ReadOnlyIntegerWrapper usefulDelaunayOrder;
    private final ReadOnlyBooleanWrapper partOfConvexHull;
    private final ReadOnlyBooleanWrapper inOptTour;
    ReadOnlyBooleanWrapper inAugmentingCircle;
    ReadOnlyBooleanWrapper inModifiedTriangulation;
    String distanceToNextCircleCenterString;
    Double leftDistance;
    Double maxDistance;
    Double rightDistance;


    public ModifiedWeightedEdge() {
        super();
        inTour = new ReadOnlyBooleanWrapper(false);
        usefulDelaunayOrder = new ReadOnlyIntegerWrapper();
        inTriangulation = new ReadOnlyBooleanWrapper(false);
        inAugmentingCircle = new ReadOnlyBooleanWrapper(false);
        inModifiedTriangulation = new ReadOnlyBooleanWrapper(false);
        partOfConvexHull = new ReadOnlyBooleanWrapper(false);
        inOptTour = new ReadOnlyBooleanWrapper(false);

        distanceToNextCircleCenterString = new String();


        //this.getSource()
    }

    public static void setAllInTour(ModifiedWeightedEdge[] edges, boolean inTour) {
        for (ModifiedWeightedEdge edge : edges
        ) {
            edge.setInTour(inTour);
        }
    }

    public boolean isInOptTour() {
        return inOptTour.get();
    }

    public void setInOptTour(boolean inOptTour) {
        this.inOptTour.set(inOptTour);
    }

    public ReadOnlyBooleanProperty inOptTourProperty() {
        return inOptTour.getReadOnlyProperty();
    }

    public boolean isPartOfConvexHull() {
        return partOfConvexHull.get();
    }

    public void setPartOfConvexHull(boolean partOfConvexHull) {
        this.partOfConvexHull.set(partOfConvexHull);
    }

    public ReadOnlyBooleanProperty getReadOnlyPartOfConvexHullProperty() {
        return partOfConvexHull.getReadOnlyProperty();
    }

    public boolean isInTour() {
        return inTour.get();
    }

    public void setInTour(boolean inTour) {

        this.inTour.set(inTour);

    }

    public ReadOnlyBooleanProperty getReadOnlyInTourProperty() {
        return inTour.getReadOnlyProperty();
    }

    //boolean in_triangulation_b;

    public int getUsefulDelaunayOrder() {
        return usefulDelaunayOrder.get();
    }

    public void setUsefulDelaunayOrder(int usefulDelaunayOrder) {
        this.usefulDelaunayOrder.set(usefulDelaunayOrder);
    }

    public boolean isInTriangulation() {
        return inTriangulation.get();
    }

    public void setInTriangulation(boolean inTriangulation) {

        this.inTriangulation.set(inTriangulation);


        inModifiedTriangulation.set(inTriangulation);

    }

    public ReadOnlyBooleanProperty getReadOnlyInTriangulationProperty() {
        return inTriangulation.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty getReadOnlyUseFullOrderProperty() {
        return usefulDelaunayOrder.getReadOnlyProperty();
    }

}

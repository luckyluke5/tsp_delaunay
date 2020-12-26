package tsp_delaunay;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import org.jgrapht.graph.DefaultWeightedEdge;

class MyEdge extends DefaultWeightedEdge {
    private final ReadOnlyBooleanWrapper in_tour_v;
    private final ReadOnlyIntegerWrapper use_full_order;
    private boolean in_tour;


    public MyEdge() {
        super();
        this.in_tour_v = new ReadOnlyBooleanWrapper(false);
        this.in_tour = false;
        use_full_order = new ReadOnlyIntegerWrapper(5);
    }

    public int get_use_full_order() {
        return use_full_order.get();
    }

    public void set_use_full_order(int use_full_order) {
        this.use_full_order.set(use_full_order);
    }

    public ReadOnlyIntegerProperty get_read_only_use_full_order_property() {
        return use_full_order.getReadOnlyProperty();
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

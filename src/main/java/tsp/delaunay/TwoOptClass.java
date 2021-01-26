package tsp.delaunay;

import javafx.concurrent.Task;

class TwoOptClass extends Task<Void> {
    final Instance instance;


    public TwoOptClass(Instance instance) {
        this.instance = instance;

    }

    @Override
    protected Void call() {


        instance.printWeightOfTourAndMSTRatio();


        return null;
    }

}

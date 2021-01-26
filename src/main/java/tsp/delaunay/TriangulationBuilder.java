package tsp.delaunay;

import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TriangulationBuilder {
    Instance instance;

    public TriangulationBuilder(Instance instance) {
        this.instance = instance;
    }

    public boolean isLineIntersectAnyOtherLine(ModifiedWeightedEdge edge, Collection<ModifiedWeightedEdge> edges) {
        Line2D line = getLineFromEdge(edge);

        List<Line2D> lines = edges.stream().map(this::getLineFromEdge).collect(Collectors.toList());

        return isLineIntersectAnyOtherLine(line, lines);
    }

    private Line2D getLineFromEdge(ModifiedWeightedEdge edge) {
        return new Line2D.Double(instance.graph.getEdgeSource(edge), instance.graph.getEdgeTarget(edge));
    }

    static boolean isLineIntersectAnyOtherLine(Line2D line, Collection<Line2D> lines) {
        for (Line2D possibleIntersectingLine : lines) {
            //Line2D line_from_triangulation_edge = line_from_edge(triangulation_edge);

            if (areLinesIntersectingWithoutEndpoints(line, possibleIntersectingLine)) {
                return true;
            }
        }
        return false;
    }

    static boolean areLinesIntersectingWithoutEndpoints(Line2D line1, Line2D line2) {


        boolean result;
        boolean sameAtP1 = false;
        boolean sameAtP2 = false;

        if (line1.getP1().equals(line2.getP1()) || line1.getP1().equals(line2.getP2())) {
            sameAtP1 = true;

        }

        if (line1.getP2().equals(line2.getP1()) || line1.getP2().equals(line2.getP2())) {
            sameAtP2 = true;

        }

        if (sameAtP1 && sameAtP2) {
            //result = true;
            throw new ArithmeticException("Undefined behavior, both lines are the same");

        } else {
            if (sameAtP1 || sameAtP2) {
                result = false;
            } else {
                result = line1.intersectsLine(line2);
            }
        }


        return result;
    }

    boolean areLinesIntersectingWithoutEndpoints(ModifiedWeightedEdge edge1, ModifiedWeightedEdge edge2) {

        Line2D line1 = getLineFromEdge(edge1);
        Line2D line2 = getLineFromEdge(edge2);

        return areLinesIntersectingWithoutEndpoints(line1, line2);
    }

    void initialTriangulationWithSetEdges() {


        for (ModifiedWeightedEdge edge : getInstance().tourSubgraphMask.edgeSet()) {

            edge.setInTriangulation(true);
        }

        completeTriangulationWithValidEdges();

    }

    public Instance getInstance() {
        return instance;
    }

    private void completeTriangulationWithValidEdges() {
        //ArrayList<MyEdge> result = new ArrayList<>();
        for (ModifiedWeightedEdge edge : getInstance().graph.edgeSet()) {
            Line2D line = getInstance().lineFromEdge(edge);
            List<Line2D> lineArray = getInstance().triangulationSubgraphMask.edgeSet().stream().map(getInstance()::lineFromEdge).collect(Collectors.toList());

            if (!isLineIntersectAnyOtherLine(line, lineArray)) {


                edge.setInTriangulation(true);

            }

        }
        //return result;
    }
}

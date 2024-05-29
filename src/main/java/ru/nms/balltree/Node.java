package ru.nms.balltree;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.RealVector;
import ru.nms.balltree.utils.BallTreeUtils;

import java.util.List;

@Getter
public class Node {
    private final List<RealVector> vectors;
    private final RealVector centroid;
    private final double radius;
    @Setter
    private Node leftChild;
    @Setter
    private Node rightChild;

    public Node(List<RealVector> vectors) {
        this.vectors = vectors;
        this.centroid = BallTreeUtils.calculateCentroid(vectors);
        this.radius = BallTreeUtils.calculateRadius(vectors, centroid);
    }
}

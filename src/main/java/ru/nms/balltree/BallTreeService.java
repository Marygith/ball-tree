package ru.nms.balltree;

import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class BallTreeService {

    private BallTree ballTree;

    public void constructBallTree(List<RealVector> vectors, int leafSize) {
        var rootNode = new Node(vectors);
        this.ballTree = new BallTree(rootNode, leafSize);
        ballTree.buildTree(rootNode);

    }

    public List<RealVector> knn(RealVector targeet, int k) {
        return ballTree.knn(targeet, k);
    }
}

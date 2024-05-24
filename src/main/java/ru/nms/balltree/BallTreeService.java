package ru.nms.balltree;

import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class BallTreeService {

    private BallTree ballTree;
    public void constructBallTree(List<RealVector> vectors) {
        var rootNode = new Node(vectors);
        this.ballTree = new BallTree(rootNode, 10);
        ballTree.buildTree(rootNode);

    }

    public List<RealVector> knn(RealVector targeet, int k) {
        return ballTree.knn(targeet, k);
    }
}

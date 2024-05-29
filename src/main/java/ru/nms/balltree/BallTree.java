package ru.nms.balltree;

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Multimaps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.linear.RealVector;
import ru.nms.balltree.utils.BallTreeUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class BallTree {

    Comparator<RealVector> projectionComparator = Comparator.comparingDouble(v -> Arrays.stream(v.toArray()).sum());
    @Getter
    private final Node root;
    private final int leafSize;

    public void buildTree(Node currentNode) {
        if (currentNode.getVectors().size() <= leafSize) {
            return;
        }

        var farthestPoint = BallTreeUtils.findFarthestVector(currentNode.getVectors(), currentNode.getCentroid());
        var secondFarthestPoint = BallTreeUtils.findSecondFarthestVector(currentNode.getVectors(), currentNode.getCentroid(), farthestPoint);
        if (Arrays.equals(farthestPoint.toArray(), secondFarthestPoint.toArray())) return;
        var baseLine = farthestPoint.subtract(secondFarthestPoint);
        var projectionsMap = Multimaps.index(currentNode.getVectors(), k -> k.projection(baseLine));

        var sortedProjections = projectionsMap.keySet().stream().sorted(projectionComparator).toList();
        var middleIndex = sortedProjections.size() / 2;

        List<RealVector> leftSubset = sortedProjections.subList(0, middleIndex).stream().map(projectionsMap::get).flatMap(List::stream).toList();
        List<RealVector> rightSubset = sortedProjections.subList(middleIndex, sortedProjections.size()).stream().map(projectionsMap::get).flatMap(List::stream).toList();

        currentNode.setLeftChild(new Node(leftSubset));
        currentNode.setRightChild(new Node(rightSubset));

        buildTree(currentNode.getLeftChild());
        buildTree(currentNode.getRightChild());
    }

    public List<RealVector> knn(RealVector target, int k) {
        Comparator<RealVector> comparator = Comparator.comparingDouble(v -> v.getDistance(target));

        var knnQueue = MinMaxPriorityQueue
                .orderedBy(comparator)
                .maximumSize(k)
                .create(root.getVectors().subList(0, k));
        search(knnQueue, root, target);
        return knnQueue.stream().toList();
    }

    private void search(MinMaxPriorityQueue<RealVector> queue, Node node, RealVector target) {
        if (node.getCentroid().getDistance(target) - node.getRadius() >= target.getDistance(queue.peekLast())) {
            return;
        } else if (node.getLeftChild() == null) {
            node.getVectors().forEach(vec -> {
                if (vec.getDistance(target) < target.getDistance(queue.peekLast())) {
                    queue.add(vec);
                }
            });
        } else {
            search(queue, node.getLeftChild(), target);
            search(queue, node.getRightChild(), target);
        }
    }
}


package ru.nms.balltree;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import ru.nms.balltree.utils.BallTreeUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@RequiredArgsConstructor
public class BallTree {
    //    private final MinMaxPriorityQueue<RealVector> knnQueue;
    Comparator<RealVector> projectionComparator = Comparator.comparingDouble(v -> Arrays.stream(v.toArray()).sum());
    @Getter
    private final Node root;
    private final int leafSize;

    // Method to build the Ball Tree recursively
    public void buildTree(Node currentNode) {
        if (currentNode.getVectors().size() <= leafSize) {
            return;
        }

//        var twoFarthestPoints = BallTreeUtils.findTwoFarthestPoints(currentNode.getVectors(), currentNode.getCentroid());
        var farthestPoint = BallTreeUtils.findFarthestVector(currentNode.getVectors(), currentNode.getCentroid());
        var secondFarthestPoint = BallTreeUtils.findSecondFarthestVector(currentNode.getVectors(), currentNode.getCentroid(), farthestPoint);
        if (Arrays.equals(farthestPoint.toArray(), secondFarthestPoint.toArray())) return;
        var baseLine = farthestPoint.subtract(secondFarthestPoint);
        var projectionsMap = Multimaps.index(currentNode.getVectors(), k -> k.projection(baseLine));
//        projectionsMap.putAll(currentNode.getVectors().stream().collect(Multimaps.flatteningToMultimap(
//                identity(),  // Key function
//                identity(),  // Value function
//                HashMultimap::create  // Multimap supplier
//        )));

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
//        Map<Double, RealVector> distanceToVector = root.getVectors().subList(0, k)
//                .stream()
//                .collect(Collectors.toMap(vec -> vec.getDistance(target), identity()));
        var knnQueue = MinMaxPriorityQueue
                .orderedBy(comparator)
                .maximumSize(k)
                .create(root.getVectors().subList(0, k));
        search(knnQueue, root, target);
        return knnQueue.stream().toList();
    }

    private void search(MinMaxPriorityQueue<RealVector> queue, Node node, RealVector target) {
        if(node.getCentroid().getDistance(target) - node.getRadius() >= target.getDistance(queue.peekFirst())) {
            return;
        } else if (node.getLeftChild() == null) {
            node.getVectors().forEach(vec -> {
                if(vec.getDistance(target) < target.getDistance(queue.peekFirst())) {
                    queue.add(vec);
                }
            });
        } else {
            search(queue, node.getLeftChild(), target);
            search(queue, node.getRightChild(), target);
        }

    }

}


package ru.nms.balltree;

import com.google.common.collect.MinMaxPriorityQueue;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BruteForceService {

    public List<RealVector> knn(List<RealVector> vectors, int k, RealVector target) {
        Comparator<RealVector> comparator = Comparator.comparingDouble(v -> v.getDistance(target));
        var queue = MinMaxPriorityQueue.orderedBy(comparator)
                .maximumSize(k)
                .create(vectors);
        return queue.stream().toList();

    }

    public List<RealVector> knn2(List<RealVector> vecs, int k, RealVector target) {
        Comparator<RealVector> comparator = Comparator.comparingDouble(v -> v.getDistance(target));
        var vectors = new ArrayList<>(vecs);
        List<RealVector> result = new ArrayList<>();
        RealVector max = vectors.getFirst();
        for (int i = 0; i < k; i++) {
            result.add(vectors.parallelStream().min(comparator).get());
            vectors.remove(result.getLast());
        }

        return result;
    }
}

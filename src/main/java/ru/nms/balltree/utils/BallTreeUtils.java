package ru.nms.balltree.utils;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class BallTreeUtils {
    public static RealVector calculateCentroid(List<RealVector> vectors) {
        int dimension = vectors.getFirst().getDimension(); // Assuming all vectors have the same dimension
        RealVector sum = new ArrayRealVector(dimension);

        for (RealVector vector : vectors) {
            sum = sum.add(vector);
        }

        return sum.mapDivide(vectors.size());
    }

    public static double calculateRadius(List<RealVector> vectors, RealVector centroid) {
        double maxDistance = 0.0;

        for (RealVector vector : vectors) {
            double distance = 0.0;
            distance = centroid.getDistance(vector);
            maxDistance = Math.max(maxDistance, distance);
        }

        return maxDistance;
    }

    public static List<RealVector> findTwoFarthestPoints(List<RealVector> vectors, RealVector centroid) {
        if(vectors.size() < 3) {
            return vectors;
        }
        double maxDistance = 0.0;
        double secondMaxDistance = 0.0;
        var farthestVector = vectors.getFirst();
        var secondFarthestVector = vectors.get(1);
        double distance = 0.0;
        for (RealVector vector : vectors) {
            distance = centroid.getDistance(vector);
            if(distance > maxDistance) {
                secondMaxDistance = maxDistance;
                secondFarthestVector = farthestVector;
                maxDistance = distance;
                farthestVector = vector;
            } else if (distance > secondMaxDistance) {
                secondMaxDistance = distance;
                secondFarthestVector = vector;
            }
        }
        return List.of(farthestVector, secondFarthestVector);
    }
}

package ru.nms.balltree.utils;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;
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


    public static RealVector findFarthestVector(List<RealVector> vectors, RealVector centroid) {

        double maxDistance = 0.0;
        var farthestVector = vectors.getFirst();
        double distance = 0.0;
        for (RealVector vector : vectors) {
            distance = centroid.getDistance(vector);
            if (distance > maxDistance) {
                maxDistance = distance;
                farthestVector = vector;
            }
        }
        return farthestVector;
    }

    public static RealVector findSecondFarthestVector(List<RealVector> vectors, RealVector centroid, RealVector farthestVector) {

        double maxDistance = 0.0;
        var secondFarthestVector = vectors.getFirst();
        double distance = 0.0;
        for (RealVector vector : vectors) {
            if (Arrays.equals(vector.toArray(), farthestVector.toArray())) continue;
            distance = centroid.getDistance(vector) + vector.getDistance(farthestVector);
            if (distance > maxDistance) {
                maxDistance = distance;
                secondFarthestVector = vector;
            }
        }
        return secondFarthestVector;
    }
}

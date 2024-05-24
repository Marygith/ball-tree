package ru.nms.balltree;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String fileName = "C:\\Users\\maria\\Downloads\\embeddings_array.txt";
        List<List<Double>> twoDList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s+");
                List<Double> row = new ArrayList<>();
                for (String value : values) {
                    row.add(Double.parseDouble(value));
                }
                twoDList.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<RealVector> vectors = twoDList.stream().map(doubleList -> new ArrayRealVector(doubleList.stream().mapToDouble(Double::doubleValue).toArray())).collect(Collectors.toList());
        BallTreeService ballTreeService = new BallTreeService();
        ballTreeService.constructBallTree(vectors);

        Set<Integer> targetIndices = new HashSet<>();
        List<RealVector> targets = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int ind = ThreadLocalRandom.current().nextInt(0, vectors.size());
            while (targetIndices.contains(ind)) {
                ind = ThreadLocalRandom.current().nextInt();
            }
            targetIndices.add(ind);
            targets.add(vectors.get(ind));
        }
        var bruteForceService = new BruteForceService();
        var res1 = bruteForceService.knn(vectors, 5, targets.getFirst());
        var res2 = bruteForceService.knn2(vectors, 5, targets.getFirst());

        System.out.println(res1.equals(res2));
        System.out.println(targets.getFirst().getDistance(vectors.getFirst()));
        res1.forEach(vec -> {
            System.out.println("Distance: " + vec.getDistance(targets.getFirst()));
            System.out.println("vec: " + vec);
        });

        System.out.println("!!!");

        res2.forEach(vec -> {
            System.out.println("Distance: " + vec.getDistance(targets.getFirst()));
            System.out.println("vec: " + vec);
        });
        System.out.println("!!!");


        var res = ballTreeService.knn(targets.getFirst(), 5);
        res.forEach(vec -> {
            System.out.println("Distance: " + vec.getDistance(targets.getFirst()));
            System.out.println("vec: " + vec);
        });
    }

}

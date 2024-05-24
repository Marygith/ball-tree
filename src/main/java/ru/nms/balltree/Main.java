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
        String fileName = "C:\\Users\\maria\\Downloads\\embeddings_array.txt";  // Replace with the actual file path

        List<List<Double>> twoDList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s+"); // Split the line by whitespace
                List<Double> row = new ArrayList<>();
                for (String value : values) {
                    row.add(Double.parseDouble(value));  // Convert the value to integer and add to the row
                }
                twoDList.add(row);  // Add the row to the 2D List
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the 2D List
//        for (List<Double> row : twoDList.subList(0, 10)) {
//
//            System.out.println("row with length " + row.size() + ": " + row);
//        }
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
        var res1 = bruteForceService.knn(vectors, 10, targets.getFirst());
        var res2 = bruteForceService.knn2(vectors, 10, targets.getFirst());

        System.out.println("res1: " + res1);
        System.out.println("res2: " + res2);




    }

}

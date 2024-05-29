package ru.nms.balltree.benchmark;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.openjdk.jmh.annotations.*;
import ru.nms.balltree.BallTreeService;
import ru.nms.balltree.BruteForceService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@State(Scope.Benchmark)
@Getter
public class ExecutionPlan {

    private final List<RealVector> vectors;
    @Getter
    private final RealVector target;

    @Param({"3", "5", "10", "50", "100", "1000"})
    private int leafSize;

    @Param({"100", "1000"})
    private int k;

    @Getter
    private BallTreeService ballTreeService;

    @Getter
    @Setter
    private List<RealVector> kActualNeighbours;

    @Setter
    private List<RealVector> foundNeighbours;

    private final BruteForceService bruteForceService = new BruteForceService();

    public ExecutionPlan() {
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
        this.vectors = twoDList.stream().map(doubleList -> new ArrayRealVector(doubleList.stream().mapToDouble(Double::doubleValue).toArray())).collect(Collectors.toList());
        target = vectors.get(ThreadLocalRandom.current().nextInt(0, vectors.size()));
    }


    @Setup(Level.Trial)
    public void setup() {
        ballTreeService = new BallTreeService();
        ballTreeService.constructBallTree(vectors, leafSize);

        kActualNeighbours = bruteForceService.knn2(vectors, k, target);
    }

    private Boolean neighbourIsPresent(double[] array) {
        return kActualNeighbours.stream().map(RealVector::toArray).anyMatch(arr -> Arrays.equals(array, arr));
    }

    @TearDown(Level.Trial)
    public void estimateResult() {
        log.info("Search {} neighbours in tree with leaf size {}", k, leafSize);
        if (foundNeighbours != null) {
            double n = foundNeighbours.stream()
                    .map(RealVector::toArray)
                    .filter(this::neighbourIsPresent)
                    .count();
            log.info("Found {} out of {} neighbours, which is {} precision", n, k, n / k);
        }
    }

}

package ru.nms.balltree.benchmark;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 1, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
public class BallTreeBenchmark {

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testBallTreeKnn(ExecutionPlan plan) throws IOException {
        plan.setFoundNeighbours(plan.getBallTreeService().knn(plan.getTarget(), plan.getK()));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testBruteForceKnn(ExecutionPlan plan) throws IOException {
        plan.setKActualNeighbours(plan.getBruteForceService().knn(plan.getVectors(), plan.getK(), plan.getTarget()));
    }
}

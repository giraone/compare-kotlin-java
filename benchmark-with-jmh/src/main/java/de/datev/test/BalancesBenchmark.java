package de.datev.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// 2 iterations to warm-up, that may last 2 seconds each
@Warmup(iterations=2, time=2)
// 5 iterations to measure, that may last 10 seconds each
@Measurement(iterations=5, time=10)
// Only one forked process
@Fork(1)
// Only one thread
@Threads(1)
// calculate the throughput
@BenchmarkMode(Mode.Throughput)
// use the same instance of this class for the whole benchmark, 
// so it is OK to have some fix member variables
@State(Scope.Benchmark)
@SuppressWarnings("unused")
public class BalancesBenchmark {

    private static final Map<String, List<de.datev.test.DailyBalance>> testBedJava = new HashMap<>();
    private static final Map<String, List<de.datev.ktest.DailyBalance>> testBedKotlin = new HashMap<>();

    private static final de.datev.test.BalancesService balancesService = new de.datev.test.BalancesService();
    private static final de.datev.ktest.BalancesService balancesServiceKotlin = new de.datev.ktest.BalancesService();

    private static final Set<String> ibansOfCustomer = Set.of("DE123", "DE987");

    static {
        try {
            testBedJava.put("large", readTestData("/test-large.json"));
        }
        catch (IOException ioe) {
            System.err.println("CANNOT READ TEST DATA! " + ioe.getMessage());
        }
        testBedKotlin.put("large", testBedJava.get("large").stream()
            .map(jdb -> new de.datev.ktest.DailyBalance(jdb.iban, jdb.day, jdb.total))
            .collect(Collectors.toList())
        );
    }

    @Benchmark
    public void java_imperative_originalCode() {

        balancesService.sumLatestTotalsByIbanImperativeOriginalCode(testBedJava.get("large"));
    }

    @Benchmark
    public void java_imperative_originalCode_withKnownIbans() {

        balancesService.sumLatestTotalsByIbanImperativeWithKnownIbans(testBedJava.get("large"), ibansOfCustomer);
    }

    @Benchmark
    public void java_functional_originalCode() {

        balancesService.sumLatestTotalsByIbanFunctionalOriginalCode(testBedJava.get("large"));
    }

    @Benchmark
    public void java_functional_improvedCode() {

        balancesService.sumLatestTotalsByIbanFunctionalImprovedCode(testBedJava.get("large"));
    }

    @Benchmark
    public void kotlin_sumLatestTotalsByIbanReactiveImprovedCode() {

        balancesServiceKotlin.sumLatestTotalsByIban(testBedKotlin.get("large"));
    }

    //------------------------------------------------------------------------------------------------------------------

    private static List<DailyBalance> readTestData(String resourcePath) throws IOException {
        byte[] input = readBytesFromResource(resourcePath);
        if (input == null) {
            throw new IllegalArgumentException("Cannot read resource \"" + resourcePath + "\". Path not found!");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TypeReference<List<DailyBalance>> listTypeRef = new TypeReference<>() { };
        return objectMapper.readValue(input, listTypeRef);
    }

    private static byte[] readBytesFromResource(String resourcePath) throws IOException {

        try (InputStream in = BalancesBenchmark.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return null;
            }
            return in.readAllBytes();
        }
    }
}

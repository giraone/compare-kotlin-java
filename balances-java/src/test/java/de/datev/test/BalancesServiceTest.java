package de.datev.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class BalancesServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BalancesServiceTest.class);

    static Map<String, List<DailyBalance> > testBed = new HashMap<>();
    static BalancesService balancesService = new BalancesService();

    @BeforeAll
    static void init() throws IOException {
        testBed.put("small", readTestData("/test-small.json"));
        testBed.put("large", readTestData("/test-large.json"));
        // warm up
        Set<String> ibansOfCustomer = Set.of("DE123", "DE987");
        for (int i = 0; i < 10; i++) {
            balancesService.sumLatestTotalsByIbanImperativeWithKnownIbans(testBed.get("large"), ibansOfCustomer);
            balancesService.sumLatestTotalsByIbanImperativeOriginalCode(testBed.get("large"));
            balancesService.sumLatestTotalsByIbanFunctionalImprovedCode(testBed.get("large"));
            balancesService.sumLatestTotalsByIbanFunctionalOriginalCode(testBed.get("large"));
        }
    }

    //-- Tests --

    @ParameterizedTest
    @CsvSource({
        "small,1835.00",
        "large,1835.00"
    })
    void imperativeConstantTime(String mode, BigDecimal expectedSum) {

        Set<String> ibansOfCustomer = Set.of("DE123", "DE987");
        BigDecimal result = measure("imperativeConstantTime-" + mode,
            () -> balancesService.sumLatestTotalsByIbanImperativeWithKnownIbans(testBed.get(mode), ibansOfCustomer));
        assertThat(result).isEqualTo(expectedSum);
    }

    @ParameterizedTest
    @CsvSource({
        "small,1835.00",
        "large,1835.00"
    })
    void imperativeOriginalCode(String mode, BigDecimal expectedSum) {

        BigDecimal result = measure("imperativeOriginalCode-" + mode,
            () -> balancesService.sumLatestTotalsByIbanImperativeOriginalCode(testBed.get(mode)));
        assertThat(result).isEqualTo(expectedSum);
    }

    @ParameterizedTest
    @CsvSource({
        "small,1835.00",
        "large,1835.00"
    })
    void functionalImprovedCode(String mode, BigDecimal expectedSum) {

        BigDecimal result = measure("reactiveImprovedCode-" + mode,
            () -> balancesService.sumLatestTotalsByIbanFunctionalImprovedCode(testBed.get(mode)));
        assertThat(result).isEqualTo(expectedSum);
    }

    @ParameterizedTest
    @CsvSource({
        "small,1835.00",
        "large,1835.00"
    })
    void functionalOriginalCode(String mode, BigDecimal expectedSum) {

        BigDecimal result = measure("reactiveOriginalCode-" + mode,
            () -> balancesService.sumLatestTotalsByIbanFunctionalOriginalCode(testBed.get(mode)));
        assertThat(result).isEqualTo(expectedSum);
    }

    //-- Utils --

    private static byte[] readBytesFromResource(String resourcePath) throws IOException {

        try (InputStream in = BalancesServiceTest.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return null;
            }
            return in.readAllBytes();
        }
    }

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

    private BigDecimal measure(String name, Supplier<BigDecimal> fun) {

        long start = System.nanoTime();
        BigDecimal ret = fun.get();
        LOGGER.info("-- {} took {} ns", name, System.nanoTime() - start);
        return ret;
    }

    void buildTestData()  {

        LocalDate start = LocalDate.of(2019, 4, 30);
        for (int i = 0; i < 365; i++) {
            String d = DateTimeFormatter.ISO_DATE.format(start);
            System.out.println("{ \"iBAN\": \"DE123\", \"day\": \"" + d + "\", \"total\": \"1234.56\" },");
            System.out.println("{ \"iBAN\": \"DE987\", \"day\": \"" + d + "\", \"total\": \"2345.67\" },");
            start = start.plusDays(1);
        }
    }
}

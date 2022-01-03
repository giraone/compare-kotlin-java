# Benchmarking "Balances-Code" (Java and Kotlin) with JMH

## Build and run

```
mvn clean package
java -jar target/benchmarks.jar
```

The JMH benchmark code and its annotations is in [BalancesBenchmark.java](src/main/java/de/datev/test/BalancesBenchmark.java).
With the default JMH iteration settings, the test may take 4 to 5 minutes, so feel free to decrease the iterations.

## Results

### Intel i7-4910MQ 2.90GHz, Windows 10, OpenJDK 11

```
Benchmark                                                            Mode  Cnt         Score        Error  Units
BalancesBenchmark.java_imperative_originalCode_withKnownIbans       thrpt    5  14593703,151 ± 225239,497  ops/s
BalancesBenchmark.java_functional_improvedCode                      thrpt    5     27540,831 ±    818,728  ops/s
BalancesBenchmark.java_imperative_originalCode                      thrpt    5     25555,489 ±    929,556  ops/s
BalancesBenchmark.kotlin_functional                                 thrpt    5     24601,712 ±    665,140  ops/s
BalancesBenchmark.java_functional_originalCode                      thrpt    5     20463,340 ±    555,912  ops/s
```

## JMH settings

These are the JMH settings, defined using annotations:

```java
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
```
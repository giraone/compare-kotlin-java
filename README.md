# Benchmarking Java (imperative/functional) / Kotlin (functional) with JMH

This project shows

- a benchmark of different coding solutions copied from https://github.com/SchlammSpringer/modern-springboot-with-kotlin

Why?

- To show that "*assessing an algorithm only by lines of code*" may lead to "*bad performance*" - less lines of code is not always faster!

## Build and run

```
cd balances-java
mvn clean install
cd ..
cd balances-kotlin
mvn clean install
cd ..
cd benchmark-woth-jmh
mvn clean package
java -jar target/benchmarks.jar
```

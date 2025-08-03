# ValidCheck vs Bean Validation Performance Benchmark Report

## Overview

This report presents performance comparison results between ValidCheck (configured with batch
validation) and Jakarta Bean Validation (Hibernate Validator 8.0.1) using JMH (Java Microbenchmark
Harness).

## Test Environment

- **JVM**: OpenJDK 64-Bit Server VM, JDK 21.0.2
- **JMH Version**: 1.37
- **Benchmark Mode**: Average time per operation (nanoseconds)
- **Configuration**: 3 warmup iterations × 2s, 5 measurement iterations × 2s
- **ValidCheck**: Configured with batch validation enabled

## Results Summary

|        Scenario        | Bean Validation (ns/op) | ValidCheck (ns/op) | Performance Improvement |
|------------------------|-------------------------|--------------------|-------------------------|
| **Valid User**         | 20,680.6 ± 4,600.3      | 2,879.4 ± 1,103.8  | **7.2x faster**         |
| **Invalid User**       | 7,227.8 ± 289.7         | 7,445.4 ± 901.9    | 3% slower               |
| **Multiple Addresses** | 18,664.5 ± 7,413.7      | 14,185.1 ± 1,696.6 | **1.3x faster**         |

### Component-Level Performance (ValidCheck)

| Component | Average Time (ns/op) | Standard Deviation |
|-----------|----------------------|--------------------|
| Address   | 1,310.7              | ± 139.2            |
| Order     | 1,192.7              | ± 47.9             |
| OrderItem | 1,259.7              | ± 262.9            |

## Key Findings

### 1. Exceptional Performance on Valid Data

ValidCheck demonstrates **7.2x better performance** when validating correct data (20.7μs → 2.9μs),
making it highly suitable for production scenarios where most data passes validation.

### 2. Comparable Performance on Invalid Data

For invalid data scenarios, both frameworks perform similarly, with ValidCheck being slightly
slower (3%). This suggests that ValidCheck's batch validation mode effectively collects multiple
errors without significant overhead.

### 3. Consistent Batch Processing Advantage

ValidCheck shows **1.3x better performance** in multiple address validation, demonstrating
efficiency in batch processing scenarios.

### 4. Lower Performance Variance

ValidCheck exhibits more consistent performance with lower error margins across most test scenarios,
indicating predictable runtime behavior.

## Architecture Benefits

### ValidCheck Advantages

- **Zero reflection overhead**: Direct method calls vs annotation processing
- **Batch validation**: Collects multiple validation errors efficiently
- **Minimal object allocation**: Reduced memory pressure
- **Java Records integration**: Leverages modern Java language features
- **Predictable performance**: Lower variance in execution times

### Bean Validation Characteristics

- **Framework overhead**: Validator factory initialization and caching
- **Reflection-based**: Annotation processing and introspection
- **Comprehensive error collection**: Standard approach across frameworks
- **Mature ecosystem**: Wide adoption and tooling support

## Conclusion

ValidCheck with batch validation configuration delivers significant performance improvements over
traditional Bean Validation, particularly for the common case of validating correct data. The **7.2x
performance gain** for valid scenarios, combined with comparable invalid data handling, makes
ValidCheck an excellent choice for high-throughput applications where validation performance is
critical.

The batch validation feature ensures that ValidCheck can efficiently collect multiple validation
errors without sacrificing the performance benefits inherent in its record-based, reflection-free
architecture.

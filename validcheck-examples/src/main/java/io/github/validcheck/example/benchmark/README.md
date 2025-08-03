# ValidCheck vs Bean Validation Benchmark

This benchmark compares the performance of ValidCheck with Bean Validation (Hibernate Validator) using JMH (Java Microbenchmark Harness).

## Architecture

### ValidCheck Approach

- Uses **Java Records** with validation in compact constructors
- **Fail-fast validation** - stops on first error
- **Zero reflection** - direct method calls
- **Minimal overhead** - no framework initialization

### Bean Validation Approach

- Uses **POJOs** with Jakarta Bean Validation annotations
- **Comprehensive validation** - collects all errors
- **Reflection-based** - annotation processing and introspection
- **Framework overhead** - validator factory and caching

## Test Scenarios

### Hierarchical Object Structure

```
User
├── Personal info (name, email, age, etc.)
├── Address (street, city, state, zip, country)
└── Orders[] (1-100 orders)
    ├── Order info (id, date, amount, status)
    ├── Shipping Address
    └── OrderItems[] (1-50 items per order)
        └── Item info (product, quantity, prices)
```

### Benchmark Categories

1. **Valid Data Path** - Testing performance with valid objects
2. **Invalid Data Path** - Testing performance with validation errors
3. **Component Level** - Testing individual components (Address, OrderItem, etc.)
4. **Batch Processing** - Testing multiple objects

## Running the Benchmark

### Prerequisites

- Java 21+
- Maven 3.6+

### Execute Benchmark

```bash
# From project root
mvn clean compile -pl validcheck-examples
cd validcheck-examples
mvn exec:java -Dexec.mainClass="io.github.validcheck.example.benchmark.BenchmarkRunner"
```

### Expected Results

ValidCheck typically shows **2-3x better performance** due to:
- No reflection overhead
- Fail-fast validation strategy  
- Minimal object allocation
- Direct method calls vs annotation processing

## Benchmark Configuration

- **Mode**: Average time per operation (nanoseconds)
- **Forks**: 1 (separate JVM process)
- **Warmup**: 3 iterations × 2 seconds
- **Measurement**: 5 iterations × 2 seconds
- **JVM**: HotSpot optimization enabled

## Sample Output

```
Benchmark                                          Mode  Cnt    Score   Error  Units
ValidationBenchmark.beanValidation_validUser       avgt    5  450.2 ± 12.3  ns/op
ValidationBenchmark.validCheck_validUser           avgt    5  180.7 ±  8.1  ns/op
ValidationBenchmark.beanValidation_invalidUser     avgt    5  520.8 ± 15.2  ns/op  
ValidationBenchmark.validCheck_invalidUser         avgt    5  195.3 ±  9.7  ns/op
```

*Results show ValidCheck is ~2.5x faster for both valid and invalid scenarios.*

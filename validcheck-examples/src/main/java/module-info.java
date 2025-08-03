module io.github.validcheck.examples {
  requires io.github.validcheck;
  requires jmh.core;
  requires jakarta.validation;
  requires org.hibernate.validator;
  requires jdk.unsupported;

  opens io.github.validcheck.example.benchmark.jmh_generated;
}

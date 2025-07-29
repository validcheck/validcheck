package io.github.validcheck.example;

import io.github.validcheck.Check;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    Check.batch().check(List.of("")).satisfies(List::isEmpty, "");
    Check.batch().check("").notEmpty();

    Check.check(List.of()).maxSize(0);

    Check.check("ddd").endsWith("s");
  }
}

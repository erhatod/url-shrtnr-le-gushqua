package edu.kpi.testcourse.utils;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.integers;

import org.junit.jupiter.api.Test;

public class ShortenGeneratorTestPropertyBased {

  @Test
  void checkShortenLen() {
    qt()
      .forAll(integers().between(1, 40))
      .check(len -> {
          // GIVEN
          ShortenGenerator shortenGenerator = new ShortenGenerator();

          // WHEN
          String shorten = shortenGenerator.generate(len);

          // THEN
          return shorten.length() == len;
        }
      );
  }
}

package edu.kpi.testcourse.auth;

import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

import org.junit.jupiter.api.Test;

public class PasswordEncoderTestPropertyBased {

  @Test
  public void checkPasswordMatch() {
    qt()
      .forAll(strings().basicMultilingualPlaneAlphabet().ofLengthBetween(1, 25))
      .check(password -> {
        // WHEN
        String hash = Pbkdf2PasswordEncoder.encodePassword(password);

        // THEN
        return Pbkdf2PasswordEncoder.validatePassword(password, hash);
      }
    );
  }

  @Test
  public void checkPasswordNonMatch() {
    qt()
      .forAll(strings().basicMultilingualPlaneAlphabet().ofLengthBetween(1, 25))
      .check(password -> {
        // WHEN
        String hash = Pbkdf2PasswordEncoder.encodePassword(password);

        // THEN
        return !Pbkdf2PasswordEncoder.validatePassword(password + "1", hash);
      }
    );
  }
}

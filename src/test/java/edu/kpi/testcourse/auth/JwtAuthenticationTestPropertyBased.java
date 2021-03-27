package edu.kpi.testcourse.auth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.text.ParseException;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest
public class JwtAuthenticationTestPropertyBased {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTestPropertyBased.class);

  @Inject
  @Client("/")
  private RxHttpClient client;

  @Test
  public void checkAuthorized() throws ParseException {
    qt()
      .forAll(strings().numeric())
      .check(randomString -> {
          // GIVEN
          var credentials = new UsernamePasswordCredentials(
            randomString,
            randomString
          );

          // WHEN
          HttpResponse<?> signUp = client.toBlocking().exchange(
            HttpRequest.POST("/signup", credentials)
          );

          // THEN
          assertThat(signUp.getStatus()).isEqualTo(HttpStatus.OK);

          // WHEN
          HttpResponse<BearerAccessRefreshToken> signIn = client.toBlocking().exchange(
            HttpRequest.POST("/signin", credentials),
            BearerAccessRefreshToken.class
          );

          // THEN
          BearerAccessRefreshToken token = signIn.getBody().orElseThrow();
          assertThat(token.getUsername()).isEqualTo(credentials.getUsername());
          try {
            assertThat(JWTParser.parse(token.getAccessToken())).isInstanceOf(SignedJWT.class);
          } catch (ParseException e) {
            return false;
          }

          // WHEN
          HttpRequest<?> hello = HttpRequest.GET("/hello")
            .bearerAuth(token.getAccessToken());
          HttpResponse<String> response = client.toBlocking().exchange(hello, String.class);

          // THEN
          assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
          return true;
        }
      );
  }
}

package it.smartcommunitylab.climb.domain.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

public class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {
  public final OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

  private final String aud;

  public JwtAudienceValidator(String audience) {
      Assert.hasText(audience, "A non-empty audience is required");
      this.aud = audience;
  }

  public OAuth2TokenValidatorResult validate(Jwt jwt) {
      if (jwt.getAudience().contains(aud)) {
          return OAuth2TokenValidatorResult.success();
      } else {
          return OAuth2TokenValidatorResult.failure(error);
      }
  }
}

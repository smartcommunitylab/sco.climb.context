package it.smartcommunitylab.climb.domain.security;

import java.util.List;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;

public class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {
  public final OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

  private final List<String> aud;

  public JwtAudienceValidator(List<String> audiences) {
      Assert.notNull(audiences, "A non-empty audience is required");
      this.aud = audiences;
  }

  public OAuth2TokenValidatorResult validate(Jwt jwt) {
      for(String audience : aud) {
    	  if(jwt.getAudience().contains(audience))
              return OAuth2TokenValidatorResult.success();    	  
      }
      return OAuth2TokenValidatorResult.failure(error);
  }
}

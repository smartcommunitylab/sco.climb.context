package it.smartcommunitylab.climb.domain.security;

import java.util.Collection;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.Assert;

public class AudienceValidatorTokenIntrospector implements OpaqueTokenIntrospector, InitializingBean {
  private final OpaqueTokenIntrospector introspector;
  private String audience;

  public AudienceValidatorTokenIntrospector(OpaqueTokenIntrospector introspector) {
      Assert.notNull(introspector, "introspector missing, check config");
      this.introspector = introspector;
  }

  public AudienceValidatorTokenIntrospector(String audience, OpaqueTokenIntrospector introspector) {
      Assert.notNull(introspector, "introspector missing, check config");
      Assert.hasText(audience, "audience can not be null or blank");
      this.introspector = introspector;
      this.audience = audience;
  }

  public void setAudience(String audience) {
      this.audience = audience;
  }

  @Override
  public OAuth2AuthenticatedPrincipal introspect(String token) {
      // delegate then check audience
      OAuth2AuthenticatedPrincipal principal = introspector.introspect(token);
      if (principal != null) {
          Object attr = principal.getAttribute(OAuth2IntrospectionClaimNames.AUDIENCE);
          if (attr == null) {
              throw new OAuth2IntrospectionException("Provided token has not valid audience");
          }

          boolean valid = false;
          if (attr instanceof Collection<?>) {
              valid = ((Collection<?>) attr).stream().anyMatch(a -> audience.equals(a));
          } else if (attr instanceof String) {
              valid = audience.equals((String) attr);
          }

          if (!valid) {
              throw new OAuth2IntrospectionException("Provided token has not valid audience");
          }

      }

      return principal;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
      Assert.hasText(audience, "audience can not be null or blank");
  }
}

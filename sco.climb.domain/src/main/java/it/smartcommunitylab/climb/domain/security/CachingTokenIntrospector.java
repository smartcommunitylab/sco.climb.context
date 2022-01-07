package it.smartcommunitylab.climb.domain.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.Assert;

public class CachingTokenIntrospector implements OpaqueTokenIntrospector, InitializingBean {
  private final static String EXP_ATTRIBUTE = "exp";
  private final static int DEFAULT_EXPIRATION = 60 * 60;
  private final static int MAX_EXPIRATION = 60 * 60 * 8;
  private final Map<String, TokenCache<OAuth2AuthenticatedPrincipal>> cache = new ConcurrentHashMap<>();

  private final OpaqueTokenIntrospector introspector;
  private int defaultExpiration;
  private String expirationAttribute;

  public CachingTokenIntrospector(String introspectionUrl, String clientId, String clientSecret) {
      Assert.hasText(introspectionUrl, "introspection url is required");
      Assert.hasText(clientId, "clientId  is required");
      Assert.hasText(clientSecret, "clientSecret  is required");

      // build a standard opaque introspector
      introspector = new NimbusOpaqueTokenIntrospector(introspectionUrl, clientId, clientSecret);

      // set default expiration and attribute
      defaultExpiration = DEFAULT_EXPIRATION;
      expirationAttribute = EXP_ATTRIBUTE;
  }

  public void setDefaultExpiration(int expirationSeconds) {
      Assert.isTrue(expirationSeconds > 0, "expiration must be >0");
      this.defaultExpiration = expirationSeconds;
  }

  public void setExpirationAttribute(String attributeName) {
      Assert.hasText(attributeName, "expiration attribute can not be null or empty");
      this.expirationAttribute = attributeName;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
      Assert.notNull(introspector, "introspector missing, check config");
  }

  private synchronized OAuth2AuthenticatedPrincipal get(String token) {

      if (cache.containsKey(token)) {
          // evaluate expire
          if (Instant.now().getEpochSecond() > cache.get(token).getExpires()) {
              cache.remove(token);
          }
      }

      TokenCache<OAuth2AuthenticatedPrincipal> auth = cache.computeIfAbsent(token, (key) -> {
          OAuth2AuthenticatedPrincipal p = introspector.introspect(key);
          // check for expires attribute
          long now = Instant.now().getEpochSecond();
          long expires = now + defaultExpiration;
          if (p.getAttribute(expirationAttribute) != null) {
              try {
                  // nimbus returns an instant after parsing
                  Instant exp = (Instant) (p.getAttribute(expirationAttribute));
                  expires = exp.getEpochSecond();
              } catch (Exception e) {
              }
          }

          // ensure max duration is not exceeded
          long max = now + MAX_EXPIRATION;
          if (expires > max) {
              expires = max;
          }

          return new TokenCache<>(expires, p);
      });

      return auth.getPrincipal();
  }

  @Override
  public OAuth2AuthenticatedPrincipal introspect(String token) {
      return get(token);
  }

  private class TokenCache<T extends AuthenticatedPrincipal> {
      private final long expires;
      private final T principal;

      public TokenCache(long expires, T principal) {
          this.expires = expires;
          this.principal = principal;
      }

      public long getExpires() {
          return expires;
      }

      public T getPrincipal() {
          return principal;
      }

  }
}

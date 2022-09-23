package it.smartcommunitylab.climb.domain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.util.StringUtils;

import it.smartcommunitylab.climb.domain.security.AudienceValidatorTokenIntrospector;
import it.smartcommunitylab.climb.domain.security.CachingTokenIntrospector;
import it.smartcommunitylab.climb.domain.security.JwtAudienceValidator;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${security.oauth2.resourceserver.jwt.issuer-uri}")
  private String jwtIssuerUri;

  @Value("${security.oauth2.resourceserver.jwt.client-id}")
  private String jwtAudience;

  @Value("${security.oauth2.resourceserver.opaque-token.introspection-uri}")
  private String introspectionUri;

  @Value("${security.oauth2.resourceserver.opaque-token.client-id}")
  private String introspectClientId;

  @Value("${security.oauth2.resourceserver.opaque-token.client-secret}")
  private String introspectClientSecret;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http
              .authorizeRequests()
              .antMatchers("/v2/api-docs",
                  "/configuration/ui",
                  "/swagger-resources/**",
                  "/configuration/**",
                  "/swagger-ui.html",
                  "/webjars/**",
                  "/backend/**",
                  "/public/**",
                  "/game-public/**",
                  "/game-dashboard/**").permitAll()
              .anyRequest().authenticated().and()
              .oauth2ResourceServer(oauth2 -> {
                  if (useJwt()) {
                      oauth2.jwt().decoder(jwtDecoder());
                  } else {
                      oauth2.opaqueToken().introspector(tokenIntrospector());
                  }
              })
              // disable request cache, we override redirects but still better enforce it
              .requestCache((requestCache) -> requestCache.disable())
              .exceptionHandling()
              // use 403
              .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
              .accessDeniedPage("/accesserror")
              .and()
              .csrf()
              .disable()
              .cors();

      // we don't want a session for a REST backend
      // each request should be evaluated
      http.sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  /*
   * Use a custom caching token introspector with audience validation: we look for
   * clientId in audience
   */
  public OpaqueTokenIntrospector tokenIntrospector() {

      OpaqueTokenIntrospector introspector = new CachingTokenIntrospector(introspectionUri, introspectClientId,
              introspectClientSecret);
      return new AudienceValidatorTokenIntrospector(introspectClientId, introspector);
  }

  /*
   * JWT decoder with audience validation
   */

  public JwtDecoder jwtDecoder() {
      // build validators for issuer + timestamp (default) and audience
      OAuth2TokenValidator<Jwt> audienceValidator = new JwtAudienceValidator(jwtAudience);
      OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(jwtIssuerUri.trim());

      // build default decoder and then use custom validators
      NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(jwtIssuerUri.trim());
      jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator));

      return jwtDecoder;
  }

  //
  private boolean useJwt() {
      return StringUtils.hasText(jwtIssuerUri);
  }
}

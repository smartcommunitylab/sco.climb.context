package it.smartcommunitylab.climb.domain.config;

import it.smartcommunitylab.climb.domain.security.AacUserInfoTokenServices;
import it.smartcommunitylab.climb.domain.security.DataSetDetails;
import it.smartcommunitylab.climb.domain.security.DataSetInfo;

import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableOAuth2Client
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  OAuth2ClientContext oauth2ClientContext;
  
  @Bean
  @ConfigurationProperties("security.oauth2.client")
  public AuthorizationCodeResourceDetails aac() {
      return new AuthorizationCodeResourceDetails();
  }

  @Bean
  @ConfigurationProperties("security.oauth2.resource")
  public ResourceServerProperties aacResource() {
      return new ResourceServerProperties();
  }
  
  @Bean
  public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
      FilterRegistrationBean registration = new FilterRegistrationBean();
      registration.setFilter(filter);
      registration.setOrder(-100);
      return registration;
  }
  
  private Filter ssoFilter() {
    OAuth2ClientAuthenticationProcessingFilter aacFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/aac");
    OAuth2RestTemplate aacTemplate = new OAuth2RestTemplate(aac(), oauth2ClientContext);
    aacFilter.setRestTemplate(aacTemplate);
    AacUserInfoTokenServices tokenServices = new AacUserInfoTokenServices(aacResource().getUserInfoUri(), aac().getClientId());
    tokenServices.setRestTemplate(aacTemplate);
    tokenServices.setPrincipalExtractor(new PrincipalExtractor() {
			
			@SuppressWarnings("unchecked")
			@Override
			public Object extractPrincipal(Map<String, Object> map) {
				DataSetInfo dataSetInfo = new DataSetInfo();
				dataSetInfo.setSubject((String) map.get("userId"));
				dataSetInfo.setName((String) map.get("name"));
				dataSetInfo.setSurname((String) map.get("surname"));
				dataSetInfo.setToken((String) map.get("token"));
				Map<String, Object> accounts = (Map<String, Object>) map.get("accounts");
				if(accounts.containsKey("google")) {
					Map<String, Object> google = (Map<String, Object>) accounts.get("google");
					dataSetInfo.setEmail((String) google.get("email"));
				}
				if(accounts.containsKey("adc")) {
					Map<String, Object> adc = (Map<String, Object>) accounts.get("adc");
					dataSetInfo.setEmail((String) adc.get("pat_attribute_codicefiscale"));
				}
				if(accounts.containsKey("facebook")) {
					Map<String, Object> facebook = (Map<String, Object>) accounts.get("facebook");
					dataSetInfo.setEmail((String) facebook.get("email"));
				}
				DataSetDetails details = new DataSetDetails(dataSetInfo);
				return details;
			}
		});
    aacFilter.setTokenServices(tokenServices);
    return aacFilter;
  }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.headers()
			.frameOptions().disable();
		http
			.csrf()
				.disable()
			.antMatcher("/**")
				.authorizeRequests()
			.antMatchers("/", "/index.html", "/login**", "/swagger-ui.html", "/v2/api-docs**")
				.permitAll()
			.antMatchers("/", "/console/**", "/upload/**", "/report/**", "/backend/**")
				.authenticated()
				.and()
					.formLogin()
						.loginPage("/login/aac")
				.and()
					.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
	}
}

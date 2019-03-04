package it.smartcommunitylab.climb.domain.security;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import it.smartcommunitylab.aac.AACProfileService;
import it.smartcommunitylab.aac.AACService;
import it.smartcommunitylab.aac.model.AccountProfile;

@Component
public class AuthManager {
	@Autowired
	@Value("${oauth.serverUrl}")	
	private String oauthServerUrl;
	
	@Autowired
	@Value("${security.oauth2.client.clientId}")	
	private String clientId;

	@Autowired
	@Value("${security.oauth2.client.clientSecret}")	
	private String clientSecret;
	
	@Autowired
	@Value("${profile.serverUrl}")
	private String profileServerUrl;

	private AACService aacService;
	
	private AACProfileService profileConnector;
	
	private LoadingCache<String, AccountProfile> cache;
	
	@PostConstruct
	public void init() throws Exception {
		aacService = new AACService(oauthServerUrl, clientId, clientSecret);
		profileConnector = new AACProfileService(profileServerUrl);
		
		CacheLoader<String, AccountProfile> loader = new CacheLoader<String, AccountProfile>() {
      @Override
      public AccountProfile load(String key) throws Exception {
      	return profileConnector.findAccountProfile(key);
      }
		};
		cache = CacheBuilder.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(3600,TimeUnit.SECONDS)
	      .build(loader);
	}
	
	public LoadingCache<String, AccountProfile> getCache() {
		return cache;
	}
	
	public AACService getAACService() {
		return aacService;
	}
	
	public AACProfileService getAACProfileService() {
		return profileConnector;
	}
}

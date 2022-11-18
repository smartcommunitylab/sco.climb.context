package it.smartcommunitylab.climb.domain.security;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class AuthManager {
	private LoadingCache<String, AccountProfile> cache;
	
	@PostConstruct
	public void init() throws Exception {
		
		CacheLoader<String, AccountProfile> loader = new CacheLoader<String, AccountProfile>() {
      @Override
      public AccountProfile load(String key) throws Exception {
      	return null; //profileConnector.findAccountProfile(key);
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
	
}

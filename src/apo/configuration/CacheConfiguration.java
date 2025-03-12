package apo.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfiguration
{

	@Bean
	public CacheManager httpCacheManager()
	{
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("httpCache", "interopCache");
		cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS));
		cacheManager.setAsyncCacheMode(false);
		return cacheManager;
	}
}

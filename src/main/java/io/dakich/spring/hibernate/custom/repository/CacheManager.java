package io.dakich.spring.hibernate.custom.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CacheManager {

  public static final String L1_CACHE_STRATEGY_KEY = "hibernate.l1.cache.strategy";
  private static final Cache<String, CACHE_STRATEGY> APP_CACHE = Caffeine.newBuilder()
      // The initial capacity of the cache
      .initialCapacity(1)
      // The maximum number of the cache
      .maximumSize(1)
      .build();

  private CacheManager() {
    throw new UnsupportedOperationException("REFLECTION !? :)");
  }

  public static CACHE_STRATEGY getCacheStrategy() {
    return APP_CACHE.getIfPresent(L1_CACHE_STRATEGY_KEY);
  }

  public static void setCacheStrategy(String cacheStrategy) {
    switch (cacheStrategy) {
      case "detach" -> APP_CACHE.put(L1_CACHE_STRATEGY_KEY, CACHE_STRATEGY.DETACH);
      case "evict" -> APP_CACHE.put(L1_CACHE_STRATEGY_KEY, CACHE_STRATEGY.EVICT);
      default -> APP_CACHE.put(L1_CACHE_STRATEGY_KEY, CACHE_STRATEGY.L1_CACHE_ACTIVE);
    }
  }

}

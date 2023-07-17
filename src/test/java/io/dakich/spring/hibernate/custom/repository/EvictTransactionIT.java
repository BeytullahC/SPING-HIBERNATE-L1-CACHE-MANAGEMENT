package io.dakich.spring.hibernate.custom.repository;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("evict-trx")
public class EvictTransactionIT extends AbstractTransaction {

  @Override
  protected CACHE_STRATEGY childExpectedCacheStrategy() {
    return CACHE_STRATEGY.EVICT;
  }
}

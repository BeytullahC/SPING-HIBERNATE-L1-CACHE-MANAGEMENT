package io.dakich.spring.hibernate.custom.repository;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("detach-trx")
public class DetachTransactionIT extends AbstractTransaction {


  @Override
  protected CACHE_STRATEGY childExpectedCacheStrategy() {
    return CACHE_STRATEGY.DETACH;
  }


}

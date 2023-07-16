package io.dakich.spring.hibernate.custom.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("evict-logger-default")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfig.class})
class SimpleJpaRepositoryImplEvictLoggerInfoIT extends AbstractSimpleJpaRepositoryImplIT{

  @Autowired
  private UserRepository userRepository;


  @Override
  protected UserRepository userRepository() {
    return userRepository;
  }

  protected CACHE_STRATEGY childExpectedCacheStrategy() {
    return CACHE_STRATEGY.EVICT;
  }


}


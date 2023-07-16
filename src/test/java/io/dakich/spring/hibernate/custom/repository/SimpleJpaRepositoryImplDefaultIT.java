package io.dakich.spring.hibernate.custom.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("default")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfig.class})
class SimpleJpaRepositoryImplDefaultIT extends AbstractSimpleJpaRepositoryImplIT{

  @Autowired
  private UserRepository userRepository;

  @Override
  protected UserRepository userRepository() {
    return userRepository;
  }

  @Override
  protected CACHE_STRATEGY childExpectedCacheStrategy() {
    return CACHE_STRATEGY.L1_CACHE_ACTIVE;
  }

}


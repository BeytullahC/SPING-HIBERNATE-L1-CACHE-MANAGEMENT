package io.dakich.spring.hibernate.custom.repository;

import io.dakich.spring.hibernate.custom.domain.Role;
import io.dakich.spring.hibernate.custom.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("evict")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfig.class})
class SimpleJpaRepositoryImplEvictIT extends AbstractSimpleJpaRepositoryImplIT{

  @Autowired
  private UserRepository userRepository;

  @Override
  protected UserRepository userRepository() {
    return userRepository;
  }

  @Override
  protected CACHE_STRATEGY childExpectedCacheStrategy() {
    return CACHE_STRATEGY.EVICT;
  }

}


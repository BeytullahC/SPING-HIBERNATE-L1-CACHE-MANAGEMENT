package io.dakich.spring.hibernate.custom.repository;

import io.dakich.spring.hibernate.custom.repository.config.TestConfig;
import io.dakich.spring.hibernate.custom.repository.sample.RoleRepository;
import io.dakich.spring.hibernate.custom.repository.sample.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("evict")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfig.class})
class SimpleJpaRepositoryImplEvictIT extends AbstractSimpleJpaRepositoryImplIT {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  public RoleRepository roleRepository() {
    return roleRepository;
  }


  @Override
  protected UserRepository userRepository() {
    return userRepository;
  }

  protected CACHE_STRATEGY childExpectedCacheStrategy() {
    return CACHE_STRATEGY.EVICT;
  }


}


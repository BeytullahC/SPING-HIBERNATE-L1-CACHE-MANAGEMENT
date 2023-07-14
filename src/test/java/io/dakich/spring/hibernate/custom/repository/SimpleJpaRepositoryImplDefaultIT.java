package io.dakich.spring.hibernate.custom.repository;

import io.dakich.spring.hibernate.custom.domain.Role;
import io.dakich.spring.hibernate.custom.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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


package io.dakich.spring.hibernate.custom.repository;

import io.dakich.spring.hibernate.custom.domain.Role;
import io.dakich.spring.hibernate.custom.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@TestMethodOrder(OrderAnnotation.class)
abstract class AbstractSimpleJpaRepositoryImplIT {


  protected abstract UserRepository userRepository();
  protected abstract CACHE_STRATEGY childExpectedCacheStrategy();
  @BeforeEach
  void cacheModeTest(){
    Assertions.assertSame(CacheManager.getCacheStrategy(), childExpectedCacheStrategy());
  }
  @Test
  @Order(1)
  @Transactional
  public void contextTest() {
    userRepository().save(new User("test","test","test",new Role("test")));
  }

}


package io.dakich.spring.hibernate.custom.repository;

import io.dakich.spring.hibernate.custom.domain.Role;
import io.dakich.spring.hibernate.custom.domain.User;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
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
  @Transactional(transactionManager = "transactionManager",propagation = Propagation.REQUIRES_NEW)
  public void contextTest() {
    final User user = new User("test", "test", "test", new Role("test"));
    final User save = userRepository().save(user);
    userRepository().findOne(Example.of(save));
    userRepository().getOne(1);
    userRepository().getOne(0);
    userRepository().getReferenceById(1);
    userRepository().getReferenceById(0);
    userRepository().getById(1);
    userRepository().getById(0);
    userRepository().findAll(Example.of(save), Sort.unsorted());
    userRepository().findAll();
    userRepository().findAll(Pageable.unpaged());
    userRepository().findAll(Sort.unsorted());
    userRepository().findAll(nameLike("test"));
    userRepository().findAll(nameLike("test"),Sort.unsorted());
    userRepository().findOne(nameLike("test"));
    userRepository().findAllById(Collections.singleton(1));
    userRepository().findAll(nameLike("test2"));
    userRepository().findAll(nameLike("test2"),Sort.unsorted());
    userRepository().findOne(nameLike("test2"));
    userRepository().findAllById(Collections.singleton(0));
  }

  private Specification<User> nameLike(String name){
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("lastname"), "%"+name+"%");
  }

}


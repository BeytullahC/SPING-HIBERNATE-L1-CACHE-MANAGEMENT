package io.dakich.spring.hibernate.custom.repository;

import io.dakich.spring.hibernate.custom.domain.Role;
import io.dakich.spring.hibernate.custom.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestConfig.class})
class SimpleJpaRepositoryImplIIT {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void contextTest() {
    userRepository.save(new User("test","test","test",new Role("test")));
  }

}


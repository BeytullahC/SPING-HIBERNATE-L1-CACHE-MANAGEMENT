package io.dakich.spring.hibernate.custom.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("evict-userrepo")
@ExtendWith({SpringExtension.class,TestDurationReportExtension.class})
@SpringBootTest(classes = {TestConfig.class,ApplicationContextUtil.class})
@Transactional
public class EvictUserRepositoryIT extends
    AbstractUserRepositoryIT {


  @PersistenceContext
  EntityManager em;

  // CUT
  @Autowired
  UserRepository repository;

  @Autowired
  PlatformTransactionManager transactionManager;

  @Override
  protected EntityManager em() {
    return em;
  }

  @Override
  protected UserRepository repository() {
    return repository;
  }

  @Override
  protected PlatformTransactionManager transactionManager() {
    return transactionManager;
  }

}

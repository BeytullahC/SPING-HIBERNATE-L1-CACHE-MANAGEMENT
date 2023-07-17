package io.dakich.spring.hibernate.custom.repository;

import io.dakich.spring.hibernate.custom.domain.Role;
import io.dakich.spring.hibernate.custom.domain.User;
import jakarta.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@ActiveProfiles("default")
@SpringBootTest(classes = TestConfig.class)
@ExtendWith(SpringExtension.class)
public class DefaultTransactionITImpl {

  private static final Logger LOG = LoggerFactory.getLogger(
      io.dakich.spring.hibernate.custom.repository.DefaultTransactionITImpl.class);
  @Autowired
  private PlatformTransactionManager transactionManager;

  private TransactionTemplate transactionTemplate;

  @Autowired
  private EntityManager entityManager;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private AsyncTaskExecutor taskExecutor;
  @Autowired
  private DataSource dataSource;


  @BeforeEach
  void setUp() {
    transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Test
  void testSaveParallel() throws InterruptedException, ExecutionException {
    Assertions.assertTrue(CacheManager.getCacheStrategy()==CACHE_STRATEGY.L1_CACHE_ACTIVE);
    roleRepository.save(new Role("test"));
    transactionTemplate.executeWithoutResult(status -> {
      final List<Integer> list = new ArrayList<>();
      final Future<Boolean> submit = taskExecutor.submit(() -> list.add(save(1)));
      final Future<Boolean> submit1 = taskExecutor.submit(() -> list.add(save(2)));
      final Future<Boolean> submit2 = taskExecutor.submit(() -> list.add(save(3)));
      while (!(submit.isDone() && submit1.isDone() && submit2.isDone())) {
        try {
          TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      final List<User> all = userRepository.findAll();
      all.forEach(a -> LOG.warn("DATA:{}", a.isActive()));
      Assertions.assertTrue(userRepository.findById(1).get().isActive());
      Assertions.assertTrue(userRepository.findById(2).get().isActive());
      Assertions.assertTrue(userRepository.findById(3).get().isActive());

      final Future upd1 = taskExecutor.submit(
          () -> execOutOfTrans("UPDATE SD_User set ACTIVE='false' where id=1"));
      final Future upd2 = taskExecutor.submit(
          () -> execOutOfTrans("UPDATE SD_User set ACTIVE='false' where id=2"));

      while (!(upd1.isDone() && upd2.isDone())) {
        try {
          TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      final List<User> all2 = userRepository.findAll();
      LOG.warn(String.valueOf(list.size()));
      all2.forEach(a -> LOG.warn("DATA:{}", a.isActive()));

      Assertions.assertTrue(userRepository.findById(1).get().isActive());
      Assertions.assertTrue(userRepository.findById(2).get().isActive());
      Assertions.assertTrue(userRepository.findById(3).get().isActive());
    });
  }

  public Integer save(Integer a) {
    try {
      Role role = roleRepository.getReferenceById(1);
      final User user = new User("test" + a, "test" + a, "test" + a, role);
      final User save = userRepository.save(user);
      return save.getId();
    } catch (Throwable t) {
      throw new RuntimeException(t.getMessage());
    }
  }

  void execOutOfTrans(String sql) {

    try (Connection con = dataSource.getConnection();

        PreparedStatement ps = con.prepareStatement(sql)

    ) {
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private Specification<User> nameLike(String name) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("lastname"), "%" + name + "%");
  }

}


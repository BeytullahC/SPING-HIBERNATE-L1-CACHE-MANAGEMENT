package io.dakich.spring.hibernate.custom.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.dakich.spring.hibernate.custom.domain.User;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.jpa.repository.support.MutableQueryHints;


public class SimpleJpaRepositoryDetachUnitTest extends
    AbstractSimpleJpaRepositoryUnitTests {


  @BeforeEach
  void setUp() {
    CacheManager.setCacheStrategy("detach");
    when(em.getDelegate()).thenReturn(em);

    when(information.getJavaType()).thenReturn(User.class);
    when(em.getCriteriaBuilder()).thenReturn(builder);

    when(builder.createQuery(User.class)).thenReturn(criteriaQuery);
    when(builder.createQuery(Long.class)).thenReturn(countCriteriaQuery);

    when(em.createQuery(criteriaQuery)).thenReturn(query);
    when(em.createQuery(countCriteriaQuery)).thenReturn(countQuery);

    MutableQueryHints hints = new MutableQueryHints();
    when(metadata.getQueryHints()).thenReturn(hints);
    when(metadata.getQueryHintsForCount()).thenReturn(hints);
    when(session.contains(any())).thenReturn(true);
    when(em.unwrap(Session.class)).thenReturn(session);
    repo = new SimpleJpaRepositoryImpl<>(information, em);
    repo.setRepositoryMethodMetadata(metadata);
  }

}

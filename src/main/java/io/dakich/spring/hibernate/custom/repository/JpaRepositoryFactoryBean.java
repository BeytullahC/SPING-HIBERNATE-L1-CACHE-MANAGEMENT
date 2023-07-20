package io.dakich.spring.hibernate.custom.repository;

import java.io.Serializable;

import jakarta.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * {@link org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean} to return a custom repository base class.
 *
 * @author Gil Markham
 * @author Oliver Gierke
 */
public class JpaRepositoryFactoryBean<T extends JpaRepository<Object, Serializable>>
    extends
    org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean<T, Object, Serializable> {

  public JpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
    super(repositoryInterface);
  }

  @Override
  protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {

    return new JpaRepositoryFactory(em);
  }
}

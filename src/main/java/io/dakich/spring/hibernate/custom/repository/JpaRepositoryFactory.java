package io.dakich.spring.hibernate.custom.repository;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Sample implementation of a custom {@link org.springframework.data.jpa.repository.support.JpaRepositoryFactory} to use a custom repository base class.
 *
 * @author Oliver Gierke
 */
public class JpaRepositoryFactory extends
    org.springframework.data.jpa.repository.support.JpaRepositoryFactory {


  private Environment environment;

  /**
   * @param entityManager
   */
  public JpaRepositoryFactory(EntityManager entityManager) {

    super(entityManager);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager em) {
    JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
    if(CACHE_STRATEGY.L1_CACHE_ACTIVE!=CacheManager.getCacheStrategy()||CacheManager.getCacheStrategy()!=null)
      return new SimpleJpaRepositoryImpl<>(entityInformation, em);
    else return new SimpleJpaRepository<>(entityInformation,em);
  }

  @Override
  protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
    return SimpleJpaRepositoryImpl.class;
  }


  public void setEnvironment(@Autowired Environment environment) {
    this.environment = environment;
  }
}

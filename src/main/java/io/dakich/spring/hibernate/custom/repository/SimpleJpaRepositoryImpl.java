package io.dakich.spring.hibernate.custom.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
@Transactional(readOnly = true)
public class SimpleJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJpaRepositoryImpl.class);
  private final JpaEntityInformation<T, ?> entityInformation;
  private final EntityManager entityManager;

  public SimpleJpaRepositoryImpl(
      JpaEntityInformation<T, ?> entityInformation,
      EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityInformation = entityInformation;
    this.entityManager = entityManager;
  }

  private void clearL1Cache(List<T> all) {
    if (all != null && !all.isEmpty()) {
      all.forEach(this::clearL1Cache);
    }
  }

  private <S extends T> void clearL1Cache(Optional<S> one) {
    if (one!=null && one.isPresent()) {
      clearL1Cache(one.get());
    }
  }


  private void clearL1Cache(Page<T> all) {
    if (all != null && !all.isEmpty()) {
      clearL1Cache(all.toList());
    }
  }

  private <S extends T> void clearL1Cache(S s) {
    if (CacheManager.getCacheStrategy() != CACHE_STRATEGY.L1_CACHE_ACTIVE) {
      final Session session = entityManager.unwrap(Session.class);
      if (session.contains(s)) {
        if (CacheManager.getCacheStrategy() == CACHE_STRATEGY.EVICT) {
          session.evict(s);
        } else {
          session.detach(s);
        }
        if (LOGGER.isDebugEnabled() && !session.contains(s)) {
          LOGGER.debug("L1 CACHE Clear");
        }
      }

    }
  }

  @Override
  @Transactional
  public <S extends T> S save(S entity) {
    final S save = super.save(entity);
    entityManager.flush();
    clearL1Cache(save);
    return save;
  }

  @Override
  public List<T> findAll() {
    final List<T> all = super.findAll();
    clearL1Cache(all);
    return all;
  }

  @Override
  public List<T> findAllById(Iterable<ID> ids) {
    final List<T> allById = super.findAllById(ids);
    clearL1Cache(allById);
    return allById;
  }

  @Override
  public List<T> findAll(Sort sort) {
    final List<T> all = super.findAll(sort);
    clearL1Cache(all);
    return all;
  }

  @Override
  public Page<T> findAll(Pageable pageable) {
    final Page<T> all = super.findAll(pageable);
    clearL1Cache(all);
    return all;
  }


  @Override
  public Optional<T> findOne(Specification<T> spec) {
    final Optional<T> one = super.findOne(spec);
    clearL1Cache(one);
    return one;
  }

  @Override
  public List<T> findAll(Specification<T> spec) {
    final List<T> all = super.findAll(spec);
    clearL1Cache(all);
    return all;
  }

  @Override
  public Page<T> findAll(Specification<T> spec, Pageable pageable) {
    final Page<T> all = super.findAll(spec, pageable);
    clearL1Cache(all);
    return all;
  }

  @Override
  public List<T> findAll(Specification<T> spec, Sort sort) {
    final List<T> all = super.findAll(spec, sort);
    clearL1Cache(all);
    return all;
  }

  @Override
  public <S extends T> Optional<S> findOne(Example<S> example) {
    final Optional<S> one = super.findOne(example);
    clearL1Cache(one);
    return one;
  }

  @Deprecated
  @Override
  public T getOne(ID id) {
    final T one = super.getOne(id);
    clearL1Cache(one);
    return one;
  }

  @Deprecated
  @Override
  public T getById(ID id) {
    final T byId = super.getById(id);
    clearL1Cache(byId);
    return byId;
  }

  @Override
  public T getReferenceById(ID id) {
    final T referenceById = super.getReferenceById(id);
    clearL1Cache(referenceById);
    return referenceById;
  }
}

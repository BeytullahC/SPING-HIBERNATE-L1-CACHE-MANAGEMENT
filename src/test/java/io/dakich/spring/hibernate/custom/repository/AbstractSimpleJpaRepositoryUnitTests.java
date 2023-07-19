package io.dakich.spring.hibernate.custom.repository;

import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.jpa.domain.Specification.where;

import io.dakich.spring.hibernate.custom.domain.User;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.MutableQueryHints;
import org.springframework.data.repository.CrudRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
abstract class AbstractSimpleJpaRepositoryUnitTests {

  protected SimpleJpaRepositoryImpl<User, Integer> repo;

  @Mock
  EntityManager em;
  @Mock
  EntityManagerFactory entityManagerFactory;
  @Mock
  PersistenceUnitUtil persistenceUnitUtil;
  @Mock
  CriteriaBuilder builder;
  @Mock CriteriaQuery<User> criteriaQuery;
  @Mock
  CriteriaQuery<Long> countCriteriaQuery;
  @Mock TypedQuery<User> query;
  @Mock
  TypedQuery<Long> countQuery;
  @Mock JpaEntityInformation<User, Integer> information;
  @Mock
  CrudMethodMetadata metadata;
  @Mock
  EntityGraph<User> entityGraph;
  @Mock
  Session session;
  @Mock org.springframework.data.jpa.repository.EntityGraph entityGraphAnnotation;

  @BeforeEach
  void setUp() {

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
    when(em.unwrap(Session.class)).thenReturn(session);

    repo = new SimpleJpaRepositoryImpl<>(information, em);
    repo.setRepositoryMethodMetadata(metadata);
  }

  @Test // DATAJPA-124, DATAJPA-912
  void retrieveObjectsForPageableOutOfRange() {

    when(countQuery.getSingleResult()).thenReturn(20L);
    repo.findAll(PageRequest.of(2, 10));

    verify(query).getResultList();
  }

  @Test // DATAJPA-912
  void doesNotRetrieveCountWithoutOffsetAndResultsWithinPageSize() {

    when(query.getResultList()).thenReturn(Arrays.asList(new User(), new User()));

    repo.findAll(PageRequest.of(0, 10));

    verify(countQuery, never()).getSingleResult();
  }

  @Test // DATAJPA-912
  void doesNotRetrieveCountWithOffsetAndResultsWithinPageSize() {

    when(query.getResultList()).thenReturn(Arrays.asList(new User(), new User()));
    when(session.contains(any())).thenReturn(true);
    repo.findAll(PageRequest.of(2, 10));

    verify(countQuery, never()).getSingleResult();
  }

  @Test // DATAJPA-177, gh-2719
  void doesNotThrowExceptionIfEntityToDeleteDoesNotExist() {
     repo.deleteById(4711);
  }

  @Test // DATAJPA-689, DATAJPA-696
  @SuppressWarnings({ "rawtypes", "unchecked" })
  void shouldPropagateConfiguredEntityGraphToFindOne() throws Exception {

    String entityGraphName = "User.detail";
    when(entityGraphAnnotation.value()).thenReturn(entityGraphName);
    when(entityGraphAnnotation.type()).thenReturn(EntityGraphType.LOAD);
    when(metadata.getEntityGraph()).thenReturn(Optional.of(entityGraphAnnotation));
    when(em.getEntityGraph(entityGraphName)).thenReturn((EntityGraph) entityGraph);
    when(information.getEntityName()).thenReturn("User");
    when(metadata.getMethod()).thenReturn(CrudRepository.class.getMethod("findById", Object.class));

    Integer id = 0;
    repo.findById(id);

    verify(em).find(User.class, id, singletonMap(EntityGraphType.LOAD.getKey(), (Object) entityGraph));
  }

  @Test // DATAJPA-931
  void mergeGetsCalledWhenDetached() {

    User detachedUser = new User();

    when(em.contains(detachedUser)).thenReturn(false);

    repo.save(detachedUser);

    verify(em).merge(detachedUser);
  }

  @Test // DATAJPA-931, DATAJPA-1261
  void mergeGetsCalledWhenAttached() {

    User attachedUser = new User();

    when(em.contains(attachedUser)).thenReturn(true);

    repo.save(attachedUser);

    verify(em).merge(attachedUser);
  }

  @Test // DATAJPA-1535
  void doNothingWhenNewInstanceGetsDeleted() {

    User newUser = new User();
    newUser.setId(null);

    when(em.getEntityManagerFactory()).thenReturn(entityManagerFactory);
    when(entityManagerFactory.getPersistenceUnitUtil()).thenReturn(persistenceUnitUtil);

    repo.delete(newUser);

    verify(em, never()).find(any(Class.class), any(Object.class));
    verify(em, never()).remove(newUser);
    verify(em, never()).merge(newUser);
  }

  @Test
  void doNothingWhenNonExistentInstanceGetsDeleted() {

    User newUser = new User();
    newUser.setId(23);

    when(information.isNew(newUser)).thenReturn(false);
    when(em.getEntityManagerFactory()).thenReturn(entityManagerFactory);
    when(entityManagerFactory.getPersistenceUnitUtil()).thenReturn(persistenceUnitUtil);
    when(persistenceUnitUtil.getIdentifier(any())).thenReturn(23);
    when(em.find(User.class, 23)).thenReturn(null);

    repo.delete(newUser);

    verify(em, never()).remove(newUser);
    verify(em, never()).merge(newUser);
  }

  @Test
    // GH-2054
  void applyQueryHintsToCountQueriesForSpecificationPageables() {

    when(query.getResultList()).thenReturn(Arrays.asList(new User(), new User()));

    repo.findAll(where(null), PageRequest.of(2, 1));

    verify(metadata).getQueryHintsForCount();
  }


  @Test
  void testClearL1CacheListEmpty() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, List.class);
    try {
      lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
      MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
          methodType);
      handle.invoke(new SimpleJpaRepositoryImpl<>(information, em), new ArrayList<>());
    } catch (Throwable x) {
      x.printStackTrace();
    }
  }

  @Test
  void testClearL1CacheListNull() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, List.class);
    try {
      lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
      MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
          methodType);
      handle.invoke(new SimpleJpaRepositoryImpl<>(information, em), null);
    } catch (Throwable x) {
      x.printStackTrace();
    }
  }

  @Test
  void testClearL1CacheOptionalEmpty() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, Optional.class);
    try {
      lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
      MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
          methodType);
      handle.invoke(new SimpleJpaRepositoryImpl<User, Integer>(information, em),
          Optional.of(new User()));
    } catch (Throwable x) {
      x.printStackTrace();
    }
  }

  @Test
  void testClearL1CacheOptionalNull() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, Optional.class);
    try {
      lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
      MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
          methodType);
      handle.invoke(new SimpleJpaRepositoryImpl<User, Integer>(information, em), null);
    } catch (Throwable x) {
      x.printStackTrace();
    }
  }

  @Test
  void testClearL1CacheOptionalGetNull() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, Optional.class);
    try {
      lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
      MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
          methodType);
      handle.invoke(new SimpleJpaRepositoryImpl<User, Integer>(information, em),
          Optional.ofNullable(null));
    } catch (Throwable x) {
      x.printStackTrace();
    }
  }

  @Test
  void testClearL1CachePageEmpty() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, Page.class);
    try {
      lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
      MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
          methodType);
      handle.invoke(new SimpleJpaRepositoryImpl<User, Integer>(information, em),
          new PageImpl(new ArrayList<>()));
    } catch (Throwable x) {
      x.printStackTrace();
    }
  }

  @Test
  void testClearL1CachePageContentNull() throws IllegalAccessException, NoSuchMethodException {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, Page.class);
    lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
    MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
        methodType);
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> handle.invoke(new SimpleJpaRepositoryImpl<User, Integer>(information, em),
            new PageImpl(null)));
  }

  @Test
  void testClearL1CachePage() throws Throwable {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, Page.class);
    lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
    MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
        methodType);
    Assertions.assertDoesNotThrow(
        () -> handle.invoke(new SimpleJpaRepositoryImpl<User, Integer>(information, em),
            new PageImpl(
                Collections.singletonList(new User()))));
  }

  @Test
  void testClearL1CachePageNull() throws Throwable {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType methodType = MethodType.methodType(void.class, Page.class);
    lookup = MethodHandles.privateLookupIn(SimpleJpaRepositoryImpl.class, lookup);
    MethodHandle handle = lookup.findVirtual(SimpleJpaRepositoryImpl.class, "clearL1Cache",
        methodType);
    Assertions.assertDoesNotThrow(
        () -> handle.invoke(new SimpleJpaRepositoryImpl<User, Integer>(information, em), null));
  }
}

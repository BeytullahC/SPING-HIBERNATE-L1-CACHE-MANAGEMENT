package io.dakich.spring.hibernate.custom.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * Collection of {@link Specification}s for a {@link User}.
 *
 * @author Oliver Gierke
 * @author Diego Krupitza
 */
public class UserSpecifications {

  /**
   * A {@link Specification} to match on a {@link User}'s firstname.
   */
  public static Specification<User> userHasFirstname(final String firstname) {

    return simplePropertySpec("firstname", firstname);
  }

  /**
   * A {@link Specification} to match on a {@link User}'s lastname.
   */
  public static Specification<User> userHasLastname(final String lastname) {

    return simplePropertySpec("lastname", lastname);
  }

  /**
   * A {@link Specification} to do a like-match on a {@link User}'s firstname.
   */
  public static Specification<User> userHasFirstnameLike(final String expression) {

    return (root, query, cb) -> cb.like(root.get("firstname").as(String.class), String.format("%%%s%%", expression));
  }

  /**
   * A {@link Specification} to do an age check.
   *
   * @param age upper (exclusive) bound of the age
   */
  public static Specification<User> userHasAgeLess(final Integer age) {

    return (root, query, cb) -> cb.lessThan(root.get("age").as(Integer.class), age);
  }

  /**
   * A {@link Specification} to do a like-match on a {@link User}'s lastname but also adding a sort order on the
   * firstname.
   */
  public static Specification<User> userHasLastnameLikeWithSort(final String expression) {

    return (root, query, cb) -> {

      query.orderBy(cb.asc(root.get("firstname")));

      return cb.like(root.get("lastname").as(String.class), String.format("%%%s%%", expression));
    };
  }

  private static <T> Specification<T> simplePropertySpec(final String property, final Object value) {

    return (root, query, builder) -> builder.equal(root.get(property), value);
  }
}

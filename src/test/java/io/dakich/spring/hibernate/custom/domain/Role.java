package io.dakich.spring.hibernate.custom.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * Sample domain class representing roles. Mapped with XML.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@Entity
public class Role {

  private static final String PREFIX = "ROLE_";

  @Id
  @GeneratedValue
  private Integer id;
  private String name;

  /**
   * Creates a new instance of {@code Role}.
   */
  public Role() {
  }

  /**
   * Creates a new preconfigured {@code Role}.
   *
   * @param name
   */
  public Role(final String name) {
    this.name = name;
  }

  /**
   * Returns the id.
   *
   * @return
   */
  public Integer getId() {

    return id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {

    return PREFIX + name;
  }

  /**
   * Returns whether the role is to be considered new.
   *
   * @return
   */
  public boolean isNew() {

    return id == null;
  }
}

package io.dakich.spring.hibernate.custom.domain;

// DATAJPA-1334
public class NameOnlyDto {

  private String firstname;
  private String lastname;

  public NameOnlyDto(String firstname, String lastname) {
    this.firstname = firstname;
    this.lastname = lastname;
  }
}

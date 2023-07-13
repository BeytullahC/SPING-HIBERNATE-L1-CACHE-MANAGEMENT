package io.dakich.spring.hibernate.custom.domain;


import jakarta.persistence.Embeddable;

/**
 * @author Thomas Darimont
 */
@Embeddable
public class Address {

  private String country;
  private String city;
  private String streetName;
  private String streetNo;

  public Address() {
  }

  public Address(String country, String city, String streetName, String streetNo) {
    this.country = country;
    this.city = city;
    this.streetName = streetName;
    this.streetNo = streetNo;
  }

  public String getCountry() {
    return country;
  }

  public String getCity() {
    return city;
  }

  public String getStreetName() {
    return streetName;
  }

  public String getStreetNo() {
    return streetNo;
  }
}

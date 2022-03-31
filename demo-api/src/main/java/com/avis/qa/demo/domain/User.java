package com.avis.qa.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class User {

  private long id;
  private String firstName;
  private String lastName;
  private int email;

}
package edu.utec.test.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
  // public int order();
  public int value();
}

package edu.utec.tools.trext.common;

import org.junit.Before;
import org.junit.Test;

public class MethodEnhancersTest {

  @Before
  public void setup() {
    new MethodEnhancers();
  }

  @Test
  public void elapsedMillisToHumanExpression() throws Exception {
    System.out.println(System.getProperty("java.class.path"));
  }

}

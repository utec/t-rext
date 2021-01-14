package edu.utec.tools.trext.common;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class TimeHelperTest {

  @Before
  public void setup() {
    new TimeHelper();
  }

  @Test
  public void elapsedMillisToHumanExpression() throws Exception {
    assertEquals("0 min 3 seg 600 millis",
        TimeHelper.elapsedMillisToHumanExpression(1000000000l, 1000003600l));
  }

}

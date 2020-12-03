package edu.utec.tools.trext.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.utec.test.junit.DefaultOrderedRunner;
import edu.utec.test.junit.ExplicitOrder;

@RunWith(DefaultOrderedRunner.class)
@ExplicitOrder()
public class VariablePlaceHolderHelperTest {

  @Test
  public void getStringRepresentation() throws Exception {

    assertEquals("foo", VariablePlaceHolderHelper.getStringRepresentation("foo"));
    assertEquals("500", VariablePlaceHolderHelper.getStringRepresentation(Integer.valueOf(500)));
    assertEquals("666.666",
        VariablePlaceHolderHelper.getStringRepresentation(Double.valueOf(666.666d)));

    try {
      VariablePlaceHolderHelper.getStringRepresentation(new Date());
      fail("VariablePlaceHolderHelper.getStringRepresentation didn't throw when I expected it to");
    } catch (Exception expectedException) {
      assertThat(expectedException.getMessage()).startsWith("value class is not supported");
    }
  }

  @Test
  public void containsJockers() throws Exception {
    assertThat(VariablePlaceHolderHelper.containsJockers("srand")).isTrue();
    assertThat(VariablePlaceHolderHelper.containsJockers("irand")).isTrue();
    assertThat(VariablePlaceHolderHelper.containsJockers("drand")).isTrue();
    assertThat(VariablePlaceHolderHelper.containsJockers("tazmania")).isFalse();
  }

  @Test
  public void parseJocker() throws Exception {
    assertThat(VariablePlaceHolderHelper.parseJocker("srand").length()).isGreaterThan(16);
    assertThat(Integer.valueOf(VariablePlaceHolderHelper.parseJocker("irand")) instanceof Integer)
        .isTrue();
    assertThat(Double.valueOf(VariablePlaceHolderHelper.parseJocker("drand")) instanceof Double)
        .isTrue();
  }

}

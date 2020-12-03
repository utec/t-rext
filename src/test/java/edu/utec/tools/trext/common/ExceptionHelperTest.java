package edu.utec.tools.trext.common;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ExceptionHelperTest {

  @Before
  public void setup() {
    new ExceptionHelper();
  }

  @Test
  public void summarizeCausesAsString() throws Exception {
    try {
      throw new Exception("one",
          new Exception("two", new Exception("three", new Exception("four"))));
    } catch (Exception e) {
      assertEquals("[one,two,three,four]", ExceptionHelper.summarizeCausesAsString(e));
    }
  }

  @Test
  public void summarizeCausesAsArray() throws Exception {
    try {
      throw new Exception("one",
          new Exception("two", new Exception("three", new Exception("four"))));
    } catch (Exception e) {
      List<String> stack = ExceptionHelper.summarizeCausesAsArray(e);
      assertEquals(4, stack.size());
    }
  }

}

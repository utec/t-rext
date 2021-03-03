package edu.utec.tools.trext.common;

import static org.junit.Assert.assertEquals;
import java.util.LinkedHashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.utec.test.common.TestHelper;
import edu.utec.test.junit.DefaultOrderedRunner;
import edu.utec.test.junit.ExplicitOrder;

@RunWith(DefaultOrderedRunner.class)
@ExplicitOrder()
public class StringHelperTest {

  @Test
  public void getStringRepresentation() throws Exception {
    String json =
        TestHelper.getFileAsString("edu/utec/tools/trext/method/evaluateJsonExpression.txt");

    assertEquals("John", (String) StringHelper.evaluateJsonExpression("$.firstName", json));
    assertEquals(26, StringHelper.evaluateJsonExpression("$.age", json));
    assertEquals(LinkedHashMap.class,
        StringHelper.evaluateJsonExpression("$.address", json).getClass());
  }

  @Test
  public void toLowerCaseWithSnakeCase() throws Exception {
    assertEquals("scenario:_fetch_user_repositories",
        StringHelper.toLowerCaseWithSnakeCase("Scenario: Fetch user repositories"));
  }

  @Test
  public void getValueAfterKeyWithSpaces() throws Exception {
    assertEquals("acme.com", StringHelper.getValueAfterKeyWithSpaces("url  acme.com  "));
    assertEquals("put", StringHelper.getValueAfterKeyWithSpaces("method  put  "));
  }

  @Test
  public void getBooleanValueAfterKeyWithSpaces() throws Exception {
    assertEquals(true, StringHelper.getBooleanValueAfterKeyWithSpaces("disabled  true"));
    assertEquals(false, StringHelper.getBooleanValueAfterKeyWithSpaces("disabled  false  "));
    assertEquals(false, StringHelper.getBooleanValueAfterKeyWithSpaces(null));
  }


  @Test
  public void enhanceSpacesInQuotedString() throws Exception {
    assertEquals("assertThat 123 isEqualTo \"1@space2@space3\"",
        StringHelper.enhanceSpacesInQuotedString("assertThat 123 isEqualTo \"1 2 3\"", "@space"));

    assertEquals("assertThat flags isEqualTo \"true@spacefalse@space@space@spacetrue\"",
        StringHelper.enhanceSpacesInQuotedString("assertThat flags isEqualTo \"true false   true\"",
            "@space"));
  }

  @Test
  public void getPayloadFromQuotedString() throws Exception {
    assertEquals("TheLaughingMan", StringHelper.getPayloadFromQuotedString("\"TheLaughingMan\""));
    assertEquals("TheLaughing\"Man",
        StringHelper.getPayloadFromQuotedString("\"TheLaughing\"Man\""));
    assertEquals("The Laughing Man",
        StringHelper.getPayloadFromQuotedString("\"The Laughing Man\""));
  }

  @Test
  public void convertGeniuneValueToStringRepresentationSafeLong() throws Exception {
    assertEquals("1598885893000",
        StringHelper.convertGeniuneValueToStringRepresentationSafe(1598885893000l));
  }

}

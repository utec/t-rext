package edu.utec.tools.trext.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import edu.utec.test.common.TestHelper;
import edu.utec.test.junit.DefaultOrderedRunner;
import edu.utec.test.junit.ExplicitOrder;

@RunWith(DefaultOrderedRunner.class)
@ExplicitOrder()
public class RawLinesHelperTest {

  @Before
  public void setup() {
    new RawLinesHelper();
  }

  @Test
  public void getMultilineValueBySimpleAndUniqueFieldName() throws Exception {

    File file = TestHelper.getFile(
        "edu/utec/tools/trext/bdd/translator/getMultilineValueBySimpleAndUniqueFieldName.txt");

    ArrayList<String> lines = FileHelper.getFileAsLines(file);

    String regex = "^\\s*Scenario\\s*:\\s*.+";
    ArrayList<ArrayList<String>> rawScenarios =
        RawLinesHelper.getGroupLinesFromLineThatMeetsRegexAndEndsWithBlankLine(regex, lines);

    assertNotNull(rawScenarios);
    assertEquals(1, rawScenarios.size());

    String body = RawLinesHelper.getMultilineStringUsingUniqueFieldNameAndRegexBoundaries(
        rawScenarios.get(0), "body", "^```\\s*", "^```\\s*");

    // get expected body from another file because
    // is a big string

    File filetmp = TestHelper.getFile(
        "edu/utec/tools/trext/bdd/translator/getMultilineValueBySimpleAndUniqueFieldName_expected.txt");

    ArrayList<String> linesTmp = FileHelper.getFileAsLines(filetmp);
    String expectedBody = linesTmp.get(0);

    assertEquals(expectedBody, body);

  }

  @Test
  public void getRawScenarios() throws Exception {

    File file = TestHelper.getFile("edu/utec/tools/trext/bdd/translator/FeatureDemo.txt");

    ArrayList<String> lines = FileHelper.getFileAsLines(file);

    String regex = "^\\s*Scenario\\s*:\\s*.+";
    ArrayList<ArrayList<String>> rawScenarios =
        RawLinesHelper.getGroupLinesFromLineThatMeetsRegexAndEndsWithBlankLine(regex, lines);

    assertNotNull(rawScenarios);
    assertEquals(2, rawScenarios.size());
    assertEquals("url https://some-api.com/api/user", rawScenarios.get(0).get(1));
    assertEquals("equal status 200", rawScenarios.get(0).get(6));
    assertEquals("Scenario: Fetch user repositories", rawScenarios.get(1).get(0));
    assertEquals("equal $.content[0].id t-rext.git", rawScenarios.get(1).get(9));
  }

  @Test
  public void getUniqueRawLineWhichStartsWith() throws Exception {

    File file = TestHelper.getFile("edu/utec/tools/trext/bdd/translator/FeatureDemo.txt");

    ArrayList<String> lines = FileHelper.getFileAsLines(file);

    String regex = "^\\s*Scenario\\s*:\\s*.+";
    ArrayList<ArrayList<String>> rawScenarios =
        RawLinesHelper.getGroupLinesFromLineThatMeetsRegexAndEndsWithBlankLine(regex, lines);

    assertNotNull(rawScenarios);
    assertEquals(2, rawScenarios.size());

    String rawUrl =
        RawLinesHelper.getUniqueRawLineWhichStartsWith("url", rawScenarios.get(0), true);
    assertEquals("url https://some-api.com/api/user", rawUrl);

    rawUrl = RawLinesHelper.getUniqueRawLineWhichStartsWith("url", rawScenarios.get(1), true);
    assertEquals("url https://some-api.com/api/user/${fetchUser.content.username}/repository",
        rawUrl);
  }

  @Test
  public void getBody() throws Exception {

    File file = TestHelper.getFile(this, "getBody.txt");
    ArrayList<String> lines = FileHelper.getFileAsLines(file);

    String body = RawLinesHelper.getMultilineStringUsingUniqueFieldNameAndRegexBoundaries(lines,
        "body", "^```\\s*", "^```\\s*");
    DocumentContext parsedResponse = JsonPath.parse(body);
    assertEquals("aaaa", (String) parsedResponse.read("$.product"));
    assertEquals("bbb", (String) parsedResponse.read("$.queryType"));
    assertEquals("76821320", (String) parsedResponse.read("$.queryPayload"));
  }

  @Test
  public void getEmptyBody() throws Exception {

    File file = TestHelper.getFile(this, "getEmptyBody.txt");
    ArrayList<String> lines = FileHelper.getFileAsLines(file);

    String body = RawLinesHelper.getMultilineStringUsingUniqueFieldNameAndRegexBoundaries(lines,
        "body", "^```\\s*", "^```\\s*");
    assertEquals(0, body.length());
  }

  @Test
  public void getRawUrl() throws Exception {
    File file = TestHelper.getFile(this, "getRawUrl.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);
    String urlLine = RawLinesHelper.getUniqueRawLineWhichStartsWith("url", rawLines, true);
    assertEquals("url https://www.lipsum.com/", urlLine);
  }

  @Test
  public void getRawMethod() throws Exception {
    File file = TestHelper.getFile(this, "getRawMethod.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);

    String methodLine = RawLinesHelper.getUniqueRawLineWhichStartsWith("method", rawLines, true);
    assertEquals("method put", methodLine);
  }

  @Test
  public void getRawDisabled() throws Exception {
    File file = TestHelper.getFile(this, "getRawDisabled.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);

    String disabledLine =
        RawLinesHelper.getUniqueRawLineWhichStartsWith("disabled", rawLines, true);
    assertEquals("disabled true", disabledLine);
  }

  @Test
  public void getRawDisabledWhenNotExist() throws Exception {
    File file = TestHelper.getFile(this, "getRawDisabledWhenNotExist.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);

    String disabledLine =
        RawLinesHelper.getUniqueRawLineWhichStartsWith("disabled", rawLines, false);
    assertNull(disabledLine);
  }

  @Test
  public void getRawHeaders() throws Exception {
    File file = TestHelper.getFile(this, "getRawHeaders.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);

    ArrayList<String> multipleLines = RawLinesHelper.getRawLinesWichStartsWith("header", rawLines);
    assertEquals(2, multipleLines.size());
    assertEquals("header Content-Type = application/json", multipleLines.get(0));
    assertEquals("header token = UkaUkaUkaUka", multipleLines.get(1));
  }

  @Test
  public void getRawAsserts() throws Exception {
    File file = TestHelper.getFile(this, "getRawAsserts.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);

    String regex1 = "^\\s*asserts\\s*";
    String regex2 = "^\\s*assertThat\\s*.+";
    ArrayList<String> multipleLines =
        RawLinesHelper.getUniqueGroupLinesAfterLineThatMeetsRegex1AndNextLinesMeetsRegex2(regex1,
            regex2, rawLines, true);

    assertEquals(3, multipleLines.size());
    assertEquals("asserts", multipleLines.get(0));
    assertEquals("assertThat $.status isEqualTo 200", multipleLines.get(1));
    // this line has several spaces at the end of string
    assertEquals("assertThat $.message isEqualTo \"success\"", multipleLines.get(2));

  }

  @Test
  public void getRawContext() throws Exception {
    File file = TestHelper.getFile(this, "getRawContext.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);

    String regex1 = "^\\s*context\\s*";
    String regex2 = "^\\s*setVar\\s*.+";
    ArrayList<String> multipleLines =
        RawLinesHelper.getUniqueGroupLinesAfterLineThatMeetsRegex1AndNextLinesMeetsRegex2(regex1,
            regex2, rawLines, true);

    assertEquals(3, multipleLines.size());
    assertEquals("context", multipleLines.get(0));
    assertEquals("setVar \"personId\" $.personId", multipleLines.get(1));
    // this line has several spaces at the end of string
    assertEquals("setVar \"numDocument\" $.content.debts[0].document.number", multipleLines.get(2));

  }

  @Test
  public void getFeatureData() throws Exception {
    File file = TestHelper.getFile(this, "getFeatureData.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);
    ArrayList<String> data = RawLinesHelper.getValueFromKey("Feature", rawLines, true);
    assertEquals(2, data.size());
    assertEquals("Google Searching", data.get(0));
    assertEquals("As a web surfer, I want to search Google, so that I can learn new things.",
        data.get(1));

  }

  @Test
  public void getKeyValue() throws Exception {
    File file = TestHelper.getFile(this, "getKeyValue.txt");
    ArrayList<String> rawLines = FileHelper.getFileAsLines(file);
    HashMap<String, String> keyValuePairs = RawLinesHelper.getKeyValue(rawLines, "header", "=");
    assertEquals(4, keyValuePairs.size());
    assertEquals("application/json", keyValuePairs.get("Content-Type"));
    assertEquals("UkaUkaUkaUka", keyValuePairs.get("token"));
    assertEquals("123456789", keyValuePairs.get("apikey"));
    assertEquals("X-My-Custom-Header, X-Another-Custom-Header",
        keyValuePairs.get("Access-Control-Expose-Headers"));

  }

  @Test
  public void getGroupLinesAfterLineThatMeetsRegexAndEndsWithBlankLineNotFoundLines()
      throws Exception {

    ArrayList<String> lines = new ArrayList<String>();
    lines.add("foo");
    lines.add("");
    lines.add("");
    lines.add("");

    try {
      RawLinesHelper.getGroupLinesFromLineThatMeetsRegexAndEndsWithBlankLine("^foo", lines);
    } catch (Exception e) {
      assertEquals(true, e.getMessage().startsWith("no one line meets the end regex"));
    }
  }

  @Test
  public void getMultilineStringUsingUniqueFieldNameAndRegexBoundariesErrorNoUnique()
      throws Exception {

    ArrayList<String> lines = new ArrayList<String>();
    lines.add("ImUnique");
    lines.add("foo");
    lines.add("baz");
    lines.add("qux");
    lines.add("ImUnique");
    lines.add("quux");
    lines.add("bar");

    try {
      RawLinesHelper.getMultilineStringUsingUniqueFieldNameAndRegexBoundaries(lines, "ImUnique",
          "^foo", "^bar");
    } catch (Exception e) {
      assertEquals(true, e.getMessage().startsWith("more than one line contains field"));
    }
  }

  @Test
  public void getMultilineStringUsingUniqueFieldNameAndRegexBoundariesErrorNoStartRegex()
      throws Exception {

    ArrayList<String> lines = new ArrayList<String>();
    lines.add("ImUnique");
    lines.add("imnotfoo");
    lines.add("baz");
    lines.add("qux");
    lines.add("quux");
    lines.add("bar");

    try {
      RawLinesHelper.getMultilineStringUsingUniqueFieldNameAndRegexBoundaries(lines, "ImUnique",
          "^foo", "^bar");
    } catch (Exception e) {
      assertEquals(true, e.getMessage().startsWith(
          "Field [ImUnique] was found, but the next line does not meet the start regex"));
    }
  }

  @Test
  public void getMultilineStringUsingUniqueFieldNameAndRegexBoundariesErrorEmptyStart()
      throws Exception {

    ArrayList<String> lines = new ArrayList<String>();
    lines.add("ImUnique");
    lines.add(null);
    lines.add("baz");
    lines.add("qux");
    lines.add("quux");
    lines.add("bar");

    try {
      RawLinesHelper.getMultilineStringUsingUniqueFieldNameAndRegexBoundaries(lines, "ImUnique",
          "^foo", "^bar");
    } catch (Exception e) {
      assertEquals(true, e.getMessage().startsWith("null lines are not allowed"));
    }
  }

  @Test
  public void getMultilineStringUsingUniqueFieldNameAndRegexBoundariesErrorNotEnoughLines()
      throws Exception {

    ArrayList<String> lines = new ArrayList<String>();
    lines.add("ImUnique");
    lines.add("foo");
    lines.add("baz");

    try {
      RawLinesHelper.getMultilineStringUsingUniqueFieldNameAndRegexBoundaries(lines, "ImUnique",
          "^foo", "^bar");
    } catch (Exception e) {
      assertEquals(true, e.getMessage().contains("there are not enough lines"));
    }
  }

}

package edu.utec.tools.trext.bdd.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.utec.test.common.TestHelper;
import edu.utec.test.junit.DefaultOrderedRunner;
import edu.utec.test.junit.ExplicitOrder;
import edu.utec.tools.trext.common.FileHelper;
import edu.utec.tools.trext.common.RawLinesHelper;
import edu.utec.tools.trext.model.Feature;

@RunWith(DefaultOrderedRunner.class)
@ExplicitOrder({"getFeatureNameAndDescription", "getScenarioMinimalFields",
    "getScenarioOptionalHeaderFields", "getScenarioOptionalAssertFields",
    "getFixedValueFromRawLine", "getRawLinesFromInitialString", "getRawAsserts", "convertNameToId"})
public class FeatureTranslatorTest {

  @Test
  public void getFeatureNameAndDescription() throws Exception {

    File file =
        TestHelper.getFile("edu/utec/tools/trext/bdd/translator/getFeatureNameAndDescription.txt");

    FeatureTranslator featureTranslator = new FeatureTranslator();
    Feature feature = featureTranslator.parse(file);
    assertNotNull(feature);
    assertEquals("Google Searching", feature.getName());
    assertEquals("As a web surfer, I want to search Google, so that I can learn new things.",
        feature.getDescription());
  }

  @Test
  public void getScenarioMinimalFields() throws Exception {

    File file =
        TestHelper.getFile("edu/utec/tools/trext/bdd/translator/getScenariosMinimalFields.txt");

    FeatureTranslator featureTranslator = new FeatureTranslator();
    Feature feature = featureTranslator.parse(file);

    assertNotNull(feature);
    assertNotNull(feature.getScenarios());
    assertEquals(2, feature.getScenarios().size());

    assertEquals("Fetch user", feature.getScenarios().get(0).getName());
    assertEquals("https://some-api.com/api/user", feature.getScenarios().get(0).getUrl());
    assertEquals("get", feature.getScenarios().get(0).getMethod());
    assertEquals("fetch_user", feature.getScenarios().get(0).getId());

    assertEquals("Fetch user repositories", feature.getScenarios().get(1).getName());
    assertEquals("https://some-api.com/api/user/${fetchUser.content.username}/repository",
        feature.getScenarios().get(1).getUrl());
    assertEquals("post", feature.getScenarios().get(1).getMethod());
    assertEquals("fetch_user_repositories", feature.getScenarios().get(1).getId());
  }

  @Test
  public void getScenarioOptionalHeaderFields() throws Exception {

    File file = TestHelper
        .getFile("edu/utec/tools/trext/bdd/translator/getScenarioOptionalHeaderFields.txt");

    FeatureTranslator featureTranslator = new FeatureTranslator();
    Feature feature = featureTranslator.parse(file);
    assertNotNull(feature);
    assertNotNull(feature.getScenarios());
    assertEquals(2, feature.getScenarios().size());

    HashMap<String, String> headers = feature.getScenarios().get(0).getHeaders();
    assertEquals(2, headers.size());
    assertEquals("123456789", headers.get("apiKey"));
    assertEquals("v1", headers.get("h1"));

    HashMap<String, String> headers2 = feature.getScenarios().get(1).getHeaders();
    assertEquals(3, headers2.size());
    assertEquals("123456789", headers2.get("apiKey"));
    assertEquals("v2", headers2.get("h2"));
    assertEquals("application/json", headers2.get("content-type"));
  }

  @Test
  public void getScenarioOptionalAssertFields() throws Exception {

    File file = TestHelper
        .getFile("edu/utec/tools/trext/bdd/translator/getScenarioOptionalAssertFields.txt");

    FeatureTranslator featureTranslator = new FeatureTranslator();
    Feature feature = featureTranslator.parse(file);
    assertNotNull(feature);
    assertNotNull(feature.getScenarios());
    assertEquals(2, feature.getScenarios().size());

    ArrayList<String> rawAsserts = feature.getScenarios().get(0).getRawAsserts();
    assertEquals(4, rawAsserts.size());
    assertEquals("assertThat status 200", rawAsserts.get(1));
    assertEquals("assertThat $.content.username JRichardsz", rawAsserts.get(2));
    assertEquals("assertThat $.content.mail jrichardsz.java@gmail.com", rawAsserts.get(3));

    ArrayList<String> rawAsserts2 = feature.getScenarios().get(1).getRawAsserts();
    assertEquals(3, rawAsserts2.size());
    assertEquals("assertThat status 200", rawAsserts2.get(1));
    assertEquals("assertThat $.content[0].id t-rext.git", rawAsserts2.get(2));
  }

  @Test
  public void getRawLinesFromInitialString() throws Exception {

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(
        classLoader.getResource("edu/utec/tools/trext/bdd/translator/FeatureDemo.txt").getFile());

    ArrayList<String> lines = FileHelper.getFileAsLines(file);

    String regex = "^\\s*Scenario\\s*:\\s*.+";
    ArrayList<ArrayList<String>> rawScenarios =
        RawLinesHelper.getGroupLinesFromLineThatMeetsRegexAndEndsWithBlankLine(regex, lines);

    assertNotNull(rawScenarios);
    assertEquals(2, rawScenarios.size());

  }
}

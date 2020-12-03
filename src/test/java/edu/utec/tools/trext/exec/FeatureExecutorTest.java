package edu.utec.tools.trext.exec;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.utec.test.common.TestResourceHelper;
import edu.utec.test.junit.DefaultOrderedRunner;
import edu.utec.test.junit.ExplicitOrder;

@RunWith(DefaultOrderedRunner.class)
@ExplicitOrder({"oneScenario", "twoScenarios", "twoScenariosPropagateVariable",
    "oneScenarioInvalidAssert", "onePostScenario", "getAndPostWithVariablePropagation"})
public class FeatureExecutorTest {

  private final Logger logger = LogManager.getLogger(this.getClass());
  private Server server;

  @Before
  public void startJetty() throws Exception {
    // Create Server
    server = new Server(8080);
    ServletContextHandler context = new ServletContextHandler();

    ServletHolder servlet1 = new ServletHolder("UserEndpoint", UserEndpoint.class);
    servlet1.setInitParameter("resourceBase", System.getProperty("user.dir"));
    servlet1.setInitParameter("dirAllowed", "true");
    context.addServlet(servlet1, "/user");

    ServletHolder servlet2 =
        new ServletHolder("NetflixPreferencesEndpoint", NetflixPreferencesEndpoint.class);
    servlet2.setInitParameter("resourceBase", System.getProperty("user.dir"));
    servlet2.setInitParameter("dirAllowed", "true");
    context.addServlet(servlet2, "/netflix/preferences");

    ServletHolder servlet3 =
        new ServletHolder("NetflixPreferencesV2Endpoint", NetflixPreferencesV2Endpoint.class);
    servlet3.setInitParameter("resourceBase", System.getProperty("user.dir"));
    servlet3.setInitParameter("dirAllowed", "true");
    context.addServlet(servlet3, "/v2/netflix/preferences");


    ServletHolder servlet4 = new ServletHolder("PostEndpoint", PostEndpoint.class);
    servlet4.setInitParameter("resourceBase", System.getProperty("user.dir"));
    servlet4.setInitParameter("dirAllowed", "true");
    context.addServlet(servlet4, "/simple/post");


    server.setHandler(context);
    // Start Server
    server.start();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void oneScenario() throws Exception {

    File file = TestResourceHelper.getFile("edu/utec/tools/trext/executor/oneGetScenario.txt");
    FeatureExecutor executor = new FeatureExecutor();
    HashMap<String, Object> report = executor.singleSafeExecute(file, new HashMap<String, Object>());

    assertEquals(1, report.get("passed"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("http"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("asserts"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void twoScenarios() throws Exception {

    File file = TestResourceHelper.getFile("edu/utec/tools/trext/executor/twoGetScenarios.txt");
    FeatureExecutor executor = new FeatureExecutor();
    HashMap<String, Object> report = executor.singleSafeExecute(file, new HashMap<String, Object>());

    assertEquals(2, report.get("passed"));

    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("http"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("asserts"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(1).get("http"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(1).get("asserts"));

  }

  @SuppressWarnings("unchecked")
  @Test
  public void twoScenariosPropagateVariable() throws Exception {

    File file = TestResourceHelper
        .getFile("edu/utec/tools/trext/executor/twoGetScenariosPropagateVariable.txt");
    FeatureExecutor executor = new FeatureExecutor();
    HashMap<String, Object> report = executor.singleSafeExecute(file, new HashMap<String, Object>());

    assertEquals(2, report.get("passed"));

    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("http"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("asserts"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(1).get("http"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(1).get("asserts"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void oneScenarioInvalidAssert() throws Exception {

    File file =
        TestResourceHelper.getFile("edu/utec/tools/trext/executor/oneGetScenarioInvalidAssert.txt");
    FeatureExecutor executor = new FeatureExecutor();
    HashMap<String, Object> report = executor.singleSafeExecute(file, new HashMap<String, Object>());

    ObjectMapper objectMapper = new ObjectMapper();
    System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));

    assertEquals(0, report.get("passed"));
    assertEquals(1, report.get("failed"));

    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("http"));
    assertEquals("failed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("asserts"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void onePostScenario() throws Exception {

    File file = TestResourceHelper.getFile("edu/utec/tools/trext/executor/onePostScenario.txt");
    FeatureExecutor executor = new FeatureExecutor();
    HashMap<String, Object> report = executor.singleSafeExecute(file, new HashMap<String, Object>());

    assertEquals(1, report.get("passed"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("http"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("asserts"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getAndPostWithVariablePropagation() throws Exception {

    File file = TestResourceHelper.getFile("edu/utec/tools/trext/executor/getAndPostScenario.txt");
    FeatureExecutor executor = new FeatureExecutor();
    HashMap<String, Object> report = executor.singleSafeExecute(file, new HashMap<String, Object>());

    assertEquals(2, report.get("passed"));

    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("http"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(0).get("asserts"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(1).get("http"));
    assertEquals("passed",
        ((List<HashMap<String, Object>>) report.get("scenarioStats")).get(1).get("asserts"));
  }

  @After
  public void stopJetty() {
    try {
      server.stop();
    } catch (Exception e) {
      logger.error("Fail to stop Jetty", e);
    }
  }

}

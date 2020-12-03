package edu.utec.tools.trext.http;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import edu.utec.test.junit.DefaultOrderedRunner;
import edu.utec.test.junit.ExplicitOrder;

@RunWith(DefaultOrderedRunner.class)
@ExplicitOrder({"simpleGet", "simplePost", "simplePut", "simpleDelete"})
public class SmartHttpClientTest {

  private final Logger logger = LogManager.getLogger(this.getClass());
  private Server server;

  @Before
  public void startJetty() throws Exception {

    System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
    System.setProperty("org.eclipse.jetty.LEVEL", "OFF");

    // Create Server
    server = new Server(8080);
    ServletContextHandler context = new ServletContextHandler();

    ServletHolder defaultServ = new ServletHolder("default", SimpleEndpoints.class);
    defaultServ.setInitParameter("resourceBase", System.getProperty("user.dir"));
    defaultServ.setInitParameter("dirAllowed", "true");
    context.addServlet(defaultServ, "/resource");

    server.setHandler(context);
    // Start Server
    server.start();
  }

  @Test
  public void simpleGet() throws Exception {

    SmartHttpClient httpClient = new SmartHttpClient();
    HashMap<String, Object> response =
        httpClient.performRequest("get", "http://localhost:8080/resource", null, null);

    DocumentContext parsedResponse = JsonPath.parse((String) response.get("body"));
    assertEquals("get", (String) parsedResponse.read("$.sourceMethod"));
    assertEquals("Jane", (String) parsedResponse.read("$.firstName"));
    assertEquals("Doe", (String) parsedResponse.read("$.lastName"));
    assertEquals("FBI-Agent", (String) parsedResponse.read("$.job"));

  }

  @Test
  public void simplePost() throws Exception {

    SmartHttpClient httpClient = new SmartHttpClient();
    HashMap<String, Object> response =
        httpClient.performRequest("post", "http://localhost:8080/resource", null, null);

    DocumentContext parsedResponse = JsonPath.parse((String) response.get("body"));
    assertEquals("post", (String) parsedResponse.read("$.sourceMethod"));
    assertEquals("created", (String) parsedResponse.read("$.status"));

  }

  @Test
  public void simplePut() throws Exception {

    SmartHttpClient httpClient = new SmartHttpClient();
    HashMap<String, Object> response =
        httpClient.performRequest("put", "http://localhost:8080/resource", null, null);

    DocumentContext parsedResponse = JsonPath.parse((String) response.get("body"));
    assertEquals("put", (String) parsedResponse.read("$.sourceMethod"));
    assertEquals("updated", (String) parsedResponse.read("$.status"));

  }

  @Test
  public void simpleDelete() throws Exception {

    SmartHttpClient httpClient = new SmartHttpClient();
    HashMap<String, Object> response =
        httpClient.performRequest("delete", "http://localhost:8080/resource", null, null);

    DocumentContext parsedResponse = JsonPath.parse((String) response.get("body"));
    assertEquals("delete", (String) parsedResponse.read("$.sourceMethod"));
    assertEquals("deleted", (String) parsedResponse.read("$.status"));

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

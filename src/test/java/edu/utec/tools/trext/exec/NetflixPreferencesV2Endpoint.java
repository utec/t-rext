package edu.utec.tools.trext.exec;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;

public class NetflixPreferencesV2Endpoint extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String rawParameter = request.getParameter("id");

    if (!rawParameter.contentEquals("77777")) {
      response.setHeader("content-type", "application/json");
      ObjectMapper mapper = new ObjectMapper();
      HashMap<String, Object> responseBean = new HashMap<String, Object>();
      responseBean.put("sourceMethod", "get");
      responseBean.put("status", "error");
      response.getOutputStream().print(mapper.writeValueAsString(responseBean));
      return;
    }

    response.setHeader("content-type", "application/json");
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> responseBean = new HashMap<String, Object>();
    responseBean.put("sourceMethod", "get");
    responseBean.put("series", "The100");
    responseBean.put("movies", "TimeMachine");
    response.getOutputStream().print(mapper.writeValueAsString(responseBean));
  }

}

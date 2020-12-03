package edu.utec.tools.trext.exec;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class PostEndpoint extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

    DocumentContext doc = JsonPath.parse(body);

    String firstName = doc.read("$.firstName");

    if (firstName == null || !firstName.contentEquals("John")) {
      response.setHeader("content-type", "application/json");
      ObjectMapper mapper = new ObjectMapper();
      HashMap<String, Object> responseBean = new HashMap<String, Object>();
      responseBean.put("sourceMethod", "post");
      responseBean.put("status", "error");
      response.getOutputStream().print(mapper.writeValueAsString(responseBean));
      return;
    }

    int id = doc.read("$.id");

    if (id != 77777) {
      response.setHeader("content-type", "application/json");
      ObjectMapper mapper = new ObjectMapper();
      HashMap<String, Object> responseBean = new HashMap<String, Object>();
      responseBean.put("sourceMethod", "post");
      responseBean.put("status", "error");
      response.getOutputStream().print(mapper.writeValueAsString(responseBean));
      return;
    }


    response.setHeader("content-type", "application/json");
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> responseBean = new HashMap<String, Object>();
    responseBean.put("sourceMethod", "post");
    responseBean.put("message", "created");
    response.getOutputStream().print(mapper.writeValueAsString(responseBean));
  }

}

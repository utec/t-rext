package edu.utec.tools.trext.http;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;

public class SimpleEndpoints extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setHeader("content-type", "application/json");
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> responseBean = new HashMap<String, Object>();
    responseBean.put("sourceMethod", "get");
    responseBean.put("firstName", "Jane");
    responseBean.put("lastName", "Doe");
    responseBean.put("job", "FBI-Agent");
    response.getOutputStream().print(mapper.writeValueAsString(responseBean));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setHeader("content-type", "application/json");
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> responseBean = new HashMap<String, Object>();
    responseBean.put("sourceMethod", "post");
    responseBean.put("status", "created");
    response.getOutputStream().print(mapper.writeValueAsString(responseBean));
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setHeader("content-type", "application/json");
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> responseBean = new HashMap<String, Object>();
    responseBean.put("sourceMethod", "delete");
    responseBean.put("status", "deleted");
    response.getOutputStream().print(mapper.writeValueAsString(responseBean));
  }

  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setHeader("content-type", "application/json");
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> responseBean = new HashMap<String, Object>();
    responseBean.put("sourceMethod", "put");
    responseBean.put("status", "updated");
    response.getOutputStream().print(mapper.writeValueAsString(responseBean));
  }


}

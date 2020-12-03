package edu.utec.tools.trext.exec;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;

public class UserEndpoint extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setHeader("content-type", "application/json");
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String, Object> responseBean = new HashMap<String, Object>();
    responseBean.put("sourceMethod", "get");
    responseBean.put("firstName", "John");
    responseBean.put("lastName", "McClane");
    responseBean.put("job", "Terrorist-Murder");
    responseBean.put("personId", 77777);
    response.getOutputStream().print(mapper.writeValueAsString(responseBean));
  }

}

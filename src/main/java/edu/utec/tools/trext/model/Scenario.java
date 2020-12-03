package edu.utec.tools.trext.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Scenario {

  private String id;
  private String name;
  private String url;
  private String method;
  private String body;
  private boolean disabled;
  private HashMap<String, String> headers;
  private ArrayList<String> rawAsserts;
  private ArrayList<String> rawContext;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(HashMap<String, String> headers) {
    this.headers = headers;
  }

  public ArrayList<String> getRawAsserts() {
    return rawAsserts;
  }

  public void setRawAsserts(ArrayList<String> rawAsserts) {
    this.rawAsserts = rawAsserts;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public ArrayList<String> getRawContext() {
    return rawContext;
  }

  public void setRawContext(ArrayList<String> rawContext) {
    this.rawContext = rawContext;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

}

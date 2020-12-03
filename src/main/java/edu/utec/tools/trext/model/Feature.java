package edu.utec.tools.trext.model;

import java.util.ArrayList;

public class Feature {

  private String name;
  private String description;
  private ArrayList<Scenario> scenarios;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ArrayList<Scenario> getScenarios() {
    return scenarios;
  }

  public void setScenarios(ArrayList<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

}

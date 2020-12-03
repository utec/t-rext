package edu.utec.tools.trext.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.ComparisonFailure;
import edu.utec.tools.trext.common.FileHelper;
import edu.utec.tools.trext.method.MethodEnhancer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class DynamicLogic {

  private final Logger logger = LogManager.getLogger(this.getClass());

  private MethodEnhancer methodEnhancer = new MethodEnhancer();

  public void perform(ArrayList<String> rawAsserts, HashMap<String, Object> globalVariables, HashMap<String, Object> requestVariables,
      HashMap<String, Object> responseVariables) throws Exception {

    String body = (String) responseVariables.get("body");
    
    //merge all varaibles to make them available in script
    HashMap<String, Object> variables = new HashMap<String, Object>();
    if(globalVariables!=null) {
      variables.putAll(globalVariables);  
    }

    if(requestVariables!=null) {
      variables.putAll(requestVariables);
    }
    
    if(responseVariables!=null) {
      variables.putAll(responseVariables);
    }

    StringBuilder headerScript = new StringBuilder();
    headerScript.append("\n" + FileHelper.getFileAsStringFromClasspath("/imports.txt"));
    headerScript.append("\n" + FileHelper.getFileAsStringFromClasspath("/default_functions.txt"));
    String headerStringScript = headerScript.toString();

    Binding binding = new Binding();

    for (Entry<String, Object> entry : variables.entrySet()) {
      binding.setVariable(entry.getKey(), entry.getValue());
    }

    for (int a = 1; a < rawAsserts.size(); a++) {
      String rawAssert = rawAsserts.get(a);
      String assertAsScript = methodEnhancer
          .rawStringToConsecutiveMethodsWithSingleArgument(rawAssert, variables, body);

      try {
        executeOneLogic(headerStringScript, assertAsScript, binding);
      } catch (Exception e) {
        throw new Exception("Error detected in assert: " + rawAssert, e);
      }
    }
  }

  private void executeOneLogic(String header, String logic, Binding binding) throws Exception {
    try {
      logger.debug("enhanced raw assert:" + logic);
      GroovyShell shell = new GroovyShell(binding);
      shell.evaluate(header+"\n\n"+logic);
    } catch (ComparisonFailure e) {
      throw new Exception("Assert failed.", e);
    } catch (AssertionError e) {
      throw new Exception("Assert failed.", e);
    } catch (Exception e) {
      throw new Exception("Assert internal error.", e);
    }
  }

}

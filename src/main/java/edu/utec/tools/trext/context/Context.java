package edu.utec.tools.trext.context;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.FileHelper;
import edu.utec.tools.trext.method.MethodEnhancer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class Context {

  private final Logger logger = LogManager.getLogger(this.getClass());
  private MethodEnhancer methodEnhancer = new MethodEnhancer();

  public void evaluate(ArrayList<String> rawContextLines,
      HashMap<String, Object> localScenarioVariables, HashMap<String, Object> globalVariables)
      throws Exception {

    logger.debug("context evaluation is starting...");
    
    if (rawContextLines == null || rawContextLines.isEmpty()) {
      logger.debug("context is missing. Nothing will be evaluated.");
      return;
    }

    String body = (String) localScenarioVariables.get("body");

    StringBuilder builder = new StringBuilder();
    builder.append("\n" + FileHelper.getFileAsStringFromClasspath("/imports.txt"));
    builder.append("\n" + FileHelper.getFileAsStringFromClasspath("/default_functions.txt"));

    for (int a = 1; a < rawContextLines.size(); a++) {
      String rawContext = rawContextLines.get(a);
      logger.debug("raw context: "+rawContext);
      String parsedContext = methodEnhancer.rawStringToOneMethodWithSeveralArguments(rawContext,
          localScenarioVariables, body);
      logger.debug("parsed context: "+parsedContext);
      builder.append("\n" + parsedContext);
    }

    Binding binding = new Binding();
    binding.setVariable("globalVariables", globalVariables);

    logger.debug(builder.toString());

    GroovyShell shell = new GroovyShell(binding);
    shell.evaluate(builder.toString());

  }
}

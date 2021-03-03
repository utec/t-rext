package edu.utec.tools.trext.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.utec.test.common.TestHelper;
import edu.utec.test.junit.DefaultOrderedRunner;
import edu.utec.test.junit.ExplicitOrder;
import edu.utec.tools.trext.common.FileHelper;
import edu.utec.tools.trext.common.LoggerHelper;

@RunWith(DefaultOrderedRunner.class)
@ExplicitOrder({"perform"})
public class DynamicLogicTest {

  // TODO: get asserts from jsonSample and replicate them in a proper test
  @Test
  public void perform() throws Exception {
    
    LoggerHelper.setDebugLevel();

    String json = TestHelper.getFileAsString("edu/utec/tools/trext/logic/jsonSample.txt");

    File file = TestHelper.getFile("edu/utec/tools/trext/logic/asserts.txt");

    ArrayList<String> rawAsserts = FileHelper.getFileAsLines(file);

    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("lastNameFromCsv", "doe");
    variables.put("ageFromCsv", "26");
    variables.put("isAdmin", "false");

    HashMap<String, Object> responseVariables = new HashMap<String, Object>();
    responseVariables.put("body", json);

    DynamicLogic dynamicLogic = new DynamicLogic();
    dynamicLogic.perform(rawAsserts, variables, null, responseVariables);
  }
}

package edu.utec.tools.trext.common;

import org.junit.Before;
import org.junit.Test;

public class LoggerHelperTest {

  @Before
  public void setup() {
    new LoggerHelper();
  }

  @Test
  public void elapsedMillisToHumanExpression() throws Exception {
    // @TODO: main idea: get access to the logs messages and ensure that
    // if debug is enabled, debug messages are created
    // https://stackoverflow.com/questions/26533121/log4j-how-to-get-the-last-inserted-log-message
    // https://stackoverflow.com/questions/1827677/how-to-do-a-junit-assert-on-a-message-in-a-logger
    // https://www.baeldung.com/junit-asserting-logs
    LoggerHelper.setDebugLevel();
  }

}

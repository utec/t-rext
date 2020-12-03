package edu.utec.tools.trext.common;

public class TimeHelper {

  public static String elapsedMillisToHumanExpression(long start, long end) {

    int allSeconds = (int) ((end - start) / 1000);
    int minuts = (int) allSeconds / 60;
    int seconds = allSeconds % 60;
    int millis = (int) (end - start) - (seconds) * 1000;

    return String.format("%s min %s seg %s millis", minuts, seconds, millis);
  }
}

package edu.utec.tools.trext.common;

import java.util.ArrayList;

public class ExceptionHelper {

  public static String summarizeCausesAsString(Throwable throwable) {
    StringBuilder builder = new StringBuilder();
    while (throwable != null) {
      builder.append(throwable.getMessage());
      throwable = throwable.getCause();
      if (throwable != null) {
        builder.append(",");
      }
    }
    return "[" + builder.toString() + "]";
  }

  public static ArrayList<String> summarizeCausesAsArray(Throwable throwable) {
    ArrayList<String> stack = new ArrayList<String>();
    while (throwable != null) {
      stack.add(throwable.getMessage());
      throwable = throwable.getCause();
    }
    return stack;
  }

}

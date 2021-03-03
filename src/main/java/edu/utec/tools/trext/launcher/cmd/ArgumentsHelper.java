package edu.utec.tools.trext.launcher.cmd;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;

public class ArgumentsHelper {

  public CommandLine getArguments(String args[]) {
    Options options = new Options();

    Option o1 =
        new Option("m", "mode", true, "single feature, multiple features or smart detection");
    o1.setRequired(true);
    options.addOption(o1);

    Option o2 =
        new Option("ff", "feature_file_location", true, "absolute location of feature file");
    o2.setRequired(false);
    options.addOption(o2);

    Option o3 = new Option("fdl", "features_directory_location", true,
        "absolute location of directory with multiple features");
    o3.setRequired(false);
    options.addOption(o3);

    Option o4 = new Option("rdl", "report_directory_location", true,
        "absolute location in which report will be created");
    o4.setRequired(false);
    options.addOption(o4);

    Option o5 = new Option("rt", "report_type", true, "deafult or html");
    o5.setRequired(false);
    options.addOption(o5);

    Option o6 = new Option("variables", "variables_file_location", true,
        "absolute location of variables file");
    o6.setRequired(false);
    options.addOption(o6);

    Option o7 = new Option("debug", "debug", false, "run application with a lot of log");
    o7.setRequired(false);
    options.addOption(o7);
    
    Option o8 = new Option("d", "directory", true, "directory to be scanned");
    o8.setRequired(false);
    options.addOption(o8);

    Option o9 = new Option("ex", "exclude_file_names", true, "java regex to exclude file(name) from feature files");
    o9.setRequired(false);
    options.addOption(o9);

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;

    try {
      cmd = parser.parse(options, args);
      return cmd;
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      formatter.printHelp("t-rext", options);
      System.exit(1);
      return null;
    }
  }

  public String simplePrint(CommandLine cmd)
      throws JsonGenerationException, JsonMappingException, IOException {

    List<String> exclusions = Arrays.asList("org.apache.commons.cli.Option.getId",
        "org.apache.commons.cli.Option.getArgName", "org.apache.commons.cli.Option.required",
        "org.apache.commons.cli.Option.type", "org.apache.commons.cli.Option.values",
        "org.apache.commons.cli.Option.getValuesList",
        "org.apache.commons.cli.Option.getValueSeparator", "org.apache.commons.cli.Option.getArgs");

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
      @Override
      public boolean hasIgnoreMarker(final AnnotatedMember m) {
        String absoluteName =
            String.format("%s.%s", m.getDeclaringClass().getCanonicalName(), m.getName());
        return exclusions.contains(absoluteName) || super.hasIgnoreMarker(m);
      }
    });

    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cmd.getOptions());
  }
}

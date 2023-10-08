package om.tools.mappolycheck;

public class Log
{
  public class ConsoleColors
  {
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE
  }

  public static void info(String message)
  {
    logMessage(ConsoleColors.WHITE, "INFO ", message);
  }

  public static void data(String message)
  {
    logMessage(ConsoleColors.GREEN, "DATA ", message);
  }

  public static void debug(String message)
  {
    logMessage(ConsoleColors.GREEN, "DEBUG", message);
  }

  public static void warning(String message)
  {
    logMessage(ConsoleColors.YELLOW, "WARN ", message + " !!!!!");
  }

  public static void error(String message)
  {
    logMessage(ConsoleColors.RED, "ERROR", message + " !!!!!");
  }

  private static void logMessage(String color, String level, String message)
  {
    System.out.println(color + "[" + level + "] " + message + ConsoleColors.RESET);
  }
}
package me.dwtj.util;

public class Log
{
    public static final String DEBUG_PREFIX = "--- ";
    public static final String INFO_PREFIX = "~~~ ";
    public static final String WARN_PREFIX = ">>> ";
    public static final String ERROR_PREFIX = "!!! ";

    public static void debug(String str) {
        System.out.println(DEBUG_PREFIX + str);
    }

    public static void info(String str) {
        System.out.println(INFO_PREFIX + str);
    }

    public static void warn(String str) {
        System.out.println(WARN_PREFIX + str);
    }

    public static void error(String str) {
        System.out.println(ERROR_PREFIX + str);
    }
}

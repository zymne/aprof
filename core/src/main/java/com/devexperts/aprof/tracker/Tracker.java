package com.devexperts.aprof.tracker;

import com.devexperts.aprof.Configuration;

import java.util.Arrays;

public class Tracker {

    private static int TRACKING_SIZE_INTERVAL = 50;

    public static Configurator init(Configuration config) {
        return new Configurator(config);
    }

    public static void checkSizeBeforeAllocation(int size) {
        if(size > 0 && (size % TRACKING_SIZE_INTERVAL) == 0) {
            Thread currentThread = Thread.currentThread();
            StackTraceElement[] s = currentThread.getStackTrace();
            //String alarmMsg = formatAlarmMessage();
            System.err.println(String.format("Thread name: %s, Current size: %s", currentThread.getName(), size));
            System.err.println(formatStackTrace(s));
        }
    }

    public static void checkSizeThreshold(int size) {
        if(size > TRACKING_SIZE_INTERVAL) {
            Thread currentThread = Thread.currentThread();
            StackTraceElement[] s = currentThread.getStackTrace();
            System.err.println(String.format("Thread name: %s, Current size: %s", currentThread.getName(), size));
            System.err.println(formatStackTrace(s));
        }
    }

//    private static String formatAlarmMessage(String threadName, int size, StackTraceElement[] st) {
//
//    }

    private static String formatStackTrace(StackTraceElement[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(2 + array.length * 5);
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append("\r\n\t");
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static class Configurator {

        private Configurator(Configuration config) {
            if (config == null)
                throw new IllegalArgumentException("Aprof arguments must be specified");
            //replace with configuration parameter
            TRACKING_SIZE_INTERVAL = config.getInterval();
        }

        private void updateInterval(int value) {
            TRACKING_SIZE_INTERVAL = value;
        }

        private boolean handleOp(String opName, String param) {
            if(opName != null && opName.equals("INTERVAL")) {
                try {
                    int value = Integer.valueOf(param);
                    updateInterval(value);
                    return true;
                }
                catch(NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        public boolean process(String cmd) {
            String[] op = cmd.split(",");
            if(op != null && op.length == 2)
                return handleOp(op[0], op[1]);

            return false;
        }
    }
}

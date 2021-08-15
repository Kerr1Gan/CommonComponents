package com.common.utils;

import android.util.Log;

public class StackUtils {

    public static String getStack(ClassLoader classLoader) {
        try {
            Throwable throwable = (Throwable) classLoader.loadClass("java.lang.Throwable").newInstance();
            return Log.getStackTraceString(throwable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getThreadTrack() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();

        StringBuilder sbf = new StringBuilder();
        for (StackTraceElement e : st) {
            sbf.append(e.toString());
            sbf.append("\n");
            /*if (sbf.length() > 0) {
                sbf.append(" <- ");
                sbf.append(System.getProperty("line.separator"));
            }
            sbf.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , e.getClassName()
                    , e.getMethodName()
                    , e.getLineNumber()));*/
        }
        return sbf.toString();
    }

}
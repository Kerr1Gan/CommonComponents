package com.common.utils;

import android.util.Log;

import com.common.componentes.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PingNet {
    private static final String TAG = "PingNet";

    public static String ping(String ip) throws InterruptedException {
        String line;
        Process process = null;
        BufferedReader successReader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String pingTime = null;
        //ping -c 次数 -w 超时时间（s） ip
        String command = "ping -c " + 3 + " -w " + 3 + " " + ip;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "ping fail:process is null.");
                }
                append(resultBuffer, "ping fail:process is null.");
                return null;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int count = 0;
            BigDecimal sum = new BigDecimal(0);
            while ((line = successReader.readLine()) != null) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, line);
                }
                append(resultBuffer, line);
                BigDecimal time = getTime(line);
                if (time != null) {
                    sum = sum.add(time);
                    count++;
                }
            }
            //时间取平均值，四舍五入保留两位小数
            if (count > 0)
                pingTime = (sum.divide(new BigDecimal(count), 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString() + " ms");
            else
                pingTime = null;
            int status = process.waitFor();
            if (status == 0) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "exec cmd success:" + command);
                }
                append(resultBuffer, "exec cmd success:" + command);
            } else {
                append(resultBuffer, "exec cmd fail.");
            }
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "exec finished.");
            }
            append(resultBuffer, "exec finished.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "ping exit.");
            }
            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException ignore) {
                }
            }
        }
        if (BuildConfig.DEBUG) {
            Log.e(TAG, resultBuffer.toString());
        }
        return pingTime;
    }

    public static String tcping(String url, int port, int timeout) {
        long time = -1L;
        for (int i = 0; i < 2; i++) {
            long one = socketConnectTime(url, port, timeout);
            if (one < 0) {
                return null;
            }
            if (time == -1L || one < time) {
                time = one;
            }
        }
        return time + " ms";
    }

    static long socketConnectTime(String url, int port, int timeout) {
        try {
            long start = System.currentTimeMillis();
            Socket socket = new Socket();
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(url, port), timeout);
            long time = System.currentTimeMillis() - start;
            socket.close();
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
        }
    }

    /**
     * 获取ping接口耗时
     *
     * @param line
     * @return BigDecimal避免float、double精准度问题
     */
    private static BigDecimal getTime(String line) {
        String[] lines = line.split("\n");
        String time = null;
        for (String l : lines) {
            if (!l.contains("time="))
                continue;
            int index = l.indexOf("time=");
            time = l.substring(index + "time=".length());
            index = time.indexOf("ms");
            time = time.substring(0, index);
            Log.e(TAG, time);
        }
        return time == null ? null : new BigDecimal(time.trim());
    }
}
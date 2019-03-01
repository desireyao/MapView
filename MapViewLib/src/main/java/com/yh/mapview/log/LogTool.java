package com.yh.mapview.log;

import android.util.Log;

import com.yh.mapview.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Log文件目录
 * ——basePath
 */
public class LogTool {
    private static final String TAG = "LogTool";
    private static final String LOG_POSITION_FORMAT = "[(%1$s:%2$d)#%3$s]";
    private static final int JSON_INDENT = 4;

    private static volatile LogTool mInstance;

    private static boolean isShowD = BuildConfig.DEBUG;
    private static boolean isShowI = true;
    private static boolean isShowE = true;
    private static boolean isShowV = false;
    private static boolean isShowW = false;
    private static boolean isShowSyso = false;

    private static final int LOG_MAXLENGTH = 3 * 1024;

    // 是否保存Log到文件中
    private boolean isSave;

    private LogTool() {
    }


    public static void LogD(String TAG, String info) {
//        if (isShowD) {
//            info = getLogPosition() + " " + info;
//            Log.d(TAG, info);
//        }

        if (!isShowD)
            return;

        long length = info.length();
        if (length <= LOG_MAXLENGTH) {                 // 长度小于等于限制直接打印
            Log.d(TAG, getLogPosition() + " " + info);
        } else {
            boolean isFirstLine = true;
            while (info.length() > LOG_MAXLENGTH) {     // 循环分段打印日志
                String logContent = info.substring(0, LOG_MAXLENGTH);
                info = info.replace(logContent, "");
                if (isFirstLine) {
                    Log.d(TAG, getLogPosition() + " " + logContent);
                } else {
                    Log.d(TAG, logContent);
                }
            }
            Log.d(TAG, info);// 打印剩余日志
        }
    }

    public static void LogI(String TAG, String info) {
        if (isShowI) {
            if (isShowD)
                info = getLogPosition() + " " + info;
            Log.i(TAG, info);
        }
    }

    public static void LogE(String TAG, String info) {
        if (isShowE) {
            if (isShowD)
                info = getLogPosition() + " " + info;
            Log.e(TAG, info);
        }
    }

    public static void LogE_DEBUG(String TAG, String info) {
//        if (isShowE && isShowD) {
//            info = getLogPosition() + " " + info;
//            Log.e(TAG, info);
//        }

        if (!isShowE || !isShowD) {
            return;
        }

        long length = info.length();
        if (length <= LOG_MAXLENGTH) {                 // 长度小于等于限制直接打印
            Log.e(TAG, getLogPosition() + " " + info);
        } else {
            boolean isFirstLine = true;
            while (info.length() > LOG_MAXLENGTH) {     // 循环分段打印日志
                String logContent = info.substring(0, LOG_MAXLENGTH);
                info = info.replace(logContent, "");
                if (isFirstLine) {
                    Log.e(TAG, getLogPosition() + " " + logContent);
                } else {
                    Log.e(TAG, logContent);
                }
            }
            Log.e(TAG, info);// 打印剩余日志
        }
    }

    public static void LogE(String TAG, String info, Throwable e) {
        if (isShowE) {
            if (isShowD)
                info = getLogPosition() + " " + info;
            Log.e(TAG, info, e);
        }
    }

    public static void LogV(String TAG, String info) {
        if (isShowV) {
//            info = getLogPosition() + " " + info;
            Log.v(TAG, info);
        }
    }

    public static void LogW(String TAG, String info) {
        if (isShowW) {
            info = getLogPosition() + " " + info;
            Log.w(TAG, info);
        }
    }

    public static void json(String TAG, String info) {
        String message = "";
        try {
            if (info.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(info);
                message = jsonObject.toString(JSON_INDENT);
            } else if (info.startsWith("{")) {
                JSONArray jsonArray = new JSONArray(info);
                message = jsonArray.toString(JSON_INDENT);
            }
        } catch (JSONException e) {
            message = getLogPosition() + "\n" + info;
            LogE(TAG, message, e);
        }

        message = getLogPosition() + "\n" + message;
        LogE_DEBUG(TAG, message);
    }

    public static void SysOut(String TAG, String info) {
        if (isShowSyso) {
            info = getLogPosition() + " " + info;
            System.out.println(TAG + "---> " + info);
        }
    }

    public static void setLogSwitch(boolean D, boolean I, boolean E, boolean V, boolean W, boolean Syso) {
        isShowD = D;
        isShowI = I;
        isShowE = E;
        isShowV = V;
        isShowW = W;
        isShowSyso = Syso;
    }

    /**
     * 获取调用log的位置
     *
     * @return
     */
    private static String getLogPosition() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        if (trace != null && trace.length >= 3) {
            String methodName = trace[2].getMethodName();
            int lineNumber = trace[2].getLineNumber();
            String fileName = trace[2].getFileName();
            caller = String.format(LOG_POSITION_FORMAT, fileName, lineNumber, methodName);
        }

        return caller;
    }

    // ========================================================================================== //

    public static String LogBytes2Hex(byte[] bytes, String name) {
        StringBuffer buffer = new StringBuffer();
        String log = String.format("%s = %s", name, "null");
        if (bytes != null) {
            for (int i = 0; i < bytes.length; i++) {
                buffer.append(String.format("%02X ", bytes[i]));
            }
            log = String.format("%s = %s", name, buffer.toString());
        }
        return log;
    }

    public static String LogBytes2Hex(ArrayList<Byte> bytes, String name) {
        StringBuffer buffer = new StringBuffer();
        String log = String.format("%s = %s", name, "null");
        if (bytes != null) {
            for (int i = 0; i < bytes.size(); i++) {
                buffer.append(String.format("%02X ", bytes.get(i)));
            }
            log = String.format("%s = %s", name, buffer.toString());
        }

        return log;
    }

    public static String LogBytes(byte[] bytes, String name) {
        StringBuffer buffer = new StringBuffer();
        String log = String.format("%s = %s", name, "null");
        if (bytes != null) {
            for (int i = 0; i < bytes.length; i++) {
                buffer.append(String.format("%d ", bytes[i]));
            }
            log = String.format("%s = %s", name, buffer.toString());
        }
        return log;
    }

    public static String LogBytes(ArrayList<Byte> bytes, String name) {
        StringBuffer buffer = new StringBuffer();
        String log = String.format("%s = %s", name, "null");
        if (bytes != null) {
            for (int i = 0; i < bytes.size(); i++) {
                buffer.append(String.format("%d ", bytes.get(i)));
            }
            log = String.format("%s = %s", name, buffer.toString());
        }

        return log;
    }
}

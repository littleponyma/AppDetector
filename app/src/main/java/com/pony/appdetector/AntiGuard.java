package com.pony.appdetector;

import java.io.File;
import java.lang.reflect.Field;

public class AntiGuard {
    static {
        System.loadLibrary("anti_guard");
    }

    public AntiGuard() {
    }

    /**
     * java法检测是否连上调试器
     *
     * @return
     */
    public boolean checkIsDebuggerConnected() {
        return android.os.Debug.isDebuggerConnected()||antiDebug();
    }

    /**
     * 检查root权限
     *
     * @return
     */
    public boolean isRoot() {
        return isSUExist()||isSu();//user版本，继续查su文件
    }

    private native boolean isSu();

    private boolean isSUExist() {
        File file = null;
        String[] paths = {"/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"};
        for (String path : paths) {
            file = new File(path);
            if (file.exists()) return true;
        }
        return false;
    }
    private native boolean antiDebug();
    /**
     * 检查是否存在XP框架
     *
     * @return
     */
    public boolean checkXposed(){
        return isXposedExists()||isXposedExistByThrow()||tryShutdownXposed();
    }

    private static final String XPOSED_HELPERS = "de.robv.android.xposed.XposedHelpers";
    private static final String XPOSED_BRIDGE = "de.robv.android.xposed.XposedBridge";

    /**
     * 通过检查是否已经加载了XP类来检测
     *
     * @return
     */
    public boolean isXposedExists() {
        try {
            ClassLoader
                    .getSystemClassLoader()
                    .loadClass(XPOSED_HELPERS)
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            ClassLoader
                    .getSystemClassLoader()
                    .loadClass(XPOSED_BRIDGE)
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 通过主动抛出异常，检查堆栈信息来判断是否存在XP框架
     *
     * @return
     */
    public boolean isXposedExistByThrow() {
        try {
            throw new Exception("gg");
        } catch (Exception e) {
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                if (stackTraceElement.getClassName().contains(XPOSED_BRIDGE)) return true;
            }
            return false;
        }
    }

    /**
     * 尝试关闭XP框架
     * 先通过isXposedExistByThrow判断有没有XP框架
     * 有的话先hookXP框架的全局变量disableHooks
     * <p>
     * 漏洞在，如果XP框架先hook了isXposedExistByThrow的返回值，那么后续就没法走了
     * 现在直接先hookXP框架的全局变量disableHooks
     *
     * @return 是否关闭成功的结果
     */
    public boolean tryShutdownXposed() {
        Field xpdisabledHooks;
        try {
            xpdisabledHooks = ClassLoader.getSystemClassLoader()
                    .loadClass(XPOSED_BRIDGE)
                    .getDeclaredField("disableHooks");
            xpdisabledHooks.setAccessible(true);
            xpdisabledHooks.set(null, Boolean.TRUE);
            return true;
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * A native method that is implemented by the 'appdetector' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}

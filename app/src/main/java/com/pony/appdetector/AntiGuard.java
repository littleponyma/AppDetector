package com.pony.appdetector;

import java.io.File;
import java.lang.reflect.Field;

public class AntiGuard {

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
    public boolean checkRoot() {
        return isSu();
    }

    private native boolean isSu();


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


    public boolean checkMagisk() {
        return checkMagisk2() || new File("/sbin/magisk").exists() || new File("/sbin/.magisk").exists() || new File("/data/adb/magisk").exists();
    }

    public native boolean checkMagisk2();

    public boolean checkEmulator() {
        return new File("/system/lib/libhoudini.so").exists() ||
                new File("/system/lib64/libhoudini.so").exists() ||
                new File("/system/bin/houdini64").exists() ||
                new File("/system/bin/houdini").exists() ||
                new File("/system/lib/libdroid4x.so").exists() ||
                new File("/system/lib64/libdroid4x.so").exists() ||
                new File("/system/bin/windroyed").exists() ||
                new File("/system/bin/windroye").exists() ||
                new File("/system/bin/ttVM_x86").exists() ||
                new File("/system/bin/ttVM").exists() ||
                new File("/system/bin/nox-prop").exists() ||
                new File("/system/bin/microvirtd").exists() ||
                new File("/system/bin/microvirt-prop").exists() ||
                new File("/dev/vboxguest").exists() ||
                new File("/dev/vboxuser").exists() ||
                new File("/system/bin/androVM-prop").exists() ||
                new File("/system/bin/androVM-vbox-sf").exists() ||
                new File("/system/bin/androVM_setprop").exists() ||
                new File("/system/bin/mount.vboxsf").exists() ||
                new File("/system/lib/libdvm.so").exists();
    }
}

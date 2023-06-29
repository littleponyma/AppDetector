package com.pony.appdetector;

public class AntiGuard {
    static {
        System.loadLibrary("anti_guard");
    }

    public AntiGuard() {
    }

    /**
     * A native method that is implemented by the 'appdetector' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}

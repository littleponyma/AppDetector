#include <jni.h>
#include <string>
#include <unistd.h>
#include "logger.h"

jboolean isSu();

void checkSu(JNIEnv *env);

extern "C"
JNIEXPORT jstring JNICALL
Java_com_pony_appdetector_AntiGuard_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_pony_appdetector_AntiGuard_isSu(JNIEnv *env, jobject thiz) {
    return isSu();
}


jboolean isSu() {
    jboolean result;
    char *mPaths[] = {"/sbin/su",
                      "/system/bin/su",
                      "/system/xbin/su",
                      "/data/local/xbin/su",
                      "/data/local/bin/su",
                      "/system/sd/xbin/su",
                      "/system/bin/failsafe/su",
                      "/data/local/su"};
    for (int i = 0; sizeof(mPaths); i++) {
        FILE *mFIle = fopen(mPaths[i], "r");
        if (mFIle != nullptr) {
            fclose(mFIle);
            result = 1;
            break;
        }
    }
    return result;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_pony_appdetector_AntiGuard_antiDebug(JNIEnv *env, jobject thiz) {
    jclass Debug = env->FindClass("android/os/Debug");
    jmethodID isDebuggerConnected = env->GetStaticMethodID(Debug, "isDebuggerConnected", "()Z");
    jboolean result = env->CallStaticBooleanMethod(Debug, isDebuggerConnected);
    env->DeleteLocalRef(Debug);
    return result;
}

void checkSu(JNIEnv *env) {
    jclass AntiGuard = env->FindClass("com/pony/appdetector/AntiGuard");
    jmethodID isSuMethod = env->GetMethodID(AntiGuard, "checkRoot", "()Z");
    jobject antiGuard = env->AllocObject(AntiGuard);
    jboolean result = env->CallBooleanMethod(antiGuard, isSuMethod);
    jboolean isAttached;
    isAttached = isSu();
    if (result != isAttached) {
        sleep(999999);
    }
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_pony_appdetector_AntiGuard_checkMagisk2(JNIEnv *env, jobject thiz) {
    jboolean result;
    char *mPaths[] = {"/sbin/magisk",
                      "/sbin/.magisk",
                      "/data/adb/magisk"};
    for (int i = 0; sizeof(mPaths); i++) {
        FILE *mFIle = fopen(mPaths[i], "r");
        if (mFIle != nullptr) {
            fclose(mFIle);
            result = 1;
            break;
        }
        if (access(mPaths[i], F_OK) == 0) {
            result = 1;
            break;
        }
    }
    return result;

}

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    checkSu(env);
    return JNI_VERSION_1_6;
}

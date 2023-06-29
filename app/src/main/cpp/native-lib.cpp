#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_pony_appdetector_AntiGuard_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_pony_appdetector_AntiGuard_isSu(JNIEnv *env, jobject thiz) {
    FILE *mFIle = fopen("/system/bin/su", "r");
    if (mFIle == nullptr) {
        mFIle = fopen("/system/xbin/su", "r");
        if (mFIle == nullptr) {
            return false;
        }
    }
    fclose(mFIle);
    return true;
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

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {

    return JNI_VERSION_1_6;
}
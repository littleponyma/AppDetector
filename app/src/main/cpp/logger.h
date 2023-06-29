//
// Created by 89394 on 2023/6/29.
//

#include <android/log.h>
#define LOG_TAG "AntiDebug"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
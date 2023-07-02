#include "CurrentOS.h"
#include <windows.h>

JNIEXPORT void JNICALL Java_by_vorivoda_matvey_controller_util_operating_system_CurrentOS_openInWinApi
(JNIEnv * env, jclass class, jstring path) {
    const char *nativeString = (*env)->GetStringUTFChars(env, path, 0);
    ShellExecute(NULL, "open", nativeString, NULL, NULL, SW_SHOWNORMAL);
    (*env)->ReleaseStringUTFChars(env, path, nativeString);
}
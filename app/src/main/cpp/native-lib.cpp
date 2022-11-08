#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring  JNICALL
Java_com_testingApp_androidCpp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring  JNICALL
Java_com_testingApp_androidCpp_MainActivity_stringFromOtherJNI(JNIEnv* env, jobject) {
            std::string hello = "Hello from another C++";
            return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_testingApp_androidCpp_CalculatorActivity_doSumCpp(JNIEnv *env, jobject thiz, jobject i, jobject j) {
    jclass cls = env->FindClass("java/lang/Integer");

    int c_i = env->CallIntMethod(i, env->GetMethodID(cls, "intValue", "()I"));
    int c_j = env->CallIntMethod(j, env->GetMethodID(cls, "intValue", "()I"));
    int res = c_i + c_j;
    jmethodID  constructor = env->GetMethodID( cls, "<init>", "(I)V");
    return env->NewObject(cls, constructor,  res);;
}
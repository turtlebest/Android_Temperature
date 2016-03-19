//
// Created by Jessica Huang on 3/17/16.
//

#include "nyu_cs_jessicahuang_temperaturec_Controller_NdkJniUtils.h"

JNIEXPORT jfloat JNICALL
Java_nyu_cs_jessicahuang_temperaturec_Controller_NdkJniUtils_celToFar
        (JNIEnv *env, jobject instance, jfloat cel) {
    return celToFar(cel);
}

JNIEXPORT jfloat JNICALL
Java_nyu_cs_jessicahuang_temperaturec_Controller_NdkJniUtils_farToCel
        (JNIEnv *env, jobject instance, jfloat far) {
    return farToCel(far);
}

JNIEXPORT jobject JNICALL
Java_nyu_cs_jessicahuang_temperaturec_Controller_NdkJniUtils_convertDayListToFar
        (JNIEnv *env, jobject instance, jobject dayList) {
    int i = 0;

    jclass listClass = (*env)->FindClass(env, "java/util/ArrayList");
    jmethodID listGet = (*env)->GetMethodID(env, listClass, "get", "(I)Ljava/lang/Object;");
    jmethodID listSize = (*env)->GetMethodID(env, listClass, "size", "()I");
    jclass dayClass = (*env)->FindClass(env, "nyu/cs/jessicahuang/temperaturec/Model/Day");
    jmethodID dayGetTemperature = (*env)->GetMethodID(env, dayClass, "getTemperature", "()F");
    jmethodID daySetTemperature = (*env)->GetMethodID(env, dayClass, "setTemperature", "(F)V");
    jint len = (*env)->CallIntMethod(env, dayList, listSize);

    for (i ; i < len; i++) {
        jobject dayObject = (*env)->CallObjectMethod(env, dayList, listGet, i);
        jfloat temp = (*env)->CallFloatMethod(env, dayObject, dayGetTemperature);
        temp = celToFar(temp);
        (*env)->CallVoidMethod(env, dayObject, daySetTemperature, temp);
    }
}

JNIEXPORT jobject JNICALL
Java_nyu_cs_jessicahuang_temperaturec_Controller_NdkJniUtils_convertDayListToCel
        (JNIEnv *env, jobject instance, jobject dayList) {
    int i = 0;

    jclass listClass = (*env)->FindClass(env, "java/util/ArrayList");
    jmethodID listGet = (*env)->GetMethodID(env, listClass, "get", "(I)Ljava/lang/Object;");
    jmethodID listSize = (*env)->GetMethodID(env, listClass, "size", "()I");
    jclass dayClass = (*env)->FindClass(env, "nyu/cs/jessicahuang/temperaturec/Model/Day");
    jmethodID dayGetTemperature = (*env)->GetMethodID(env, dayClass, "getTemperature", "()F");
    jmethodID daySetTemperature = (*env)->GetMethodID(env, dayClass, "setTemperature", "(F)V");
    jint len = (*env)->CallIntMethod(env, dayList, listSize);

    for (i ; i < len; i++) {
        jobject dayObject = (*env)->CallObjectMethod(env, dayList, listGet, i);
        jfloat temp = (*env)->CallFloatMethod(env, dayObject, dayGetTemperature);
        temp = farToCel(temp);
        (*env)->CallVoidMethod(env, dayObject, daySetTemperature, temp);
    }
}

/**
 * NdkJniUtils.java
 * The class to call NDK library - tempLib
 *
 * Created by Jessica Huang.
 */
package nyu.cs.jessicahuang.temperaturec.Controller;

import nyu.cs.jessicahuang.temperaturec.Model.Day;

import java.util.List;

import android.util.Log;

public class NdkJniUtils {

    public native float celToFar(float cel);

    public native float farToCel(float far);

    public native List<Day> convertDayListToFar(List<Day> dayInfoList);

    public native List<Day> convertDayListToCel(List<Day> dayInfoList);

    static {
        try {
            System.loadLibrary("tempLib");
        } catch (UnsatisfiedLinkError ule) {
            Log.e("NdkJniUtils", "Error: Cannot load Ndk library: " + ule.getMessage());
        }

    }
}

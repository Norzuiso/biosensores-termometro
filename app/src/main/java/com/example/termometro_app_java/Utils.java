package com.example.termometro_app_java;

public class Utils {

    public static float calculateF(Float tempC) {
        return (float) ((tempC * 1.8) + 32);
    }

    public static float calculateR(Float tempC) {
        return (float) ((tempC * 1.8) + 491.67);
    }

    public static float calculateK(Float tempC) {
        return (float) (tempC + 273.15);
    }
}

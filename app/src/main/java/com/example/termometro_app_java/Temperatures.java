package com.example.termometro_app_java;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Temperatures {

    private String time;
    private Float tempC;
    private Float tempF;
    private Float tempK;
    private Float tempR;

    public Temperatures(Float tempC) {
        this.tempC = tempC;
        this.tempF = Utils.calculateF(tempC);
        this.tempR = Utils.calculateR(tempC);
        this.tempK = Utils.calculateK(tempC);
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

    }

    public Temperatures(Float tempC, Float tempF, Float tempK, Float tempR) {
        this.tempC = tempC;
        this.tempF = tempF;
        this.tempK = tempK;
        this.tempR = tempR;
        this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public String toString() {
        return String.format("%.2f °C|%.2f °F|%.2f K|%.2f R|", tempC, tempF, tempK, tempR);
    }
    public String toStringOnFile() {
        return time+"="+String.format("%.2f|%.2f|%.2f|%.2f", tempC, tempF, tempK, tempR);
    }

    public Temperatures(String time, Float tempC) {
        this.time = time;
        this.tempC = tempC;
        this.tempF = Utils.calculateF(tempC);
        this.tempR = Utils.calculateR(tempC);
        this.tempK = Utils.calculateK(tempC);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Float getTempC() {
        return tempC;
    }

    public void setTempC(Float tempC) {
        this.tempC = tempC;
    }

    public Float getTempF() {
        return tempF;
    }

    public void setTempF(Float tempF) {
        this.tempF = tempF;
    }

    public Float getTempK() {
        return tempK;
    }

    public void setTempK(Float tempK) {
        this.tempK = tempK;
    }

    public Float getTempR() {
        return tempR;
    }

    public void setTempR(Float tempR) {
        this.tempR = tempR;
    }

    public String toStringSave() {
        return String.format("%s->%.2f °C|%.2f °F|%.2f K|%.2f R|", time,tempC, tempF, tempK, tempR);
    }
}

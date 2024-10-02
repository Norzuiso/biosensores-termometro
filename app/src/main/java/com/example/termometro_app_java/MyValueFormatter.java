package com.example.termometro_app_java;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.DecimalFormat;

// Custom Value Formatter class
public class MyValueFormatter extends ValueFormatter {

    private final DecimalFormat format;

    public MyValueFormatter() {
        format = new DecimalFormat("###,###,##0.00"); // Ensures two decimal places
    }

    @Override
    public String getFormattedValue(float value) {
        return format.format(value);
    }
}
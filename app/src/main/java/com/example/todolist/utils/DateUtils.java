package com.example.todolist.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    // Get today's date
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(Calendar.getInstance().getTime());
        return todayDate;
    }

    // Get tomorrow's date
    public static String getTomorrowDate()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowDate = dateFormat.format(calendar.getTime());
        return tomorrowDate;
    }
}

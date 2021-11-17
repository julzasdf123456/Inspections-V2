package com.lopez.julz.inspectionv2.helpers;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ObjectHelpers {

    public static String databaseName() {
        return "inspections";
    }

    public static String getSelectedTextFromRadioGroup(RadioGroup rg, View view) {
        try {
            int selectedId = rg.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) view.findViewById(selectedId);
            return radioButton.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDateTime() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            return formatter.format(date);
        } catch (Exception e) {
            Log.e("ERR_GET_DATE", e.getMessage());
            return null;
        }
    }
}

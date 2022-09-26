package com.lopez.julz.inspectionv2.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String generateRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static String getTimeInMillis() {
        try {
            return new Date().getTime() + "";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateIDandRandString() {
        return getTimeInMillis() + "-" + generateRandomString();
    }

    public static String roundTwo(Double doubleX) {
        try {
            DecimalFormat df = new DecimalFormat("#,###.00");
            return df.format(doubleX);
        } catch (Exception e) {
            return "";
        }
    }

    public static String roundTwoNoComma(Double doubleX) {
        try {
            DecimalFormat df = new DecimalFormat("####.00");
            return df.format(doubleX);
        } catch (Exception e) {
            return "";
        }
    }
}

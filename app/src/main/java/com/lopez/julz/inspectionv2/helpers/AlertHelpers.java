package com.lopez.julz.inspectionv2.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.lopez.julz.inspectionv2.R;

public class AlertHelpers {

    public static void infoDialog(Context context, String title, String message) {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(context);

        builder.setTitle(title);

        builder.setMessage(message);

        builder.setCancelable(false);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
            }
        });

        alertDialog.show();
    }
}

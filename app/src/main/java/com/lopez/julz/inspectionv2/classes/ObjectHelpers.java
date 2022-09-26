package com.lopez.julz.inspectionv2.classes;

import android.util.Log;

import com.lopez.julz.inspectionv2.api.ServiceConnections;
import com.lopez.julz.inspectionv2.database.LocalServiceConnections;

public class ObjectHelpers {
    public static String getAddress(ServiceConnections serviceConnections) {
        try {
            if (serviceConnections != null) {
                return (serviceConnections.getSitio() != null ? (serviceConnections.getSitio() + ", ") : "") +
                        (serviceConnections.getBarangayFull() != null ? (serviceConnections.getBarangayFull() + ", ") : "") +
                        (serviceConnections.getTownFull() != null ? (serviceConnections.getTownFull()) : "");
            } else {
                return "SC is Null";
            }
        } catch (Exception e) {
            Log.e("ERR_GET_ADDR", e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public static String getAddress(LocalServiceConnections serviceConnections) {
        try {
            if (serviceConnections != null) {
                return (serviceConnections.getSitio() != null ? (serviceConnections.getSitio() + ", ") : "") +
                        (serviceConnections.getBarangayFull() != null ? (serviceConnections.getBarangayFull() + ", ") : "") +
                        (serviceConnections.getTownFull() != null ? (serviceConnections.getTownFull()) : "");
            } else {
                return "SC is Null";
            }
        } catch (Exception e) {
            Log.e("ERR_GET_ADDR", e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public static String getBillDepositId() {
        return "1629263796222";
    }
}

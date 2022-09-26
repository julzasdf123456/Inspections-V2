package com.lopez.julz.inspectionv2.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PayTransactions {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name="ServiceConnectionId")
    private String ServiceConnectionId;

    @ColumnInfo(name="Particular")
    private String Particular;

    @ColumnInfo(name="Amount")
    private String Amount;

    @ColumnInfo(name="Vat")
    private String Vat;

    @ColumnInfo(name="Total")
    private String Total;

    public PayTransactions() {
    }

    public PayTransactions(@NonNull String id, String serviceConnectionId, String particular, String amount, String vat, String total) {
        this.id = id;
        ServiceConnectionId = serviceConnectionId;
        Particular = particular;
        Amount = amount;
        Vat = vat;
        Total = total;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getServiceConnectionId() {
        return ServiceConnectionId;
    }

    public void setServiceConnectionId(String serviceConnectionId) {
        ServiceConnectionId = serviceConnectionId;
    }

    public String getParticular() {
        return Particular;
    }

    public void setParticular(String particular) {
        Particular = particular;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getVat() {
        return Vat;
    }

    public void setVat(String vat) {
        Vat = vat;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }
}

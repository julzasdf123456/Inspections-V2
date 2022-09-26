package com.lopez.julz.inspectionv2.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TotalPayments {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name="ServiceConnectionId")
    private String ServiceConnectionId;

    @ColumnInfo(name="SubTotal")
    private String SubTotal;

    @ColumnInfo(name="Form2307TwoPercent")
    private String Form2307TwoPercent;

    @ColumnInfo(name="Form2307FivePercent")
    private String Form2307FivePercent;

    @ColumnInfo(name="TotalVat")
    private String TotalVat;

    @ColumnInfo(name="Total")
    private String Total;

    @ColumnInfo(name="Notes")
    private String Notes;

    public TotalPayments() {
    }

    public TotalPayments(String id, String serviceConnectionId, String subTotal, String form2307TwoPercent, String form2307FivePercent, String totalVat, String total, String notes) {
        this.id = id;
        ServiceConnectionId = serviceConnectionId;
        SubTotal = subTotal;
        Form2307TwoPercent = form2307TwoPercent;
        Form2307FivePercent = form2307FivePercent;
        TotalVat = totalVat;
        Total = total;
        Notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceConnectionId() {
        return ServiceConnectionId;
    }

    public void setServiceConnectionId(String serviceConnectionId) {
        ServiceConnectionId = serviceConnectionId;
    }

    public String getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(String subTotal) {
        SubTotal = subTotal;
    }

    public String getForm2307TwoPercent() {
        return Form2307TwoPercent;
    }

    public void setForm2307TwoPercent(String form2307TwoPercent) {
        Form2307TwoPercent = form2307TwoPercent;
    }

    public String getForm2307FivePercent() {
        return Form2307FivePercent;
    }

    public void setForm2307FivePercent(String form2307FivePercent) {
        Form2307FivePercent = form2307FivePercent;
    }

    public String getTotalVat() {
        return TotalVat;
    }

    public void setTotalVat(String totalVat) {
        TotalVat = totalVat;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }
}

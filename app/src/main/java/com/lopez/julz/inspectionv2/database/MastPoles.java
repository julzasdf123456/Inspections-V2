package com.lopez.julz.inspectionv2.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MastPoles {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "ServiceConnectionId")
    private String ServiceConnectionId;

    @ColumnInfo(name = "Latitude")
    private String Latitude;

    @ColumnInfo(name = "Longitude")
    private String Longitude;

    @ColumnInfo(name = "PoleRemarks")
    private String PoleRemarks;

    @ColumnInfo(name = "IsUploaded")
    private String IsUploaded;

    @ColumnInfo(name = "DateTimeTaken")
    private String DateTimeTaken;

    public MastPoles() {
    }

    public MastPoles(@NonNull String id, String serviceConnectionId, String latitude, String longitude, String poleRemarks, String isUploaded, String dateTimeTaken) {
        this.id = id;
        ServiceConnectionId = serviceConnectionId;
        Latitude = latitude;
        Longitude = longitude;
        PoleRemarks = poleRemarks;
        IsUploaded = isUploaded;
        DateTimeTaken = dateTimeTaken;
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

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getIsUploaded() {
        return IsUploaded;
    }

    public void setIsUploaded(String isUploaded) {
        IsUploaded = isUploaded;
    }

    public String getDateTimeTaken() {
        return DateTimeTaken;
    }

    public void setDateTimeTaken(String dateTimeTaken) {
        DateTimeTaken = dateTimeTaken;
    }

    public String getPoleRemarks() {
        return PoleRemarks;
    }

    public void setPoleRemarks(String poleRemarks) {
        PoleRemarks = poleRemarks;
    }
}

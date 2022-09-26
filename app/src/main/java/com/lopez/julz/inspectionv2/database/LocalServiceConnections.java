package com.lopez.julz.inspectionv2.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LocalServiceConnections {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "MemberConsumerId")
    private String MemberConsumerId;

    @ColumnInfo(name = "DateOfApplication")
    private String DateOfApplication;

    @ColumnInfo(name = "ServiceAccountName")
    private String ServiceAccountName;

    @ColumnInfo(name = "AccountCount")
    private String AccountCount;

    @ColumnInfo(name = "Sitio")
    private String Sitio;

    @ColumnInfo(name = "Barangay")
    private String Barangay;

    @ColumnInfo(name = "Town")
    private String Town;

    @ColumnInfo(name = "ContactNumber")
    private String ContactNumber;

    @ColumnInfo(name = "EmailAddress")
    private String EmailAddress;

    @ColumnInfo(name = "AccountType")
    private String AccountType;

    @ColumnInfo(name = "AccountOrganization")
    private String AccountOrganization;

    @ColumnInfo(name = "OrganizationAccountNumber")
    private String OrganizationAccountNumber;

    @ColumnInfo(name = "IsNIHE")
    private String IsNIHE;

    @ColumnInfo(name = "AccountApplicationType")
    private String AccountApplicationType;

    @ColumnInfo(name = "ConnectionApplicationType")
    private String ConnectionApplicationType;

    @ColumnInfo(name = "BuildingType")
    private String BuildingType;

    @ColumnInfo(name = "Status")
    private String Status;

    @ColumnInfo(name = "Notes")
    private String Notes;

    @ColumnInfo(name = "BarangayFull")
    private String BarangayFull;

    @ColumnInfo(name = "TownFull")
    private String TownFull;

    public LocalServiceConnections(){}

    public LocalServiceConnections(@NonNull String id, String memberConsumerId, String dateOfApplication, String serviceAccountName, String accountCount, String sitio, String barangay, String town, String contactNumber, String emailAddress, String accountType, String accountOrganization, String organizationAccountNumber, String isNIHE, String accountApplicationType, String connectionApplicationType, String buildingType, String status, String notes, String barangayFull, String townFull) {
        this.id = id;
        MemberConsumerId = memberConsumerId;
        DateOfApplication = dateOfApplication;
        ServiceAccountName = serviceAccountName;
        AccountCount = accountCount;
        Sitio = sitio;
        Barangay = barangay;
        Town = town;
        ContactNumber = contactNumber;
        EmailAddress = emailAddress;
        AccountType = accountType;
        AccountOrganization = accountOrganization;
        OrganizationAccountNumber = organizationAccountNumber;
        IsNIHE = isNIHE;
        AccountApplicationType = accountApplicationType;
        ConnectionApplicationType = connectionApplicationType;
        BuildingType = buildingType;
        Status = status;
        Notes = notes;
        BarangayFull = barangayFull;
        TownFull = townFull;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberConsumerId() {
        return MemberConsumerId;
    }

    public void setMemberConsumerId(String memberConsumerId) {
        MemberConsumerId = memberConsumerId;
    }

    public String getDateOfApplication() {
        return DateOfApplication;
    }

    public void setDateOfApplication(String dateOfApplication) {
        DateOfApplication = dateOfApplication;
    }

    public String getServiceAccountName() {
        return ServiceAccountName;
    }

    public void setServiceAccountName(String serviceAccountName) {
        ServiceAccountName = serviceAccountName;
    }

    public String getAccountCount() {
        return AccountCount;
    }

    public void setAccountCount(String accountCount) {
        AccountCount = accountCount;
    }

    public String getSitio() {
        return Sitio;
    }

    public void setSitio(String sitio) {
        Sitio = sitio;
    }

    public String getBarangay() {
        return Barangay;
    }

    public void setBarangay(String barangay) {
        Barangay = barangay;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public String getAccountOrganization() {
        return AccountOrganization;
    }

    public void setAccountOrganization(String accountOrganization) {
        AccountOrganization = accountOrganization;
    }

    public String getOrganizationAccountNumber() {
        return OrganizationAccountNumber;
    }

    public void setOrganizationAccountNumber(String organizationAccountNumber) {
        OrganizationAccountNumber = organizationAccountNumber;
    }

    public String getIsNIHE() {
        return IsNIHE;
    }

    public void setIsNIHE(String isNIHE) {
        IsNIHE = isNIHE;
    }

    public String getAccountApplicationType() {
        return AccountApplicationType;
    }

    public void setAccountApplicationType(String accountApplicationType) {
        AccountApplicationType = accountApplicationType;
    }

    public String getConnectionApplicationType() {
        return ConnectionApplicationType;
    }

    public void setConnectionApplicationType(String connectionApplicationType) {
        ConnectionApplicationType = connectionApplicationType;
    }

    public String getBuildingType() {
        return BuildingType;
    }

    public void setBuildingType(String buildingType) {
        BuildingType = buildingType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getBarangayFull() {
        return BarangayFull;
    }

    public void setBarangayFull(String barangayFull) {
        BarangayFull = barangayFull;
    }

    public String getTownFull() {
        return TownFull;
    }

    public void setTownFull(String townFull) {
        TownFull = townFull;
    }
}

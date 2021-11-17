package com.lopez.julz.inspectionv2.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OfflineUsers {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo (name = "UserId")
    private String UserId;

    @ColumnInfo (name = "Username")
    private String Username;

    @ColumnInfo (name = "Password")
    private String Password;

    public OfflineUsers() {}

    public OfflineUsers(int id, String userId, String username, String password) {
        this.id = id;
        UserId = userId;
        Username = username;
        Password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}

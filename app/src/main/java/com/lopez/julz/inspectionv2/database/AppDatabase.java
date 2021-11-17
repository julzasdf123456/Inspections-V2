package com.lopez.julz.inspectionv2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LocalServiceConnections.class, LocalServiceConnectionInspections.class, OfflineUsers.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ServiceConnectionsDao serviceConnectionsDao();

    public abstract ServiceConnectionInspectionsDao serviceConnectionInspectionsDao();

    public abstract OfflineUsersDao offlineUsersDao();
}

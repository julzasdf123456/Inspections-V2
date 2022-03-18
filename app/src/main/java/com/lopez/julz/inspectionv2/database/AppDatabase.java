package com.lopez.julz.inspectionv2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LocalServiceConnections.class,
        LocalServiceConnectionInspections.class,
        OfflineUsers.class,
        Photos.class,
        Barangays.class,
        Towns.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ServiceConnectionsDao serviceConnectionsDao();

    public abstract ServiceConnectionInspectionsDao serviceConnectionInspectionsDao();

    public abstract OfflineUsersDao offlineUsersDao();

    public abstract PhotosDao photosDao();

    public abstract BarangaysDao barangaysDao();

    public abstract TownsDao townsDao();
}

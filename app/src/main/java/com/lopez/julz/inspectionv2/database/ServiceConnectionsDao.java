package com.lopez.julz.inspectionv2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServiceConnectionsDao {
    @Query("SELECT * FROM LocalServiceConnections WHERE Status IS NOT 'TRASH'")
    List<LocalServiceConnections> getAll();

    @Insert
    void insertAll(LocalServiceConnections... localServiceConnections);

    @Update
    void updateServiceConnections(LocalServiceConnections... localServiceConnections);

    @Query("SELECT * FROM LocalServiceConnections WHERE id = :id")
    LocalServiceConnections getOne(String id);

    @Query("DELETE FROM LocalServiceConnections WHERE id = :id")
    void delete(String id);
}

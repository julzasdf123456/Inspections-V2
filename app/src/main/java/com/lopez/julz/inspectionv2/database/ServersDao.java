package com.lopez.julz.inspectionv2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServersDao {
    @Query("SELECT * FROM Servers")
    List<Servers> getAll();

    @Insert
    void insertAll(Servers... servers);

    @Update
    void updateAll(Servers... servers);

    @Query("SELECT * FROM Servers WHERE id = :id")
    Servers getOne(String id);
}

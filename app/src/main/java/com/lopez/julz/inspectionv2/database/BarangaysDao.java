package com.lopez.julz.inspectionv2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BarangaysDao {
    @Query("SELECT * FROM Barangays WHERE TownId = :TownId")
    List<Barangays> getAllByTown(String TownId);

    @Insert
    void insertAll(Barangays... barangays);

    @Update
    void updateAll(Barangays... barangays);

    @Query("SELECT * FROM Barangays WHERE id = :id")
    Barangays getOne(String id);
}

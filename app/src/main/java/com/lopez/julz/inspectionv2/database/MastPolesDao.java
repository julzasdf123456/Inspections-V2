package com.lopez.julz.inspectionv2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MastPolesDao {
    @Query("SELECT * FROM MastPoles WHERE ServiceConnectionId = :scId ORDER BY DateTimeTaken DESC")
    List<MastPoles> getAllMastPoles(String scId);

    @Query("SELECT * FROM MastPoles WHERE IsUploaded='No'")
    List<MastPoles> getUnuploadedMastPoles();

    @Insert
    void insertAll(MastPoles... mastPoles);

    @Update
    void updateAll(MastPoles... mastPoles);

    @Query("DELETE FROM MastPoles WHERE id = :id")
    void deleteOne(String id);
}

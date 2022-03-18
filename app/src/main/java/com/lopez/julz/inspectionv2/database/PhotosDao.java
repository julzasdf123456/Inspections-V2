package com.lopez.julz.inspectionv2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PhotosDao {
    @Query("SELECT * FROM Photos WHERE ServiceConnectionId = :serviceConnectionId")
    List<Photos> getAllPhotos(String serviceConnectionId);

    @Insert
    void insertAll(Photos... photos);

    @Update
    void updateAll(Photos... photos);

    @Query("DELETE FROM Photos WHERE id = :id")
    void deleteOne(String id);
}

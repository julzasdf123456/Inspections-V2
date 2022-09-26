package com.lopez.julz.inspectionv2.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServiceConnectionInspectionsDao {
    @Query("SELECT * FROM LocalServiceConnectionInspections")
    List<LocalServiceConnectionInspections> getAll();

    @Insert
    void insertAll(LocalServiceConnectionInspections... localServiceConnectionInspections);

    @Update
    void updateServiceConnectionInspections(LocalServiceConnectionInspections... localServiceConnectionInspections);

    @Query("SELECT * FROM LocalServiceConnectionInspections WHERE id = :id")
    LocalServiceConnectionInspections getOne(String id);

    @Query("SELECT * FROM LocalServiceConnectionInspections WHERE ServiceConnectionId = :svcId")
    LocalServiceConnectionInspections getOneBySvcId(String svcId);

    @Query("DELETE FROM LocalServiceConnectionInspections WHERE id = :id")
    void delete(String id);

    @Query("DELETE FROM LocalServiceConnectionInspections WHERE ServiceConnectionId = :svcId")
    void deleteBySvcId(String svcId);

    @Query("SELECT * FROM LocalServiceConnectionInspections WHERE Status = :status")
    List<LocalServiceConnectionInspections> getAllByStatus(String status);

    @Query("SELECT * FROM LocalServiceConnectionInspections WHERE Status = 'Approved' OR Status = 'Re-Inspection'")
    List<LocalServiceConnectionInspections> getUploadable();

    @Query("DELETE FROM LocalServiceConnectionInspections WHERE id = :id")
    void deleteOnById(String id);
}

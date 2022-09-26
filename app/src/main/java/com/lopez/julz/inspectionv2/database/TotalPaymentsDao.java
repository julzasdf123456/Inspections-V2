package com.lopez.julz.inspectionv2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TotalPaymentsDao {
    @Query("SELECT * FROM TotalPayments WHERE ServiceConnectionId = :serviceConnectionId")
    List<TotalPayments> getAll(String serviceConnectionId);

    @Query("SELECT * FROM TotalPayments WHERE ServiceConnectionId = :serviceConnectionId LIMIT 1")
    TotalPayments getOneByServiceConnectionId(String serviceConnectionId);

    @Insert
    void insertAll(TotalPayments... totalPayments);

    @Update
    void updateAll(TotalPayments... totalPayments);
}

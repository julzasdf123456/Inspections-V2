package com.lopez.julz.inspectionv2.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PayTransactionsDao {
    @Query("SELECT * FROM PayTransactions WHERE ServiceConnectionId = :serviceConnectionId")
    List<PayTransactions> getAll(String serviceConnectionId);

    @Query("SELECT * FROM PayTransactions WHERE ServiceConnectionId IS NOT NULL")
    List<PayTransactions> getAll();

    @Query("SELECT * FROM PayTransactions WHERE ServiceConnectionId = :serviceConnectionId LIMIT 1")
    PayTransactions getOneByServiceConnectionId(String serviceConnectionId);

    @Insert
    void insertAll(PayTransactions... payTransactions);

    @Update
    void updateAll(PayTransactions... payTransactions);

    @Query("DELETE FROM PayTransactions WHERE id=:id")
    void delete(String id);
}

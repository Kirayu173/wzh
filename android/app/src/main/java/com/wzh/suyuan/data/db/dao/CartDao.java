package com.wzh.suyuan.data.db.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.wzh.suyuan.data.db.entity.CartEntity;

@Dao
public interface CartDao {
    @Query("SELECT * FROM cart_item WHERE user_id = :userId ORDER BY updated_at DESC")
    List<CartEntity> getByUser(long userId);

    @Query("SELECT * FROM cart_item WHERE user_id = :userId AND synced = 0")
    List<CartEntity> getPendingByUser(long userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CartEntity> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CartEntity item);

    @Update
    void update(CartEntity item);

    @Query("DELETE FROM cart_item WHERE user_id = :userId AND synced = 1")
    void deleteSyncedByUser(long userId);

    @Query("DELETE FROM cart_item WHERE id = :id")
    void deleteById(long id);

    @Query("UPDATE cart_item SET id = :newId, synced = 1 WHERE id = :oldId")
    void markSynced(long oldId, long newId);
}

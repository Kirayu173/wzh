package com.wzh.suyuan.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.wzh.suyuan.data.db.entity.BootstrapRecord;

@Dao
public interface BootstrapDao {
    @Query("SELECT * FROM bootstrap_record WHERE id = :id LIMIT 1")
    BootstrapRecord findById(long id);

    @Query("SELECT COUNT(*) FROM bootstrap_record")
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(BootstrapRecord record);
}

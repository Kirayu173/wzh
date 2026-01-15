package com.wzh.suyuan.data.db.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.wzh.suyuan.data.db.entity.ScanRecordEntity;

@Dao
public interface ScanRecordDao {
    @Query("SELECT * FROM scan_record ORDER BY scan_time DESC")
    List<ScanRecordEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ScanRecordEntity record);

    @Query("DELETE FROM scan_record")
    void clearAll();
}

package com.wzh.suyuan.data.db.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.wzh.suyuan.data.db.entity.ProductEntity;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductEntity> products);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProductEntity product);

    @Query("SELECT * FROM product ORDER BY id DESC LIMIT :limit OFFSET :offset")
    List<ProductEntity> getPage(int limit, int offset);

    @Query("SELECT * FROM product WHERE id = :id LIMIT 1")
    ProductEntity getById(long id);

    @Query("SELECT COUNT(*) FROM product")
    int count();
}

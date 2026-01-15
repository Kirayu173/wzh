package com.wzh.suyuan.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.wzh.suyuan.data.db.DbConverters;
import com.wzh.suyuan.data.db.entity.ProductEntity;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ProductDao_Impl implements ProductDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProductEntity> __insertionAdapterOfProductEntity;

  public ProductDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProductEntity = new EntityInsertionAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `product` (`id`,`name`,`price`,`stock`,`cover_url`,`origin`,`description`,`updated_at`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ProductEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        final String _tmp = DbConverters.fromBigDecimal(entity.getPrice());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        if (entity.getStock() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getStock());
        }
        if (entity.getCoverUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCoverUrl());
        }
        if (entity.getOrigin() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getOrigin());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDescription());
        }
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
  }

  @Override
  public void insertAll(final List<ProductEntity> products) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfProductEntity.insert(products);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insert(final ProductEntity product) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfProductEntity.insert(product);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<ProductEntity> getPage(final int limit, final int offset) {
    final String _sql = "SELECT * FROM product ORDER BY id DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 2;
    _statement.bindLong(_argIndex, offset);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
      final int _cursorIndexOfStock = CursorUtil.getColumnIndexOrThrow(_cursor, "stock");
      final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_url");
      final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
      final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ProductEntity _item;
        _item = new ProductEntity();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        _item.setName(_tmpName);
        final BigDecimal _tmpPrice;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfPrice)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfPrice);
        }
        _tmpPrice = DbConverters.toBigDecimal(_tmp);
        _item.setPrice(_tmpPrice);
        final Integer _tmpStock;
        if (_cursor.isNull(_cursorIndexOfStock)) {
          _tmpStock = null;
        } else {
          _tmpStock = _cursor.getInt(_cursorIndexOfStock);
        }
        _item.setStock(_tmpStock);
        final String _tmpCoverUrl;
        if (_cursor.isNull(_cursorIndexOfCoverUrl)) {
          _tmpCoverUrl = null;
        } else {
          _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
        }
        _item.setCoverUrl(_tmpCoverUrl);
        final String _tmpOrigin;
        if (_cursor.isNull(_cursorIndexOfOrigin)) {
          _tmpOrigin = null;
        } else {
          _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
        }
        _item.setOrigin(_tmpOrigin);
        final String _tmpDescription;
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _tmpDescription = null;
        } else {
          _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
        }
        _item.setDescription(_tmpDescription);
        final long _tmpUpdatedAt;
        _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
        _item.setUpdatedAt(_tmpUpdatedAt);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ProductEntity getById(final long id) {
    final String _sql = "SELECT * FROM product WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
      final int _cursorIndexOfStock = CursorUtil.getColumnIndexOrThrow(_cursor, "stock");
      final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "cover_url");
      final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
      final ProductEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new ProductEntity();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _result.setId(_tmpId);
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        _result.setName(_tmpName);
        final BigDecimal _tmpPrice;
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfPrice)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfPrice);
        }
        _tmpPrice = DbConverters.toBigDecimal(_tmp);
        _result.setPrice(_tmpPrice);
        final Integer _tmpStock;
        if (_cursor.isNull(_cursorIndexOfStock)) {
          _tmpStock = null;
        } else {
          _tmpStock = _cursor.getInt(_cursorIndexOfStock);
        }
        _result.setStock(_tmpStock);
        final String _tmpCoverUrl;
        if (_cursor.isNull(_cursorIndexOfCoverUrl)) {
          _tmpCoverUrl = null;
        } else {
          _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
        }
        _result.setCoverUrl(_tmpCoverUrl);
        final String _tmpOrigin;
        if (_cursor.isNull(_cursorIndexOfOrigin)) {
          _tmpOrigin = null;
        } else {
          _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
        }
        _result.setOrigin(_tmpOrigin);
        final String _tmpDescription;
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _tmpDescription = null;
        } else {
          _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
        }
        _result.setDescription(_tmpDescription);
        final long _tmpUpdatedAt;
        _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
        _result.setUpdatedAt(_tmpUpdatedAt);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int count() {
    final String _sql = "SELECT COUNT(*) FROM product";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

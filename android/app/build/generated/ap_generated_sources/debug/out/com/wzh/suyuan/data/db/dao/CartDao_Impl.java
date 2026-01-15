package com.wzh.suyuan.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.wzh.suyuan.data.db.DbConverters;
import com.wzh.suyuan.data.db.entity.CartEntity;
import java.lang.Class;
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
public final class CartDao_Impl implements CartDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CartEntity> __insertionAdapterOfCartEntity;

  private final EntityDeletionOrUpdateAdapter<CartEntity> __updateAdapterOfCartEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSyncedByUser;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfMarkSynced;

  public CartDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCartEntity = new EntityInsertionAdapter<CartEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cart_item` (`id`,`user_id`,`product_id`,`quantity`,`selected`,`price_snapshot`,`product_name`,`product_image`,`updated_at`,`synced`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CartEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindLong(3, entity.getProductId());
        statement.bindLong(4, entity.getQuantity());
        final int _tmp = entity.isSelected() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final String _tmp_1 = DbConverters.fromBigDecimal(entity.getPriceSnapshot());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        if (entity.getProductName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getProductName());
        }
        if (entity.getProductImage() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getProductImage());
        }
        statement.bindLong(9, entity.getUpdatedAt());
        final int _tmp_2 = entity.isSynced() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
      }
    };
    this.__updateAdapterOfCartEntity = new EntityDeletionOrUpdateAdapter<CartEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cart_item` SET `id` = ?,`user_id` = ?,`product_id` = ?,`quantity` = ?,`selected` = ?,`price_snapshot` = ?,`product_name` = ?,`product_image` = ?,`updated_at` = ?,`synced` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CartEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindLong(3, entity.getProductId());
        statement.bindLong(4, entity.getQuantity());
        final int _tmp = entity.isSelected() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final String _tmp_1 = DbConverters.fromBigDecimal(entity.getPriceSnapshot());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        if (entity.getProductName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getProductName());
        }
        if (entity.getProductImage() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getProductImage());
        }
        statement.bindLong(9, entity.getUpdatedAt());
        final int _tmp_2 = entity.isSynced() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteSyncedByUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cart_item WHERE user_id = ? AND synced = 1";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cart_item WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE cart_item SET id = ?, synced = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final List<CartEntity> items) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCartEntity.insert(items);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insert(final CartEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfCartEntity.insertAndReturnId(item);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final CartEntity item) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfCartEntity.handle(item);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteSyncedByUser(final long userId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSyncedByUser.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, userId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteSyncedByUser.release(_stmt);
    }
  }

  @Override
  public void deleteById(final long id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteById.release(_stmt);
    }
  }

  @Override
  public void markSynced(final long oldId, final long newId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSynced.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, newId);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, oldId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfMarkSynced.release(_stmt);
    }
  }

  @Override
  public List<CartEntity> getByUser(final long userId) {
    final String _sql = "SELECT * FROM cart_item WHERE user_id = ? ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "product_id");
      final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
      final int _cursorIndexOfSelected = CursorUtil.getColumnIndexOrThrow(_cursor, "selected");
      final int _cursorIndexOfPriceSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "price_snapshot");
      final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "product_name");
      final int _cursorIndexOfProductImage = CursorUtil.getColumnIndexOrThrow(_cursor, "product_image");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
      final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
      final List<CartEntity> _result = new ArrayList<CartEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CartEntity _item;
        _item = new CartEntity();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final long _tmpUserId;
        _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
        _item.setUserId(_tmpUserId);
        final long _tmpProductId;
        _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
        _item.setProductId(_tmpProductId);
        final int _tmpQuantity;
        _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
        _item.setQuantity(_tmpQuantity);
        final boolean _tmpSelected;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfSelected);
        _tmpSelected = _tmp != 0;
        _item.setSelected(_tmpSelected);
        final BigDecimal _tmpPriceSnapshot;
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfPriceSnapshot)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfPriceSnapshot);
        }
        _tmpPriceSnapshot = DbConverters.toBigDecimal(_tmp_1);
        _item.setPriceSnapshot(_tmpPriceSnapshot);
        final String _tmpProductName;
        if (_cursor.isNull(_cursorIndexOfProductName)) {
          _tmpProductName = null;
        } else {
          _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
        }
        _item.setProductName(_tmpProductName);
        final String _tmpProductImage;
        if (_cursor.isNull(_cursorIndexOfProductImage)) {
          _tmpProductImage = null;
        } else {
          _tmpProductImage = _cursor.getString(_cursorIndexOfProductImage);
        }
        _item.setProductImage(_tmpProductImage);
        final long _tmpUpdatedAt;
        _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
        _item.setUpdatedAt(_tmpUpdatedAt);
        final boolean _tmpSynced;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfSynced);
        _tmpSynced = _tmp_2 != 0;
        _item.setSynced(_tmpSynced);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<CartEntity> getPendingByUser(final long userId) {
    final String _sql = "SELECT * FROM cart_item WHERE user_id = ? AND synced = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "user_id");
      final int _cursorIndexOfProductId = CursorUtil.getColumnIndexOrThrow(_cursor, "product_id");
      final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
      final int _cursorIndexOfSelected = CursorUtil.getColumnIndexOrThrow(_cursor, "selected");
      final int _cursorIndexOfPriceSnapshot = CursorUtil.getColumnIndexOrThrow(_cursor, "price_snapshot");
      final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "product_name");
      final int _cursorIndexOfProductImage = CursorUtil.getColumnIndexOrThrow(_cursor, "product_image");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
      final int _cursorIndexOfSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "synced");
      final List<CartEntity> _result = new ArrayList<CartEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CartEntity _item;
        _item = new CartEntity();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final long _tmpUserId;
        _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
        _item.setUserId(_tmpUserId);
        final long _tmpProductId;
        _tmpProductId = _cursor.getLong(_cursorIndexOfProductId);
        _item.setProductId(_tmpProductId);
        final int _tmpQuantity;
        _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
        _item.setQuantity(_tmpQuantity);
        final boolean _tmpSelected;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfSelected);
        _tmpSelected = _tmp != 0;
        _item.setSelected(_tmpSelected);
        final BigDecimal _tmpPriceSnapshot;
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfPriceSnapshot)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfPriceSnapshot);
        }
        _tmpPriceSnapshot = DbConverters.toBigDecimal(_tmp_1);
        _item.setPriceSnapshot(_tmpPriceSnapshot);
        final String _tmpProductName;
        if (_cursor.isNull(_cursorIndexOfProductName)) {
          _tmpProductName = null;
        } else {
          _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
        }
        _item.setProductName(_tmpProductName);
        final String _tmpProductImage;
        if (_cursor.isNull(_cursorIndexOfProductImage)) {
          _tmpProductImage = null;
        } else {
          _tmpProductImage = _cursor.getString(_cursorIndexOfProductImage);
        }
        _item.setProductImage(_tmpProductImage);
        final long _tmpUpdatedAt;
        _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
        _item.setUpdatedAt(_tmpUpdatedAt);
        final boolean _tmpSynced;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfSynced);
        _tmpSynced = _tmp_2 != 0;
        _item.setSynced(_tmpSynced);
        _result.add(_item);
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

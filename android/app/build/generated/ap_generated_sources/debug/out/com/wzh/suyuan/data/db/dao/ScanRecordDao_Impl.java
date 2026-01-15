package com.wzh.suyuan.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.wzh.suyuan.data.db.entity.ScanRecordEntity;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScanRecordDao_Impl implements ScanRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScanRecordEntity> __insertionAdapterOfScanRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public ScanRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScanRecordEntity = new EntityInsertionAdapter<ScanRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `scan_record` (`id`,`trace_code`,`scan_time`,`product_name`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ScanRecordEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getTraceCode() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTraceCode());
        }
        statement.bindLong(3, entity.getScanTime());
        if (entity.getProductName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getProductName());
        }
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM scan_record";
        return _query;
      }
    };
  }

  @Override
  public long insert(final ScanRecordEntity record) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfScanRecordEntity.insertAndReturnId(record);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearAll.release(_stmt);
    }
  }

  @Override
  public List<ScanRecordEntity> getAll() {
    final String _sql = "SELECT * FROM scan_record ORDER BY scan_time DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTraceCode = CursorUtil.getColumnIndexOrThrow(_cursor, "trace_code");
      final int _cursorIndexOfScanTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scan_time");
      final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "product_name");
      final List<ScanRecordEntity> _result = new ArrayList<ScanRecordEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ScanRecordEntity _item;
        _item = new ScanRecordEntity();
        final long _tmpId;
        _tmpId = _cursor.getLong(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpTraceCode;
        if (_cursor.isNull(_cursorIndexOfTraceCode)) {
          _tmpTraceCode = null;
        } else {
          _tmpTraceCode = _cursor.getString(_cursorIndexOfTraceCode);
        }
        _item.setTraceCode(_tmpTraceCode);
        final long _tmpScanTime;
        _tmpScanTime = _cursor.getLong(_cursorIndexOfScanTime);
        _item.setScanTime(_tmpScanTime);
        final String _tmpProductName;
        if (_cursor.isNull(_cursorIndexOfProductName)) {
          _tmpProductName = null;
        } else {
          _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
        }
        _item.setProductName(_tmpProductName);
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

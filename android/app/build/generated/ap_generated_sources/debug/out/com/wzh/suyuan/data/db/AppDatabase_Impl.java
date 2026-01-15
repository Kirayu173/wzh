package com.wzh.suyuan.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.wzh.suyuan.data.db.dao.BootstrapDao;
import com.wzh.suyuan.data.db.dao.BootstrapDao_Impl;
import com.wzh.suyuan.data.db.dao.CartDao;
import com.wzh.suyuan.data.db.dao.CartDao_Impl;
import com.wzh.suyuan.data.db.dao.ProductDao;
import com.wzh.suyuan.data.db.dao.ProductDao_Impl;
import com.wzh.suyuan.data.db.dao.ScanRecordDao;
import com.wzh.suyuan.data.db.dao.ScanRecordDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile BootstrapDao _bootstrapDao;

  private volatile ProductDao _productDao;

  private volatile CartDao _cartDao;

  private volatile ScanRecordDao _scanRecordDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `bootstrap_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `product` (`id` INTEGER NOT NULL, `name` TEXT, `price` TEXT, `stock` INTEGER, `cover_url` TEXT, `origin` TEXT, `description` TEXT, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `cart_item` (`id` INTEGER NOT NULL, `user_id` INTEGER NOT NULL, `product_id` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, `selected` INTEGER NOT NULL, `price_snapshot` TEXT, `product_name` TEXT, `product_image` TEXT, `updated_at` INTEGER NOT NULL, `synced` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `scan_record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `trace_code` TEXT, `scan_time` INTEGER NOT NULL, `product_name` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e037e22a4514c81a84bafc33067e13c8')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `bootstrap_record`");
        db.execSQL("DROP TABLE IF EXISTS `product`");
        db.execSQL("DROP TABLE IF EXISTS `cart_item`");
        db.execSQL("DROP TABLE IF EXISTS `scan_record`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsBootstrapRecord = new HashMap<String, TableInfo.Column>(3);
        _columnsBootstrapRecord.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBootstrapRecord.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBootstrapRecord.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBootstrapRecord = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBootstrapRecord = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBootstrapRecord = new TableInfo("bootstrap_record", _columnsBootstrapRecord, _foreignKeysBootstrapRecord, _indicesBootstrapRecord);
        final TableInfo _existingBootstrapRecord = TableInfo.read(db, "bootstrap_record");
        if (!_infoBootstrapRecord.equals(_existingBootstrapRecord)) {
          return new RoomOpenHelper.ValidationResult(false, "bootstrap_record(com.wzh.suyuan.data.db.entity.BootstrapRecord).\n"
                  + " Expected:\n" + _infoBootstrapRecord + "\n"
                  + " Found:\n" + _existingBootstrapRecord);
        }
        final HashMap<String, TableInfo.Column> _columnsProduct = new HashMap<String, TableInfo.Column>(8);
        _columnsProduct.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduct.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduct.put("price", new TableInfo.Column("price", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduct.put("stock", new TableInfo.Column("stock", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduct.put("cover_url", new TableInfo.Column("cover_url", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduct.put("origin", new TableInfo.Column("origin", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduct.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduct.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProduct = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProduct = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProduct = new TableInfo("product", _columnsProduct, _foreignKeysProduct, _indicesProduct);
        final TableInfo _existingProduct = TableInfo.read(db, "product");
        if (!_infoProduct.equals(_existingProduct)) {
          return new RoomOpenHelper.ValidationResult(false, "product(com.wzh.suyuan.data.db.entity.ProductEntity).\n"
                  + " Expected:\n" + _infoProduct + "\n"
                  + " Found:\n" + _existingProduct);
        }
        final HashMap<String, TableInfo.Column> _columnsCartItem = new HashMap<String, TableInfo.Column>(10);
        _columnsCartItem.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("user_id", new TableInfo.Column("user_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("product_id", new TableInfo.Column("product_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("selected", new TableInfo.Column("selected", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("price_snapshot", new TableInfo.Column("price_snapshot", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("product_name", new TableInfo.Column("product_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("product_image", new TableInfo.Column("product_image", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCartItem.put("synced", new TableInfo.Column("synced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCartItem = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCartItem = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCartItem = new TableInfo("cart_item", _columnsCartItem, _foreignKeysCartItem, _indicesCartItem);
        final TableInfo _existingCartItem = TableInfo.read(db, "cart_item");
        if (!_infoCartItem.equals(_existingCartItem)) {
          return new RoomOpenHelper.ValidationResult(false, "cart_item(com.wzh.suyuan.data.db.entity.CartEntity).\n"
                  + " Expected:\n" + _infoCartItem + "\n"
                  + " Found:\n" + _existingCartItem);
        }
        final HashMap<String, TableInfo.Column> _columnsScanRecord = new HashMap<String, TableInfo.Column>(4);
        _columnsScanRecord.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanRecord.put("trace_code", new TableInfo.Column("trace_code", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanRecord.put("scan_time", new TableInfo.Column("scan_time", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScanRecord.put("product_name", new TableInfo.Column("product_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScanRecord = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScanRecord = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScanRecord = new TableInfo("scan_record", _columnsScanRecord, _foreignKeysScanRecord, _indicesScanRecord);
        final TableInfo _existingScanRecord = TableInfo.read(db, "scan_record");
        if (!_infoScanRecord.equals(_existingScanRecord)) {
          return new RoomOpenHelper.ValidationResult(false, "scan_record(com.wzh.suyuan.data.db.entity.ScanRecordEntity).\n"
                  + " Expected:\n" + _infoScanRecord + "\n"
                  + " Found:\n" + _existingScanRecord);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e037e22a4514c81a84bafc33067e13c8", "59a99a36430ad9f31aedd4ff4b4061b8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "bootstrap_record","product","cart_item","scan_record");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `bootstrap_record`");
      _db.execSQL("DELETE FROM `product`");
      _db.execSQL("DELETE FROM `cart_item`");
      _db.execSQL("DELETE FROM `scan_record`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(BootstrapDao.class, BootstrapDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProductDao.class, ProductDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CartDao.class, CartDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScanRecordDao.class, ScanRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public BootstrapDao bootstrapDao() {
    if (_bootstrapDao != null) {
      return _bootstrapDao;
    } else {
      synchronized(this) {
        if(_bootstrapDao == null) {
          _bootstrapDao = new BootstrapDao_Impl(this);
        }
        return _bootstrapDao;
      }
    }
  }

  @Override
  public ProductDao productDao() {
    if (_productDao != null) {
      return _productDao;
    } else {
      synchronized(this) {
        if(_productDao == null) {
          _productDao = new ProductDao_Impl(this);
        }
        return _productDao;
      }
    }
  }

  @Override
  public CartDao cartDao() {
    if (_cartDao != null) {
      return _cartDao;
    } else {
      synchronized(this) {
        if(_cartDao == null) {
          _cartDao = new CartDao_Impl(this);
        }
        return _cartDao;
      }
    }
  }

  @Override
  public ScanRecordDao scanRecordDao() {
    if (_scanRecordDao != null) {
      return _scanRecordDao;
    } else {
      synchronized(this) {
        if(_scanRecordDao == null) {
          _scanRecordDao = new ScanRecordDao_Impl(this);
        }
        return _scanRecordDao;
      }
    }
  }
}

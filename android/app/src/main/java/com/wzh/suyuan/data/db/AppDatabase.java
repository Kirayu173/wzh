package com.wzh.suyuan.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.wzh.suyuan.data.db.dao.BootstrapDao;
import com.wzh.suyuan.data.db.dao.CartDao;
import com.wzh.suyuan.data.db.dao.ProductDao;
import com.wzh.suyuan.data.db.dao.ScanRecordDao;
import com.wzh.suyuan.data.db.entity.BootstrapRecord;
import com.wzh.suyuan.data.db.entity.CartEntity;
import com.wzh.suyuan.data.db.entity.ProductEntity;
import com.wzh.suyuan.data.db.entity.ScanRecordEntity;

@Database(entities = {BootstrapRecord.class, ProductEntity.class, CartEntity.class, ScanRecordEntity.class},
        version = 4, exportSchema = true)
@TypeConverters(DbConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS product ("
                    + "id INTEGER PRIMARY KEY NOT NULL,"
                    + "name TEXT,"
                    + "price TEXT,"
                    + "stock INTEGER,"
                    + "cover_url TEXT,"
                    + "origin TEXT,"
                    + "description TEXT,"
                    + "updated_at INTEGER NOT NULL"
                    + ")");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS cart_item ("
                    + "id INTEGER PRIMARY KEY NOT NULL,"
                    + "user_id INTEGER NOT NULL,"
                    + "product_id INTEGER NOT NULL,"
                    + "quantity INTEGER NOT NULL,"
                    + "selected INTEGER NOT NULL,"
                    + "price_snapshot TEXT,"
                    + "product_name TEXT,"
                    + "product_image TEXT,"
                    + "updated_at INTEGER NOT NULL,"
                    + "synced INTEGER NOT NULL"
                    + ")");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS scan_record ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "trace_code TEXT,"
                    + "scan_time INTEGER NOT NULL,"
                    + "product_name TEXT"
                    + ")");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "suyuan.db")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract BootstrapDao bootstrapDao();

    public abstract ProductDao productDao();

    public abstract CartDao cartDao();

    public abstract ScanRecordDao scanRecordDao();
}

package com.czy4201b.fastfill.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.czy4201b.fastfill.feature.fastfill.data.db.TableDao
import com.czy4201b.fastfill.feature.fastfill.data.db.TableMeta
import com.czy4201b.fastfill.feature.fastfill.data.db.TableRow

@Database(
    entities = [TableMeta::class, TableRow::class],
    version = 2,
    exportSchema = true
)
abstract class AppDb : RoomDatabase() {
    abstract fun tableDao(): TableDao
}

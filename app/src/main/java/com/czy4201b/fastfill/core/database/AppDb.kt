package com.czy4201b.fastfill.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
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

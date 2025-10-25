package com.czy4201b.fastfill.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.czy4201b.fastfill.feature.fastfill.data.TableDao
import com.czy4201b.fastfill.feature.fastfill.data.TableMeta
import com.czy4201b.fastfill.feature.fastfill.data.TableRow

@Database(
    entities = [TableMeta::class, TableRow::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun tableDao(): TableDao

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        fun get(ctx: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDb::class.java,
                    "app.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
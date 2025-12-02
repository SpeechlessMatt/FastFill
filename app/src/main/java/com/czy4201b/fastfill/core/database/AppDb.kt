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
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun tableDao(): TableDao

    companion object {
        @Volatile
        private var INSTANCE: AppDb? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE table_row ADD COLUMN type TEXT NOT NULL DEFAULT 'text'"
                )
            }
        }

        fun get(ctx: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDb::class.java,
                    "app.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
package com.czy4201b.fastfill.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.czy4201b.fastfill.feature.fastfill.data.db.TableDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDb {
        return Room.databaseBuilder(
            context,
            AppDb::class.java,
            "app.db"
        )
            .addMigrations(
                MIGRATION_1_2
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideTableDao(db: AppDb): TableDao = db.tableDao()
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE table_row ADD COLUMN type TEXT NOT NULL DEFAULT 'text'"
        )
    }
}

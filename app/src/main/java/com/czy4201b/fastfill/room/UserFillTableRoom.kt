package com.czy4201b.fastfill.room

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Entity(tableName = "table_meta")
data class TableMeta(
    @PrimaryKey
    @ColumnInfo(name = "table_id")
    val tableId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    val name: String = "新建表格",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    init {
        require(name.isNotBlank()) { "表格名称不能为空" }
    }
}

@Entity(
    tableName = "table_row",
    foreignKeys = [ForeignKey(
        entity = TableMeta::class,
        parentColumns = ["table_id"],
        childColumns = ["table_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("table_id"),
        Index(value = ["table_id", "row_key"], unique = true) // 防止重复键
    ]
)

data class TableRow(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "table_id")
    val tableId: String,

    @ColumnInfo(name = "row_key")
    val key: String = "",

    @ColumnInfo(name = "value")
    val value: String = "",

    @ColumnInfo(name = "index")
    val index: Int = 0
) {
    init {
        require(tableId.isNotBlank()) { "tableId 不能为空" }
    }
}

@Dao
interface TableDao {
    /* -------- meta -------- */
    @Query("SELECT * FROM table_meta ORDER BY created_at DESC")
    fun observeAllMeta(): Flow<List<TableMeta>>

    @Query("SELECT * FROM table_meta WHERE table_id = :tableId")
    suspend fun getMetaById(tableId: String): TableMeta?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeta(meta: TableMeta)

    @Query("DELETE FROM table_meta WHERE table_id = :tableId")
    suspend fun deleteMeta(tableId: String)

    @Query("UPDATE table_meta SET name = :newName, updated_at = :timestamp WHERE table_id = :tableId")
    suspend fun updateTableName(tableId: String, newName: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE table_meta SET updated_at = :timestamp WHERE table_id = :tableId")
    suspend fun updateMetaTimestamp(tableId: String, timestamp: Long = System.currentTimeMillis())

    /* -------- 查询最近表格 -------- */
    @Query("SELECT * FROM table_meta ORDER BY updated_at DESC LIMIT 1")
    suspend fun getMostRecentTable(): TableMeta?

    /* -------- row -------- */
    @Query("SELECT * FROM table_row WHERE table_id = :tableId ORDER BY `index` ASC")
    fun observeRows(tableId: String): Flow<List<TableRow>>

    @Query("SELECT * FROM table_row WHERE table_id = :tableId AND row_key = :key")
    suspend fun getRowByKey(tableId: String, key: String): TableRow?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRow(row: TableRow)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRows(list: List<TableRow>)

    @Query("DELETE FROM table_row WHERE table_id = :tableId")
    suspend fun deleteRows(tableId: String)

    @Query("DELETE FROM table_row WHERE id = :rowId")
    suspend fun deleteRow(rowId: String)

    @Update
    suspend fun updateRow(row: TableRow)

    /* -------- 事务操作 -------- */
    @Transaction
    suspend fun saveTableWithTimestamp(tableId: String, rows: List<TableRow>) {
        deleteRows(tableId)
        insertRows(rows)
        updateMetaTimestamp(tableId)
    }

    @Transaction
    suspend fun deleteTableWithRows(tableId: String) {
        deleteRows(tableId)
        deleteMeta(tableId)
    }

    @Transaction
    @Query("SELECT * FROM table_meta WHERE table_id = :tableId")
    suspend fun getTableWithRows(tableId: String): TableWithRows?
}

data class TableWithRows(
    @Embedded val meta: TableMeta,
    @Relation(
        parentColumn = "table_id",
        entityColumn = "table_id"
    )
    val rows: List<TableRow>
)
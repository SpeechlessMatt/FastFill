package com.czy4201b.fastfill.feature.fastfill.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.czy4201b.fastfill.FastFillApplication
import com.czy4201b.fastfill.core.database.AppDb
import com.czy4201b.fastfill.feature.fastfill.data.TableDao
import com.czy4201b.fastfill.feature.fastfill.data.TableMeta
import com.czy4201b.fastfill.feature.fastfill.data.TableRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserFillTableViewModel(
    private val dao: TableDao = AppDb.Companion.get(FastFillApplication.Companion.instance).tableDao()
) : ViewModel() {

    // 添加互斥锁，目的很简单防止不应该的并发造成数据库错乱
    private val tableMutex = Mutex()

    /* ---------- 全部表格 ---------- */
    val allTables: StateFlow<List<TableMeta>> =
        dao.observeAllMeta()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _currentMeta = MutableStateFlow<TableMeta?>(null)
    val currentMeta: StateFlow<TableMeta?> = _currentMeta

    val otherTables: StateFlow<List<TableMeta>> =
        allTables          // 使用allTables而不是再次调用dao
            .combine(currentMeta) { list, cur ->
                list.filterNot { it.tableId == cur?.tableId }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // 加载状态
//    private val _tableLoadingState = MutableStateFlow<DataLoadingState>(DataLoadingState.Idle)
//    val tableLoadingState: StateFlow<DataLoadingState> = _tableLoadingState.asStateFlow()

    // 定义ui状态
    private val _state = MutableStateFlow(UserFillViewUiState())
    val state: StateFlow<UserFillViewUiState> = _state.asStateFlow()

    private var _nextRowIndex by mutableIntStateOf(0)
    private var _nextTableIndex = 1

    init {
        Log.d("Database", "init Database")
        viewModelScope.launch {
            // 使用专门的查询方法 获取最近编辑的表格
            val recentTable = dao.getMostRecentTable()
            recentTable?.let {
                loadTableData(it.tableId)
                Log.d("Database", "Load Recent Data: ${it.tableId}")
                return@launch
            }
            createDefaultTableAndLoad()
        }
    }

    private suspend fun createDefaultTableAndLoad(): TableMeta = tableMutex.withLock {
        val defaultMeta = TableMeta(name = "新建表格${_nextTableIndex++}")
        dao.insertMeta(defaultMeta)          // 插入新表格
        Log.d("Database", "createDefaultTable and Load")
        loadTableData(defaultMeta.tableId)   // 立即加载
        return defaultMeta
    }

    fun changeTableName(newName: String) {
        viewModelScope.launch {
            tableMutex.withLock {
                val currentTableId = _currentMeta.value?.tableId ?: return@launch

                // 冗余但是不删，数据库处理要严格
                // 验证名称不能为空
                if (newName.isBlank()) {
                    return@launch
                }

                // 更新数据库
                dao.updateTableName(currentTableId, newName)
                // 但为了立即响应，我们可以手动更新
                _currentMeta.value = _currentMeta.value?.copy(name = newName)
            }
        }
    }

    /* ---------- 手动加载表格数据 ---------- */
    private suspend fun loadTableData(tableId: String) {
        try {

            dao.getMetaById(tableId)?.let { meta ->
                // 更新current meta状态
                _currentMeta.value = meta
                Log.d("Database", "Load meta: $meta")
                Log.d("Database", "Load meta name: ${_currentMeta.value?.name}")

                // 加载表格行数据到 userFillTable
                val rows = dao.observeRows(tableId).first()  // 使用.first()来获取当前时刻的数据。
                _nextRowIndex = rows.maxByOrNull { it.index }?.index?: 0

                _state.update { state ->
                    state.copy(
                        inputTableName = meta.name,
                        userFillTable = rows
                    )
                }
                Log.d("Database", "Load Data: $tableId")

//                _operationState.value = OperationState.Success("表格加载成功")
            }
        } catch (e: Exception) {
            Log.d("Database", "load data error: $e")
        }

    }

    private fun saveCurrentTable() {
        viewModelScope.launch {
            val tableId = _currentMeta.value?.tableId ?: run {
                return@launch
            }

            try {
                val userFillTable = _state.value.userFillTable
                // 保存到数据库
                dao.saveTableWithTimestamp(tableId, userFillTable)

            } catch (e: Exception) {
                Log.d("Database", "load data error: $e")
            }
        }
    }

    /** 删除表及其行 */
    fun deleteTable(meta: TableMeta) {
        viewModelScope.launch {
            dao.deleteMeta(meta.tableId)
            // 如果删的不是现在正在展示的表格，那么就不用管后面
            if (_currentMeta.value?.tableId != meta.tableId) return@launch

            // 看看最近编辑的表格，就用最近编辑的顶上
            val recentTable = dao.getMostRecentTable()
            // 如果有的话那就顶上
            recentTable?.let {
                _currentMeta.value = it
                return@launch
            }
            // 没有就生成create默认了
            createDefaultTableAndLoad()
        }
    }

    /* ui处理部分 */
    fun addRow() {
        val tableId = _currentMeta.value?.tableId ?: return
        _state.update { state ->
            val userFillTable = state.userFillTable
            state.copy(
                userFillTable = userFillTable + TableRow(tableId = tableId, index = _nextRowIndex++)
            )
        }
    }

    fun removeRow(id: String) {
        _state.update { state ->
            val userFillTable = state.userFillTable
            state.copy(
                userFillTable = userFillTable.filter { it.id != id }
            )
        }
    }

    fun updateTableRow(id: String, key: String? = null, value: String? = null) {
        _state.update { state ->
            val userFillTable = state.userFillTable
            state.copy(
                userFillTable = userFillTable.map { row ->
                    if (row.id == id)
                        row.copy(key = key ?: row.key, value = value ?: row.value)
                    else
                        row
                }
            )
        }
    }

    val userFillMap: Map<String, String>
        get() = state.value.userFillTable.associate { it.key to it.value }

    val userFillMapSize: Int
        get() = state.value.userFillTable.size

    fun showEditView() {
        _state.update { state ->
            state.copy(
                isShowEditView = true
            )
        }
    }

    fun closeEditView() {
        _state.update { state ->
            state.copy(
                isShowEditView = false,
                inputTableName = _currentMeta.value?.name ?: "新建表格"
            )
        }
    }

    fun saveAll() {
        changeTableName(_state.value.inputTableName)
        _state.update { state ->
            state.copy(
                isShowEditView = false,
            )
        }
        saveCurrentTable()
    }

    fun updateTableNameText(name: String) {
        _state.update { state ->
            state.copy(
                inputTableName = name
            )
        }
    }

    fun setPickerShow(isShow: Boolean) {
        _state.update { state ->
            state.copy(
                isShowPicker = isShow
            )
        }
    }

    fun selectTable(tableId: String) {
        viewModelScope.launch {
            loadTableData(tableId)
        }
    }

    fun addTable() {
        // 这里应该询问是否保存,暂时不做，通过这个先保存
        changeTableName(_state.value.inputTableName)
        saveCurrentTable()
        viewModelScope.launch {
            createDefaultTableAndLoad()
        }
    }

//    override fun onCleared() {
//        super.onCleared()
//        // 确保退出前保存
//        viewModelScope.launch {
//            saveUsers(users.value)
//        }
//    }

}
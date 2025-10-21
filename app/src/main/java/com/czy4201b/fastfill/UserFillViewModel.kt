package com.czy4201b.fastfill

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.czy4201b.fastfill.room.AppDb
import com.czy4201b.fastfill.room.TableDao
import com.czy4201b.fastfill.room.TableMeta
import com.czy4201b.fastfill.room.TableRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class UserFillViewUiState(
    val isShowEditView: Boolean = false,
    val isShowPicker: Boolean = false,
    val inputTableName: String = "",
    val isVertical: Boolean = false
)

// 数据加载状态
sealed class DataLoadingState {
    object Idle : DataLoadingState()
    object Loading : DataLoadingState()
    object Saving : DataLoadingState()
    data class Success(val itemCount: Int) : DataLoadingState()
    data class Error(val message: String) : DataLoadingState()
}

class UserFillViewModel(
    private val dao: TableDao = AppDb.get(App.instance).tableDao()
) : ViewModel() {

    /* ---------- 全部表格 ---------- */
    // 等待改动
    val allTables: StateFlow<List<TableMeta>> =
        dao.observeAllMeta()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _currentMeta = MutableStateFlow<TableMeta?>(null)
    val currentMeta: StateFlow<TableMeta?> = _currentMeta

    val otherTables: StateFlow<List<TableMeta>> =
        dao.observeAllMeta()          // ① Room 数据
            .combine(currentMeta) { list, cur ->  // ② 组合当前表
                list.filterNot { it.tableId == cur?.tableId }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _userFillTable = mutableStateListOf<TableRow>()

    val userFillMap: Map<String, String>
        get() = _userFillTable.associate { it.key to it.value }

    val userFillMapSize: Int get() = _userFillTable.size

    // 加载状态
    private val _tableLoadingState = MutableStateFlow<DataLoadingState>(DataLoadingState.Idle)
    val tableLoadingState: StateFlow<DataLoadingState> = _tableLoadingState.asStateFlow()

    // 定义ui状态
    private val _state = MutableStateFlow(UserFillViewUiState())
    val state: StateFlow<UserFillViewUiState> = _state.asStateFlow()

    private var nextIndex = 0

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

    private suspend fun createDefaultTableAndLoad(): TableMeta {
        val defaultMeta = TableMeta(name = "新建表格")
        dao.insertMeta(defaultMeta)
        Log.d("Database", "createDefaultTable and Load")
        loadTableData(defaultMeta.tableId)
        return defaultMeta
    }

    fun changeTableName(newName: String) {
        viewModelScope.launch {
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

    /* ---------- 手动加载表格数据 ---------- */
    private suspend fun loadTableData(tableId: String) {
        try {
//                _operationState.value = OperationState.Loading("正在加载表格数据...")
            // 加载表格元数据
            dao.getMetaById(tableId)?.let { meta ->
                // 更新current meta状态
                _currentMeta.value = meta
                Log.d("Database", "Load meta: $meta")
                Log.d("Database", "Load meta name: ${_currentMeta.value?.name}")
                _state.update { state ->
                    state.copy(
                        inputTableName = meta.name
                    )
                }
                // 加载表格行数据到 userFillTable
                val rows = dao.observeRows(tableId).first()  // 使用.first()来获取当前时刻的数据。
                _userFillTable.clear()
                _userFillTable.addAll(rows)
                Log.d("Database", "Load Data: $tableId")

//                _operationState.value = OperationState.Success("表格加载成功")
            }
        } catch (e: Exception) {
//                _operationState.value = OperationState.Error("加载失败: ${e.message}")
        }

    }

    private fun saveCurrentTable() {
        viewModelScope.launch {
            val tableId = _currentMeta.value?.tableId ?: run {
//                _operationState.value = OperationState.Error("没有选中的表格")
                return@launch
            }

            try {
//                _operationState.value = OperationState.Loading("正在保存表格...")

                // 保存到数据库
                dao.saveTableWithTimestamp(tableId, _userFillTable.toList())

//                _operationState.value = OperationState.Success("表格保存成功")

            } catch (e: Exception) {
//                _operationState.value = OperationState.Error("保存失败: ${e.message}")
            }
        }
    }

    /** 删除表及其行 */
     fun deleteTable(meta: TableMeta) {
        viewModelScope.launch {
            dao.deleteMeta(meta.tableId)
            if (_currentMeta.value?.tableId != meta.tableId) return@launch

            val recentTable = dao.getMostRecentTable()
            recentTable?.let {
                _currentMeta.value = it
                return@launch
            }
            createDefaultTableAndLoad()
        }
    }

    /* ui处理部分 */
    fun addRow() {
        val tableId = _currentMeta.value?.tableId ?: return
        _userFillTable += TableRow(
            tableId = tableId,
            index = nextIndex++
        )
    }

    fun removeRow(id: String) {
        _userFillTable.removeAll { it.id == id }
    }

    fun updateTableRow(id: String, key: String? = null, value: String? = null) {
        val idx = _userFillTable.indexOfFirst { it.id == id }
        if (idx == -1) return
        _userFillTable[idx] = _userFillTable[idx].run {
            copy(
                key = key ?: this.key,
                value = value ?: this.value
            )
        }
    }

    val sortedRows: List<TableRow>
        get() = _userFillTable.sortedBy { it.index }

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

    fun selectTable(tableId: String){
        viewModelScope.launch {
            loadTableData(tableId)
        }
    }

    fun addTable(){
        viewModelScope.launch {
            changeTableName(_state.value.inputTableName)
            createDefaultTableAndLoad()
        }
    }

}
package com.czy4201b.fastfill.feature.fastfill.ui

import com.czy4201b.fastfill.feature.fastfill.data.TableMeta
import com.czy4201b.fastfill.feature.fastfill.data.TableRow

data class UserFillViewUiState(
    val isShowEditView: Boolean = false,
    val isShowPicker: Boolean = false,
    val inputTableName: String = "",
    val isVertical: Boolean = false,
    val userFillTable: List<TableRow> = emptyList(),
    val tableList: List<TableMeta> = emptyList()
)
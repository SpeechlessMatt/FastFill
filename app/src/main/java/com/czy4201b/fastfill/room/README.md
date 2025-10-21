observeAllMeta()	无	Flow<List<TableMeta>>	监听所有表格列表变化（实时更新）
getMetaById()	tableId: String	TableMeta?	查询单个表格的元数据
insertMeta()	meta: TableMeta	无	插入/替换表格元数据
deleteMeta()	tableId: String	无	删除指定表格的元数据
updateMeta()	meta: TableMeta	无	更新表格元数据
updateMetaTimestamp()	tableId, timestamp	无	更新表格的修改时间

observeRows()	tableId: String	Flow<List<TableRow>>	监听指定表格的所有行数据变化
getRowByKey()	tableId, key	TableRow?	查询指定键名的行数据
insertRow()	row: TableRow	无	插入/替换单行数据
insertRows()	list: List<TableRow>	无	插入/替换多行数据
deleteRows()	tableId: String	无	删除指定表格的所有行
deleteRow()	rowId: String	无	删除单行数据
updateRow()	row: TableRow	无	更新行数据

saveTableWithTimestamp()	tableId, rows	无	保存整张表：删除旧行 → 插入新行 → 更新时间戳
deleteTableWithRows()	tableId: String	无	删除整张表：删除行数据 → 删除元数据
getTableWithRows()	tableId: String	TableWithRows?	获取完整表格：元数据 + 所有行数据

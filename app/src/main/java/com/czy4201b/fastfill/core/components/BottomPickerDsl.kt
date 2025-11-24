package com.czy4201b.fastfill.core.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.math.abs

data class BottomPickerProperties(
    val pickerHeight: Dp = 120.dp,
    val pickerTitleTextStyle: TextStyle = TextStyle(),
    val pickerPadding: PaddingValues = PaddingValues(all = 10.dp),
    val pickerTitlePadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    val pickerContentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    val pickerVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    val pickerHorizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    val pickerBarHeight: Dp = 20.dp,
    val pickerBarColor: Color = Color.Black.copy(alpha = 0.08f),
    val pickerBarShape: Shape = RoundedCornerShape(4.dp),
)

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class BottomPickerDsl

@BottomPickerDsl
class BottomPickerScope<T>(
    private val properties: BottomPickerProperties
) {
    val columns = mutableListOf<Pair<@Composable () -> Unit, String>>()

    // 每列的当前选中值
    internal val selectedValues = mutableStateListOf<T?>()

    internal val resultValues: List<T>
        get() {
            selectedValues.filterNotNull().let { list ->
                return list
            }
        }

// 这种方式确实很好玩，但是暂时不需要呢
//    private var onResult: ((List<T>) -> Unit)? = null
//    fun onResult(callback: (List<T>) -> Unit) {
//        this.onResult = callback
//    }
//
//    internal fun dispatchResult() {
//        selectedValues.filterNotNull().let { list ->
//            if (list.size == selectedValues.size) {
//                onResult?.invoke(list)
//            }
//        }
//    }

    fun picker(
        state: LazyListState? = null,
        title: String = "",
        block: BottomPickerItemScope<T>.() -> Unit
    ) {
        val scope = BottomPickerItemScope<T>().apply(block)
        val realList = scope.contentList
        val realSize = realList.size
        if (realSize == 0) return

        // 给这一列占一个位置
        val colIndex = selectedValues.size
        selectedValues.add(null)

        columns += Pair({
            val loopCount = 10 // 理论上够你滚很久
            val totalCount = realSize * loopCount
            // 外部没给就 remember 一个默认的
            val columnState = state ?: rememberLazyListState(
                initialFirstVisibleItemIndex = realSize * loopCount / 2
            )
            val flingBehavior = rememberSnapFlingBehavior(columnState)

            // 保证初始的情况下，数字在中间哦
            LaunchedEffect(Unit) {
                snapshotFlow { columnState.layoutInfo.visibleItemsInfo }
                    .first { it.isNotEmpty() }

                val itemSize = columnState.layoutInfo.visibleItemsInfo[0].size
                val centerOffset = columnState.layoutInfo.viewportSize.height / 2 - itemSize / 2

                columnState.animateScrollToItem(
                    index = columnState.firstVisibleItemIndex,
                    scrollOffset = -centerOffset
                )
            }

            val centerGlobal by remember {
                derivedStateOf {
                    val layoutInfo = columnState.layoutInfo
                    val viewportCenter =
                        layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2

                    // 更精确的方法：找到距离视口中心最近的项
                    layoutInfo.visibleItemsInfo.minByOrNull { item ->
                        val itemCenter = item.offset + item.size / 2
                        abs(itemCenter - viewportCenter)
                    }?.index ?: 0
                }
            }

            LaunchedEffect(Unit) {
                snapshotFlow { centerGlobal }
                    .map { globalIndex ->
                        (globalIndex % realSize + realSize) % realSize
                    }
                    .distinctUntilChanged() // 去重，只触发真实变化
                    .collect { index ->
                        val value = realList[index].second
                        selectedValues[colIndex] = value
//                        dispatchResult()
                    }
            }

            LazyColumn(
                state = columnState,
                flingBehavior = flingBehavior
            ) {
                items(
                    count = totalCount,
                    key  = { it }
                ) { globalIndex ->
                    val actualIndex = globalIndex % realSize
                    val isCenter = (globalIndex == centerGlobal)
                    val contentAlpha = if (isCenter) 1f else 0.5f
                    Box(
                        modifier = Modifier
                            .alpha(contentAlpha),
                        contentAlignment = Alignment.Center
                    ) {
                        realList[actualIndex].first()
                    }
                }
            }
        }, title)
    }

    @Composable
    fun Build() {
        Box(
            modifier = Modifier.padding(properties.pickerPadding),
        ) {
            Column {
                // title
                Row(
                    modifier = Modifier.fillMaxWidth().padding(properties.pickerTitlePadding),
                    verticalAlignment = properties.pickerVerticalAlignment,
                    horizontalArrangement = properties.pickerHorizontalArrangement
                ) {
                    columns.forEach { picker ->
                        Text(
                            text = picker.second,
                            modifier = Modifier.weight(1f),
                            style = properties.pickerTitleTextStyle
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // picker
                    Row(
                        modifier = Modifier
                            .height(properties.pickerHeight)
                            .fillMaxWidth()
                            .padding(properties.pickerContentPadding),
                        verticalAlignment = properties.pickerVerticalAlignment,
                        horizontalArrangement = properties.pickerHorizontalArrangement
                    ) {
                        columns.forEach { picker ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                picker.first()
                            }
                        }
                    }

                    // bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(properties.pickerBarHeight)
                            .clip(properties.pickerBarShape)
                            .background(color = properties.pickerBarColor)
                    )
                }
            }
        }
    }
}

@BottomPickerDsl
class BottomPickerItemScope<T> {
    val contentList = mutableListOf<Pair<@Composable () -> Unit, T?>>()

    inline fun item(crossinline content: @Composable () -> Unit) {
        contentList += Pair({ content() }, null)
    }

    // 带值的 item
    inline fun item(value: T, crossinline content: @Composable () -> Unit) {
        contentList += Pair({ content() }, value)
    }

    inline fun items(items: Iterable<T>, crossinline content: @Composable (T) -> Unit) {
        items.forEach { item ->
            contentList += Pair({ content(item) }, item)
        }
    }

    inline fun items(
        vararg items: T,
        crossinline content: @Composable (T) -> Unit
    ) {
        items.forEach { item ->
            contentList += Pair({ content(item) }, item)
        }
    }

    inline fun itemsIndexed(
        items: Iterable<T>,
        crossinline content: @Composable (Int, T) -> Unit
    ) {
        items.forEachIndexed { index, item ->
            contentList += Pair({ content(index, item) }, item)
        }
    }

    inline fun itemsIndexed(
        vararg items: T,
        crossinline content: @Composable (Int, T) -> Unit
    ) {
        items.forEachIndexed { index, item ->
            contentList += Pair({ content(index, item) }, item)
        }
    }
}
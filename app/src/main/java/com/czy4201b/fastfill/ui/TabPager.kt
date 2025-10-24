package com.czy4201b.fastfill.ui

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun TabPager(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val saveableStateHolder = rememberSaveableStateHolder()

    // 吸附行为（滑动到整页）
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(currentPage) {
        coroutineScope.launch {
            listState.animateScrollToItem(currentPage)
        }
    }

    LazyRow(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = modifier,
        userScrollEnabled = false
    ) {
        items(pageCount) { index ->
            Box(Modifier.fillParentMaxWidth()) {
                saveableStateHolder.SaveableStateProvider(key = "page_$index") {
                    content(index)
                }
            }
        }
    }
}

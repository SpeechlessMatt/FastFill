package com.czy4201b.fastfill.core.navigation

sealed class Route(val route: String) {
    object Main : Route("main")
    object UpdateDialog : Route("update_dialog")
}
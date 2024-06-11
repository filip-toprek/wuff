package com.filiptoprek.wuff.domain.model.core

import androidx.compose.ui.graphics.vector.ImageVector
import com.filiptoprek.wuff.navigation.Routes

data class BottomNavigationItem (
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val route: String
)
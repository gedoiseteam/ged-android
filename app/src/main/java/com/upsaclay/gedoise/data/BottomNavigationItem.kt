package com.upsaclay.gedoise.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.gedoise.R

sealed class BottomNavigationItem(
    open val screen: Screen,
    @StringRes open val label: Int,
    open var badges: Int,
    open var hasNews: Boolean,
    @DrawableRes open val filledIcon: Int,
    @DrawableRes open val outlinedIcon: Int,
    @StringRes open val iconDescription: Int
) {
    data class Home(
        override val screen: Screen = Screen.NEWS,
        override val label: Int = R.string.home,
        override var badges: Int = 0,
        override var hasNews: Boolean = false,
        override val filledIcon: Int = com.upsaclay.common.R.drawable.ic_fill_home,
        override val outlinedIcon: Int = com.upsaclay.common.R.drawable.ic_outline_home,
        override val iconDescription: Int = R.string.home_icon_description
    ) : BottomNavigationItem(screen, label, badges, hasNews, filledIcon, outlinedIcon, iconDescription)

    data class Message(
        override val screen: Screen = Screen.CONVERSATION,
        override val label: Int = R.string.messages,
        override var badges: Int = 0,
        override var hasNews: Boolean = false,
        override val filledIcon: Int = com.upsaclay.common.R.drawable.ic_fill_message,
        override val outlinedIcon: Int = com.upsaclay.common.R.drawable.ic_outline_message,
        override val iconDescription: Int = R.string.message_icon_description
    ) : BottomNavigationItem(screen, label, badges, hasNews, filledIcon, outlinedIcon, iconDescription)

    data class Calendar(
        override val screen: Screen = Screen.CALENDAR,
        override val label: Int = R.string.calendar,
        override var badges: Int = 0,
        override var hasNews: Boolean = false,
        override val filledIcon: Int = com.upsaclay.common.R.drawable.ic_fill_calendar,
        override val outlinedIcon: Int = com.upsaclay.common.R.drawable.ic_outline_calendar,
        override val iconDescription: Int = R.string.calendar_icon_description
    ) : BottomNavigationItem(screen, label, badges, hasNews, filledIcon, outlinedIcon, iconDescription)

    data class Forum(
        override val screen: Screen = Screen.FORUM,
        override val label: Int = R.string.forum,
        override var badges: Int = 0,
        override var hasNews: Boolean = false,
        override val filledIcon: Int = com.upsaclay.common.R.drawable.ic_fill_forum,
        override val outlinedIcon: Int = com.upsaclay.common.R.drawable.ic_outline_forum,
        override val iconDescription: Int = R.string.forum_icon_description
    ) : BottomNavigationItem(screen, label, badges, hasNews, filledIcon, outlinedIcon, iconDescription)
}
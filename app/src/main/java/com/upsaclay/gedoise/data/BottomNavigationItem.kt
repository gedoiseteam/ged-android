package com.upsaclay.gedoise.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.upsaclay.common.domain.model.Screen
import com.upsaclay.gedoise.R

sealed class BottomNavigationItem(
    open val screen: Screen,
    @StringRes open val label: Int,
    open var badges: Int,
    open var hasNews: Boolean,
    @DrawableRes open val icon: Int,
    @StringRes open val iconDescription: Int
) {
    data class Home(
        override val screen: Screen = Screen.NEWS,
        override val label: Int = R.string.home,
        override var badges: Int = 0,
        override var hasNews: Boolean = false,
        override val icon: Int = com.upsaclay.common.R.drawable.ic_home,
        override val iconDescription: Int = R.string.home_icon_description
    ) : BottomNavigationItem(screen, label, badges, hasNews, icon, iconDescription)

    data class Message(
        override val screen: Screen = Screen.CONVERSATIONS,
        override val label: Int = R.string.messages,
        override var badges: Int = 0,
        override var hasNews: Boolean = false,
        override val icon: Int = com.upsaclay.common.R.drawable.ic_mail,
        override val iconDescription: Int = R.string.message_icon_description
    ) : BottomNavigationItem(screen, label, badges, hasNews, icon, iconDescription)

    data class Calendar(
        override val screen: Screen = Screen.CALENDAR,
        override val label: Int = R.string.calendar,
        override var badges: Int = 0,
        override var hasNews: Boolean = false,
        override val icon: Int = com.upsaclay.common.R.drawable.ic_calendar,
        override val iconDescription: Int = R.string.calendar_icon_description
    ) : BottomNavigationItem(screen, label, badges, hasNews, icon, iconDescription)

    data class Forum(
        override val screen: Screen = Screen.FORUM,
        override val label: Int = R.string.forum,
        override var badges: Int = 0,
        override var hasNews: Boolean = false,
        override val icon: Int = com.upsaclay.common.R.drawable.ic_forum,
        override val iconDescription: Int = R.string.forum_icon_description
    ) : BottomNavigationItem(screen, label, badges, hasNews, icon, iconDescription)
}
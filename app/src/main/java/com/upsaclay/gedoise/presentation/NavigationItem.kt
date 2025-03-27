package com.upsaclay.gedoise.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.upsaclay.common.domain.entity.Screen
import com.upsaclay.gedoise.R
import com.upsaclay.message.domain.entity.MessageScreen
import com.upsaclay.news.domain.entity.NewsScreen

sealed class NavigationItem(
    open val badges: Int,
    open val hasNews: Boolean
) {
    abstract val screen: Screen
    abstract val label: Int
    abstract val filledIcon: Int
    abstract val outlinedIcon: Int
    abstract val iconDescription: Int

    data class Home(
        override val badges: Int = 0,
        override val hasNews: Boolean = false
    ): NavigationItem(badges, hasNews) {
        override val screen: NewsScreen = NewsScreen.News
        @StringRes override val label: Int = R.string.home
        @DrawableRes override val filledIcon: Int = com.upsaclay.common.R.drawable.ic_fill_home
        @DrawableRes override val outlinedIcon: Int = com.upsaclay.common.R.drawable.ic_outline_home
        @StringRes override val iconDescription: Int = R.string.home_icon_description
    }

    data class Message(
        override val badges: Int = 0,
        override val hasNews: Boolean = false
    ): NavigationItem(badges, hasNews) {
        override val screen: Screen = MessageScreen.Conversation
        @StringRes override val label: Int = R.string.messages
        @DrawableRes override val filledIcon: Int = com.upsaclay.common.R.drawable.ic_fill_message
        @DrawableRes override val outlinedIcon: Int = com.upsaclay.common.R.drawable.ic_outline_message
        @StringRes override val iconDescription: Int = R.string.message_icon_description
    }
//
//    data class Calendar(
//        override val badges: Int = 0,
//        override val hasNews: Boolean = false
//    ): NavigationItem(badges, hasNews) {
//        override val screen: Screen = Screen.CALENDAR
//        @StringRes override val label: Int = R.string.calendar
//        @DrawableRes override val filledIcon: Int = com.upsaclay.common.R.drawable.ic_fill_calendar
//        @DrawableRes override val outlinedIcon: Int = com.upsaclay.common.R.drawable.ic_outline_calendar
//        @StringRes override val iconDescription: Int = R.string.calendar_icon_description
//    }
//
//    data class Forum(
//        override val badges: Int = 0,
//        override val hasNews: Boolean = false
//    ): NavigationItem(badges, hasNews) {
//        override val screen: Screen = Screen.FORUM
//        @StringRes override val label: Int = R.string.forum
//        @DrawableRes override val filledIcon: Int = com.upsaclay.common.R.drawable.ic_fill_forum
//        @DrawableRes override val outlinedIcon: Int = com.upsaclay.common.R.drawable.ic_outline_forum
//        @StringRes override val iconDescription: Int = R.string.forum_icon_description
//    }
}
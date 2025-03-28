package com.upsaclay.news.domain.entity

import com.upsaclay.common.domain.entity.ScreenRoute

private const val NEWS_ROUTE = "news_screen"
private const val READ_ANNOUNCEMENT_ROUTE = "read_announcement_screen"
private const val EDIT_ANNOUNCEMENT_ROUTE = "edit_announcement_screen"
private const val CREATE_ANNOUNCEMENT_ROUTE = "create_announcement_screen"

sealed class NewsScreenRoute: ScreenRoute {
    data object News: NewsScreenRoute() {
        override val route: String = NEWS_ROUTE
    }

    data class ReadAnnouncement(val announcementId: String): NewsScreenRoute() {
        override val route: String = "$READ_ANNOUNCEMENT_ROUTE?announcementId=$announcementId"
        companion object {
            const val HARD_ROUTE = "$READ_ANNOUNCEMENT_ROUTE?announcementId={announcementId}"
        }
    }

    data class EditAnnouncement(val announcementId: String): NewsScreenRoute() {
        override val route: String = "$EDIT_ANNOUNCEMENT_ROUTE?announcementId=$announcementId"
        companion object {
            const val HARD_ROUTE = "$EDIT_ANNOUNCEMENT_ROUTE?announcementId={announcementId}"
        }
    }

    data object CreateAnnouncement: NewsScreenRoute() {
        override val route: String = CREATE_ANNOUNCEMENT_ROUTE
    }
}
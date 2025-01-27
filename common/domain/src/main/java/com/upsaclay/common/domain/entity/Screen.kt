package com.upsaclay.common.domain.entity

enum class Screen(val route: String) {
    AUTHENTICATION("authentication_screen"),
    FIRST_REGISTRATION("first_registration_screen"),
    SECOND_REGISTRATION("second_registration_screen"),
    THIRD_REGISTRATION("third_registration_screen"),
    EMAIL_VERIFICATION("email_verification_screen"),
    NEWS("news_screen"),
    READ_ANNOUNCEMENT("read_announcement_screen"),
    EDIT_ANNOUNCEMENT("edit_announcement_screen"),
    CREATE_ANNOUNCEMENT("create_announcement_screen"),
    CHAT("chat_screen"),
    CONVERSATIONS("conversations_screen"),
    CREATE_CONVERSATION("create_conversations_screen"),
    CALENDAR("calendar_screen"),
    FORUM("forum_screen"),
    PROFILE("profile_screen"),
    ACCOUNT("account_screen"),
    SPLASH("splash_screen")
}
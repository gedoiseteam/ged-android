package com.upsaclay.news.domain.entity

enum class AnnouncementScreenState {
    DEFAULT,
    LOADING,
    CREATED,
    DELETED,
    UPDATED,
    CREATION_ERROR,
    UPDATE_ERROR,
    CONNECTION_ERROR,
    ERROR
}
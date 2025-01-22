package com.upsaclay.gedoise.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upsaclay.message.data.local.dao.ConversationDao
import com.upsaclay.message.data.local.dao.MessageDao
import com.upsaclay.message.data.local.model.LocalConversation
import com.upsaclay.message.data.local.model.LocalMessage
import com.upsaclay.news.data.local.AnnouncementDao
import com.upsaclay.news.data.model.LocalAnnouncement

@Database(
    entities = [
        LocalAnnouncement::class,
        LocalConversation::class,
        LocalMessage::class
    ],
    version = 1
)
internal abstract class GedoiseDatabase : RoomDatabase() {
    abstract fun announcementDao(): AnnouncementDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
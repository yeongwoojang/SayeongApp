package com.sayeong.vv.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sayeong.vv.database.model.BookmarkedMusicEntity

@Database(entities = [BookmarkedMusicEntity::class], version = 1)
abstract class SayeongDatabase: RoomDatabase() {

    abstract fun bookmarkedMusicDao(): BookmarkedMusicDao
}
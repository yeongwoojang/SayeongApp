package com.sayeong.vv.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sayeong.vv.database.model.BookmarkedMusicEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface BookmarkedMusicDao {
    @Query("SELECT * FROM bookmarked_music")
    fun getAllBookmarkedMusics(): Flow<List<BookmarkedMusicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: BookmarkedMusicEntity)

    @Delete
    suspend fun delete(music: BookmarkedMusicEntity)
}
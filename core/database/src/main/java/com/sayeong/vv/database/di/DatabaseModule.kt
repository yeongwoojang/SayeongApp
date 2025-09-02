package com.sayeong.vv.database.di

import android.content.Context
import androidx.room.Room
import com.sayeong.vv.database.BookmarkedMusicDao
import com.sayeong.vv.database.SayeongDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    internal fun provideSayeongDatabase(
        @ApplicationContext context: Context,
    ): SayeongDatabase {
        return Room.databaseBuilder(
            context,
            SayeongDatabase::class.java,
            "sayeong-database"
        ).build()
    }

    @Provides
    fun provideBookmarkedMusicDao(database: SayeongDatabase): BookmarkedMusicDao {
        return database.bookmarkedMusicDao()
    }
}
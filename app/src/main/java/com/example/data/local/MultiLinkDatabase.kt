package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ClickEvent
import com.example.data.model.CreatorProfile
import com.example.data.model.LinkItem

@Database(
    entities = [
        CreatorProfile::class,
        LinkItem::class,
        ClickEvent::class
    ],
    version = 4,
    exportSchema = false
)
abstract class MultiLinkDatabase : RoomDatabase() {

    abstract fun multiLinkDao(): MultiLinkDao

    companion object {
        @Volatile
        private var INSTANCE: MultiLinkDatabase? = null

        fun getDatabase(context: Context): MultiLinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MultiLinkDatabase::class.java,
                    "multilink_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

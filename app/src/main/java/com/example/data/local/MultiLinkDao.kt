package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ClickEvent
import com.example.data.model.CreatorProfile
import com.example.data.model.LinkCategory
import com.example.data.model.LinkItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MultiLinkDao {

    // Profile
    @Query("SELECT * FROM creator_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<CreatorProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: CreatorProfile)

    // Links
    @Query("SELECT * FROM links ORDER BY orderIndex ASC, id DESC")
    fun getAllLinks(): Flow<List<LinkItem>>

    @Query("SELECT * FROM links WHERE category = :category ORDER BY orderIndex ASC, id DESC")
    fun getLinksByCategory(category: LinkCategory): Flow<List<LinkItem>>

    @Query("SELECT * FROM links WHERE isActive = 1 ORDER BY isFeatured DESC, orderIndex ASC, id DESC")
    fun getActiveLinks(): Flow<List<LinkItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: LinkItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLinks(links: List<LinkItem>)

    @Update
    suspend fun updateLink(link: LinkItem)

    @Delete
    suspend fun deleteLink(link: LinkItem)

    @Query("DELETE FROM links WHERE id = :id")
    suspend fun deleteLinkById(id: Long)

    @Query("UPDATE links SET totalClicks = totalClicks + 1 WHERE id = :linkId")
    suspend fun incrementLinkClicks(linkId: Long)

    @Query("UPDATE links SET isActive = :isActive WHERE id = :linkId")
    suspend fun toggleLinkActive(linkId: Long, isActive: Boolean)

    // Analytics / Click Events
    @Query("SELECT * FROM click_events ORDER BY timestamp DESC")
    fun getAllClickEvents(): Flow<List<ClickEvent>>

    @Query("SELECT * FROM click_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentClickEvents(limit: Int): Flow<List<ClickEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClickEvent(event: ClickEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllClickEvents(events: List<ClickEvent>)

    @Query("SELECT COUNT(*) FROM click_events")
    fun getTotalClickCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(revenueAmount), 0.0) FROM click_events")
    fun getTotalRevenue(): Flow<Double>
}

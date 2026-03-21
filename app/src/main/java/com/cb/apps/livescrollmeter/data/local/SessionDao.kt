package com.cb.apps.livescrollmeter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: SessionEntity)

    @Query("SELECT * FROM sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT SUM(swipeCount) FROM sessions")
    fun getTotalSwipeCount(): Flow<Int?>

    @Query("SELECT SUM(durationSeconds) FROM sessions")
    fun getTotalDuration(): Flow<Long?>

    @Query("""
        SELECT 
            date(timestamp / 1000, 'unixepoch', 'localtime') as date,
            SUM(swipeCount) as totalSwipes,
            SUM(durationSeconds) as totalDurationSeconds
        FROM sessions
        GROUP BY date
        ORDER BY date DESC
    """)
    fun getDailyStats(): Flow<List<DailyStat>>
}

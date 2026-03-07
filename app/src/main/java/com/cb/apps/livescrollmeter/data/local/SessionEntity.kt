package com.cb.apps.livescrollmeter.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val swipeCount: Int,
    val durationSeconds: Long,
    val timestamp: Long = System.currentTimeMillis()
)

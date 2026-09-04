package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.TimetableEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable_entries ORDER BY startTime ASC")
    fun getAllEntries(): Flow<List<TimetableEntry>>

    @Query("SELECT * FROM timetable_entries WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getEntriesForDay(dayOfWeek: String): Flow<List<TimetableEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: TimetableEntry): Long

    @Update
    suspend fun updateEntry(entry: TimetableEntry)

    @Delete
    suspend fun deleteEntry(entry: TimetableEntry)

    @Query("DELETE FROM timetable_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)
}

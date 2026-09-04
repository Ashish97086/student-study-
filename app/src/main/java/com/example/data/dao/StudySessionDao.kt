package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE date = :date ORDER BY startTime DESC")
    fun getSessionsForDate(date: String): Flow<List<StudySession>>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE date = :date")
    fun getTotalMinutesForDate(date: String): Flow<Int?>

    @Query("SELECT DISTINCT date FROM study_sessions ORDER BY date DESC")
    fun getStudyDates(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Delete
    suspend fun deleteSession(session: StudySession)

    @Query("SELECT SUM(durationMinutes) FROM study_sessions")
    fun getTotalStudyMinutes(): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE date >= :startDate AND date <= :endDate")
    fun getTotalMinutesBetweenDates(startDate: String, endDate: String): Flow<Int?>

    @Query("SELECT COUNT(*) FROM study_sessions")
    fun getTotalSessionsCount(): Flow<Int>

    @Query("SELECT subjectId FROM study_sessions WHERE subjectId IS NOT NULL GROUP BY subjectId ORDER BY SUM(durationMinutes) DESC LIMIT 1")
    fun getMostStudiedSubjectId(): Flow<Long?>

    @Query("SELECT * FROM study_sessions ORDER BY endTime DESC LIMIT 1")
    fun getLastSession(): Flow<StudySession?>

    @Query("SELECT * FROM study_sessions WHERE (:subjectId IS NULL OR subjectId = :subjectId) AND (:studyType IS NULL OR studyType = :studyType) AND (:date IS NULL OR date = :date) ORDER BY startTime DESC")
    fun getFilteredSessions(subjectId: Long?, studyType: String?, date: String?): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE id = :id")
    fun getSessionById(id: Long): Flow<StudySession?>
}

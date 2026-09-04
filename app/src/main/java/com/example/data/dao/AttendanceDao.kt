package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendances ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendances WHERE subjectId = :subjectId ORDER BY date DESC")
    fun getAttendanceForSubject(subjectId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendances WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("SELECT COUNT(*) FROM attendances WHERE subjectId = :subjectId AND status = 'PRESENT'")
    fun getPresentCountForSubject(subjectId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendances WHERE subjectId = :subjectId")
    fun getTotalCountForSubject(subjectId: Long): Flow<Int>
}

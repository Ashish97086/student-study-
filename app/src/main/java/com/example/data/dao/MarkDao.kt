package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.Mark
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkDao {
    @Query("SELECT * FROM marks ORDER BY examDate DESC")
    fun getAllMarks(): Flow<List<Mark>>

    @Query("SELECT * FROM marks WHERE subjectId = :subjectId ORDER BY examDate DESC")
    fun getMarksForSubject(subjectId: Long): Flow<List<Mark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMark(mark: Mark): Long

    @Update
    suspend fun updateMark(mark: Mark)

    @Delete
    suspend fun deleteMark(mark: Mark)
}

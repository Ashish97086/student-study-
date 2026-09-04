package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY id ASC")
    fun getChaptersForSubject(subjectId: Long): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE id = :id")
    fun getChapterById(id: Long): Flow<Chapter?>

    @Query("SELECT * FROM chapters")
    fun getAllChapters(): Flow<List<Chapter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Update
    suspend fun updateChapter(chapter: Chapter)

    @Delete
    suspend fun deleteChapter(chapter: Chapter)

    @Query("DELETE FROM chapters WHERE id = :id")
    suspend fun deleteChapterById(id: Long)

    @Query("SELECT COUNT(*) FROM chapters")
    fun getTotalChapterCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapters WHERE status = 'COMPLETED'")
    fun getCompletedChapterCount(): Flow<Int>
}

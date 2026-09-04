package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.Exam
import com.example.data.entity.ExamSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY examDate ASC")
    fun getAllExams(): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE examDate >= :today ORDER BY examDate ASC LIMIT 1")
    fun getUpcomingExam(today: String): Flow<Exam?>

    @Query("SELECT * FROM exams WHERE id = :id")
    fun getExamById(id: Long): Flow<Exam?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    @Query("SELECT * FROM exam_subjects WHERE examId = :examId")
    fun getSubjectsForExam(examId: Long): Flow<List<ExamSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamSubject(examSubject: ExamSubject): Long

    @Delete
    suspend fun deleteExamSubject(examSubject: ExamSubject)
}

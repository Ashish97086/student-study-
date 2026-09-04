package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AttendanceDao
import com.example.data.dao.ChapterDao
import com.example.data.dao.ExamDao
import com.example.data.dao.MarkDao
import com.example.data.dao.StudentProfileDao
import com.example.data.dao.StudySessionDao
import com.example.data.dao.SubjectDao
import com.example.data.dao.TaskDao
import com.example.data.dao.TimetableDao
import com.example.data.entity.Attendance
import com.example.data.entity.Chapter
import com.example.data.entity.Exam
import com.example.data.entity.ExamSubject
import com.example.data.entity.Mark
import com.example.data.entity.StudentProfile
import com.example.data.entity.StudySession
import com.example.data.entity.Subject
import com.example.data.entity.Task
import com.example.data.entity.TimetableEntry

@Database(
    entities = [
        StudentProfile::class,
        Subject::class,
        Chapter::class,
        StudySession::class,
        Task::class,
        TimetableEntry::class,
        Exam::class,
        ExamSubject::class,
        Attendance::class,
        Mark::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentProfileDao(): StudentProfileDao
    abstract fun subjectDao(): SubjectDao
    abstract fun chapterDao(): ChapterDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun taskDao(): TaskDao
    abstract fun timetableDao(): TimetableDao
    abstract fun examDao(): ExamDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun markDao(): MarkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_study_manager.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.example.data.repository

import com.example.data.database.AppDatabase
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
import kotlinx.coroutines.flow.Flow

class StudyRepository(private val database: AppDatabase) {
    private val profileDao = database.studentProfileDao()
    private val subjectDao = database.subjectDao()
    private val chapterDao = database.chapterDao()
    private val studySessionDao = database.studySessionDao()
    private val taskDao = database.taskDao()
    private val timetableDao = database.timetableDao()
    private val examDao = database.examDao()
    private val attendanceDao = database.attendanceDao()
    private val markDao = database.markDao()

    // Profile
    val studentProfile: Flow<StudentProfile?> = profileDao.getProfile()
    suspend fun saveProfile(profile: StudentProfile): Long = profileDao.insertProfile(profile)
    suspend fun updateProfile(profile: StudentProfile) = profileDao.updateProfile(profile)

    // Subjects
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()
    fun getSubjectById(id: Long): Flow<Subject?> = subjectDao.getSubjectById(id)
    suspend fun insertSubject(subject: Subject): Long = subjectDao.insertSubject(subject)
    suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)
    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)
    suspend fun deleteSubjectById(id: Long) = subjectDao.deleteSubjectById(id)

    // Chapters
    fun getChaptersForSubject(subjectId: Long): Flow<List<Chapter>> = chapterDao.getChaptersForSubject(subjectId)
    val allChapters: Flow<List<Chapter>> = chapterDao.getAllChapters()
    suspend fun insertChapter(chapter: Chapter): Long = chapterDao.insertChapter(chapter)
    suspend fun updateChapter(chapter: Chapter) = chapterDao.updateChapter(chapter)
    suspend fun deleteChapter(chapter: Chapter) = chapterDao.deleteChapter(chapter)
    suspend fun deleteChapterById(id: Long) = chapterDao.deleteChapterById(id)
    val totalChaptersCount: Flow<Int> = chapterDao.getTotalChapterCount()
    val completedChaptersCount: Flow<Int> = chapterDao.getCompletedChapterCount()

    // Study Sessions
    val allSessions: Flow<List<StudySession>> = studySessionDao.getAllSessions()
    fun getSessionsForDate(date: String): Flow<List<StudySession>> = studySessionDao.getSessionsForDate(date)
    fun getTotalMinutesForDate(date: String): Flow<Int?> = studySessionDao.getTotalMinutesForDate(date)
    fun getTotalMinutesBetweenDates(startDate: String, endDate: String): Flow<Int?> = studySessionDao.getTotalMinutesBetweenDates(startDate, endDate)
    val studyDates: Flow<List<String>> = studySessionDao.getStudyDates()
    val totalStudyMinutes: Flow<Int?> = studySessionDao.getTotalStudyMinutes()
    val totalSessionsCount: Flow<Int> = studySessionDao.getTotalSessionsCount()
    val mostStudiedSubjectId: Flow<Long?> = studySessionDao.getMostStudiedSubjectId()
    val lastSession: Flow<StudySession?> = studySessionDao.getLastSession()
    fun getFilteredSessions(subjectId: Long?, studyType: String?, date: String?): Flow<List<StudySession>> =
        studySessionDao.getFilteredSessions(subjectId, studyType, date)
    suspend fun insertSession(session: StudySession): Long = studySessionDao.insertSession(session)
    suspend fun deleteSession(session: StudySession) = studySessionDao.deleteSession(session)

    // Tasks
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<Task>> = taskDao.getPendingTasks()
    val completedTasks: Flow<List<Task>> = taskDao.getCompletedTasks()
    val pendingTaskCount: Flow<Int> = taskDao.getPendingTaskCount()
    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    // Timetable
    val allTimetableEntries: Flow<List<TimetableEntry>> = timetableDao.getAllEntries()
    fun getEntriesForDay(day: String): Flow<List<TimetableEntry>> = timetableDao.getEntriesForDay(day)
    suspend fun insertTimetableEntry(entry: TimetableEntry): Long = timetableDao.insertEntry(entry)
    suspend fun updateTimetableEntry(entry: TimetableEntry) = timetableDao.updateEntry(entry)
    suspend fun deleteTimetableEntry(entry: TimetableEntry) = timetableDao.deleteEntry(entry)

    // Exams
    val allExams: Flow<List<Exam>> = examDao.getAllExams()
    fun getUpcomingExam(today: String): Flow<Exam?> = examDao.getUpcomingExam(today)
    suspend fun insertExam(exam: Exam): Long = examDao.insertExam(exam)
    suspend fun updateExam(exam: Exam) = examDao.updateExam(exam)
    suspend fun deleteExam(exam: Exam) = examDao.deleteExam(exam)
    fun getSubjectsForExam(examId: Long): Flow<List<ExamSubject>> = examDao.getSubjectsForExam(examId)
    suspend fun insertExamSubject(examSubject: ExamSubject): Long = examDao.insertExamSubject(examSubject)
    suspend fun deleteExamSubject(examSubject: ExamSubject) = examDao.deleteExamSubject(examSubject)

    // Attendance
    val allAttendance: Flow<List<Attendance>> = attendanceDao.getAllAttendance()
    fun getAttendanceForSubject(subjectId: Long): Flow<List<Attendance>> = attendanceDao.getAttendanceForSubject(subjectId)
    fun getAttendanceForDate(date: String): Flow<List<Attendance>> = attendanceDao.getAttendanceForDate(date)
    suspend fun insertAttendance(attendance: Attendance): Long = attendanceDao.insertAttendance(attendance)
    suspend fun deleteAttendance(attendance: Attendance) = attendanceDao.deleteAttendance(attendance)
    fun getPresentCountForSubject(subjectId: Long): Flow<Int> = attendanceDao.getPresentCountForSubject(subjectId)
    fun getTotalCountForSubject(subjectId: Long): Flow<Int> = attendanceDao.getTotalCountForSubject(subjectId)

    // Marks
    val allMarks: Flow<List<Mark>> = markDao.getAllMarks()
    fun getMarksForSubject(subjectId: Long): Flow<List<Mark>> = markDao.getMarksForSubject(subjectId)
    suspend fun insertMark(mark: Mark): Long = markDao.insertMark(mark)
    suspend fun updateMark(mark: Mark) = markDao.updateMark(mark)
    suspend fun deleteMark(mark: Mark) = markDao.deleteMark(mark)
}

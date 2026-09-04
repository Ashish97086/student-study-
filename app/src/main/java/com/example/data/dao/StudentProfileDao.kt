package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.StudentProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentProfileDao {
    @Query("SELECT * FROM student_profiles ORDER BY id DESC LIMIT 1")
    fun getProfile(): Flow<StudentProfile?>

    @Query("SELECT * FROM student_profiles ORDER BY id DESC LIMIT 1")
    suspend fun getProfileSync(): StudentProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: StudentProfile): Long

    @Update
    suspend fun updateProfile(profile: StudentProfile)

    @Query("DELETE FROM student_profiles")
    suspend fun deleteAll()
}

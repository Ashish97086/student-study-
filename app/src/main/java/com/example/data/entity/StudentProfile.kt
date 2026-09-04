package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profiles")
data class StudentProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val course: String = "",
    val college: String = "",
    val semester: String = "1",
    val academicYear: String = "",
    val dailyStudyTarget: Int = 120, // Daily target in minutes
    val createdAt: Long = System.currentTimeMillis()
)

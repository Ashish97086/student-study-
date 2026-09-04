package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val examDate: String, // YYYY-MM-DD
    val description: String = ""
)

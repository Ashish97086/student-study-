package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String = "",
    val teacher: String = "",
    val targetPercentage: Double = 75.0,
    val colorHex: String = "#4F46E5",
    val createdAt: Long = System.currentTimeMillis()
)

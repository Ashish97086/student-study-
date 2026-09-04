package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val subjectId: Long? = null,
    val type: String = "ASSIGNMENT", // ASSIGNMENT, HOMEWORK, REVISION, OTHER
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val dueDate: String = "",
    val dueTime: String = "",
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

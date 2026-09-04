package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class Chapter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val name: String,
    val description: String = "",
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val progressPercentage: Int = 0, // 0 - 100
    val status: String = "NOT_STARTED", // NOT_STARTED, IN_PROGRESS, COMPLETED
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    fun getTodayDisplayString(): String {
        return displayDateFormat.format(Date())
    }

    fun getCurrentDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: "Monday"
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Good night"
        }
    }

    fun formatMinutes(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) {
            "${hours}h ${mins}m"
        } else {
            "${mins}m"
        }
    }

    fun getStartOfWeekDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return dateFormat.format(calendar.time)
    }

    fun getEndOfWeekDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return dateFormat.format(calendar.time)
    }

    fun getStartOfMonthDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return dateFormat.format(calendar.time)
    }

    fun getEndOfMonthDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return dateFormat.format(calendar.time)
    }

    fun formatTime(millis: Long): String {
        return timeFormat.format(Date(millis))
    }

    fun formatTimeRange(startMillis: Long, endMillis: Long): String {
        return "${formatTime(startMillis)} - ${formatTime(endMillis)}"
    }

    fun calculateStreak(studyDates: List<String>): Int {
        if (studyDates.isEmpty()) return 0
        val todayStr = getTodayDateString()
        
        // Filter out future dates, remove duplicates, sort descending
        val validDates = studyDates
            .filter { it <= todayStr }
            .distinct()
            .sortedDescending()

        if (validDates.isEmpty()) return 0

        val calendar = Calendar.getInstance()
        val today = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(calendar.time)

        val mostRecent = validDates.first()
        // If neither today nor yesterday has study, current streak is 0
        if (mostRecent != today && mostRecent != yesterday) {
            return 0
        }

        var streak = 0
        val checkCal = Calendar.getInstance()
        if (mostRecent == yesterday) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        for (d in validDates) {
            val expected = dateFormat.format(checkCal.time)
            if (d == expected) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else if (d < expected) {
                break
            }
        }
        return streak
    }

    fun calculateBestStreak(studyDates: List<String>): Int {
        if (studyDates.isEmpty()) return 0
        val todayStr = getTodayDateString()
        val validSortedDates = studyDates
            .filter { it <= todayStr }
            .distinct()
            .sorted() // ascending

        if (validSortedDates.isEmpty()) return 0

        var maxStreak = 1
        var currentStreak = 1

        for (i in 1 until validSortedDates.size) {
            try {
                val prev = dateFormat.parse(validSortedDates[i - 1]) ?: continue
                val curr = dateFormat.parse(validSortedDates[i]) ?: continue
                val diffDays = TimeUnit.DAYS.convert(curr.time - prev.time, TimeUnit.MILLISECONDS)
                if (diffDays == 1L) {
                    currentStreak++
                    if (currentStreak > maxStreak) {
                        maxStreak = currentStreak
                    }
                } else if (diffDays > 1L) {
                    currentStreak = 1
                }
            } catch (e: Exception) {
                currentStreak = 1
            }
        }
        return maxStreak
    }

    fun calculateTotalStudyDays(studyDates: List<String>): Int {
        val todayStr = getTodayDateString()
        return studyDates.filter { it <= todayStr }.distinct().size
    }

    fun daysUntil(targetDateStr: String): Long {
        return try {
            val target = dateFormat.parse(targetDateStr) ?: return 0
            val today = dateFormat.parse(getTodayDateString()) ?: return 0
            val diff = target.time - today.time
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            0
        }
    }
}

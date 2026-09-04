package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.navigation.Screen
import com.example.ui.theme.GeoOutline
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoPrimaryContainer
import com.example.viewmodel.StudyViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: StudyViewModel
) {
    var name by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("1") }
    var targetHours by remember { mutableFloatStateOf(2.5f) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val isNameValid = name.trim().isNotBlank()
    val isCourseValid = course.trim().isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Hero Header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, GeoOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GeoPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Welcome Icon",
                        tint = GeoPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Welcome to Study Manager",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1C1B1F)
                    )
                    Text(
                        text = "Your private, offline study companion",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Tell us about yourself",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color(0xFF1C1B1F)
        )
        Text(
            text = "No account or cloud login needed. All data stays strictly on your device.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Student Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Student Name") },
            placeholder = { Text("e.g. Alex Johnson") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Student Name", tint = GeoPrimary)
            },
            singleLine = true,
            isError = hasAttemptedSubmit && !isNameValid,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_name_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GeoPrimary,
                cursorColor = GeoPrimary
            )
        )
        if (hasAttemptedSubmit && !isNameValid) {
            Text(
                text = "Please enter your name",
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Course / Class
        OutlinedTextField(
            value = course,
            onValueChange = { course = it },
            label = { Text("Course / Major / Class") },
            placeholder = { Text("e.g. Computer Science, 12th Grade") },
            leadingIcon = {
                Icon(Icons.Default.School, contentDescription = "Course", tint = GeoPrimary)
            },
            singleLine = true,
            isError = hasAttemptedSubmit && !isCourseValid,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_course_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GeoPrimary,
                cursorColor = GeoPrimary
            )
        )
        if (hasAttemptedSubmit && !isCourseValid) {
            Text(
                text = "Please enter your course or class",
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Semester
        OutlinedTextField(
            value = semester,
            onValueChange = { semester = it },
            label = { Text("Semester / Term / Grade Level") },
            placeholder = { Text("e.g. Semester 3 or Fall 2026") },
            leadingIcon = {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Semester", tint = GeoPrimary)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_semester_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GeoPrimary,
                cursorColor = GeoPrimary
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Daily Study Target
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, GeoOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GeoPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Target icon",
                            tint = GeoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Daily Study Target",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1C1B1F)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${String.format("%.1f", targetHours)} hrs / day",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = GeoPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Slider(
                    value = targetHours,
                    onValueChange = { targetHours = it },
                    valueRange = 0.5f..8f,
                    steps = 14,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = GeoPrimary,
                        activeTrackColor = GeoPrimary,
                        inactiveTrackColor = GeoOutline
                    )
                )
                Text(
                    text = "${(targetHours * 60).toInt()} minutes of focused study each day",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Continue Button
        Button(
            onClick = {
                hasAttemptedSubmit = true
                if (isNameValid && isCourseValid) {
                    viewModel.saveOnboardingProfile(
                        name = name,
                        course = course,
                        semester = semester,
                        targetHours = targetHours.toInt().coerceAtLeast(1)
                    )
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("onboarding_continue_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = GeoPrimary,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ApplicationRecordEntity
import com.example.ui.JobPilotViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ApplicationsTrackerScreen(
    viewModel: JobPilotViewModel,
    modifier: Modifier = Modifier
) {
    val applications by viewModel.allApplications.collectAsStateWithLifecycle()
    val candidateProfile by viewModel.candidateProfile.collectAsStateWithLifecycle()
    var selectedFilterTab by remember { mutableIntStateOf(0) }

    val filterOptions = listOf("All", "Submitted", "Under Review", "Interview", "Prepared", "Blocked")

    val filteredList = remember(applications, selectedFilterTab) {
        val selectedLabel = filterOptions[selectedFilterTab]
        if (selectedLabel == "All") applications
        else applications.filter { it.status.equals(selectedLabel, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("applications_tracker_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "International Applications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${applications.size} Total",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Daily Quota Security Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Daily Submission Guard: ${candidateProfile.applicationsSubmittedToday}/${candidateProfile.dailyApplicationLimit} used today",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "Anti-Spam Active",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857)
                )
            }

            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedFilterTab,
                edgePadding = 0.dp
            ) {
                filterOptions.forEachIndexed { index, option ->
                    Tab(
                        selected = selectedFilterTab == index,
                        onClick = { selectedFilterTab = index },
                        text = {
                            val count = if (option == "All") applications.size else applications.count { it.status.equals(option, ignoreCase = true) }
                            Text("$option ($count)", fontSize = 11.sp)
                        }
                    )
                }
            }
        }

        // Applications List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "No applications in this category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Find verified openings on the Discover tab and initiate 12-step preparation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { record ->
                    ApplicationRecordCard(
                        record = record,
                        onUpdateStatus = { newStatus -> viewModel.updateApplicationStatus(record, newStatus) },
                        onUpdateNotes = { newNotes -> viewModel.updateApplicationNotes(record, newNotes) },
                        onDelete = { viewModel.deleteApplication(record.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationRecordCard(
    record: ApplicationRecordEntity,
    onUpdateStatus: (String) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var isEditingNotes by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf(record.recruiterNotes) }

    val formattedDate = remember(record.createdTimestamp) {
        val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        df.format(Date(record.createdTimestamp))
    }

    val (statusBg, statusFg) = when (record.status.uppercase()) {
        "SUBMITTED" -> Pair(Color(0xFFECFDF5), Color(0xFF047857))
        "UNDER_REVIEW" -> Pair(Color(0xFFEFF6FF), Color(0xFF1D4ED8))
        "INTERVIEW" -> Pair(Color(0xFFFAF5FF), Color(0xFF7E22CE))
        "OFFER" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "PREPARED" -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
        else -> Pair(Color(0xFFFEF2F2), Color(0xFFB91C1C))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_record_${record.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Company, Role & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.company,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = record.jobTitle,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${record.city}, ${record.country} • Created $formattedDate",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = record.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg
                    )
                }
            }

            // Submission Evidence details
            if (record.confirmationCode != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Conf: ${record.confirmationCode}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (record.submissionEvidenceUrl != null) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Evidence Link",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(record.submissionEvidenceUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            // Status Transitions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Change Status:", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)

                listOf("SUBMITTED", "UNDER_REVIEW", "INTERVIEW", "OFFER").forEach { statusLabel ->
                    if (!statusLabel.equals(record.status, ignoreCase = true)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = statusLabel.replace("_", " "),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(1.dp)
                            )
                        }
                    }
                }
            }

            // Recruiter outreach & Notes
            if (isEditingNotes) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Recruiter contacts, interview dates, referral notes...", fontSize = 12.sp) },
                        maxLines = 3
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = { isEditingNotes = false }) {
                            Text("Cancel", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(onClick = {
                            onUpdateNotes(notesText)
                            isEditingNotes = false
                        }) {
                            Text("Save Note", fontSize = 11.sp)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (record.recruiterNotes.isNotBlank()) "Notes: ${record.recruiterNotes}" else "No outreach notes yet",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { isEditingNotes = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit notes", modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete record", tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

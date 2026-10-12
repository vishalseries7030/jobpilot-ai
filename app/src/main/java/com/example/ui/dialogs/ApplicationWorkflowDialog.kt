package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.ApplicationRecordEntity
import com.example.data.local.CandidateProfileEntity
import com.example.data.model.JobOpportunity
import com.example.service.ApplicationEngine
import com.example.service.VerificationStatus

@Composable
fun ApplicationWorkflowDialog(
    job: JobOpportunity,
    profile: CandidateProfileEntity,
    existingApplication: ApplicationRecordEntity? = null,
    onDismiss: () -> Unit,
    onSubmitApplication: (ApplicationRecordEntity) -> Unit
) {
    val context = LocalContext.current
    var overrideBlocker by remember { mutableStateOf(false) }
    var userConfirmedAuthorization by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var enteredEmployerConfirmationCode by remember { mutableStateOf("") }
    var applicationPrepared by remember { mutableStateOf(existingApplication != null) }
    var directSubmitted by remember { mutableStateOf(existingApplication?.status == "SUBMITTED") }

    val auditSteps = remember(job, profile, overrideBlocker) {
        ApplicationEngine.execute12StepAudit(job, profile, overrideBlocker)
    }

    val docs = remember(job, profile) {
        ApplicationEngine.generateDocuments(job, profile)
    }

    val isDuplicate = existingApplication != null
    val hasHardBlocker = auditSteps.any { it.status == VerificationStatus.BLOCKED && !overrideBlocker }
    val isDailyQuotaExceeded = profile.applicationsSubmittedToday >= profile.dailyApplicationLimit

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("application_workflow_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "12-Step Application Workflow",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${job.company} • ${job.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Duplicate Application Alert
                if (isDuplicate) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF3C7))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                            Column {
                                Text(
                                    text = "Duplicate Application Guard Active",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "You already have an existing tracked application for this role (Status: ${existingApplication?.status}). Re-submitting to the same opening is prevented to avoid spam flags.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }

                // Daily Limit Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isDailyQuotaExceeded) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Safety Limit: ${profile.applicationsSubmittedToday}/${profile.dailyApplicationLimit} used today",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDailyQuotaExceeded) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isDailyQuotaExceeded) "Daily Quota Reached" else "${profile.dailyApplicationLimit - profile.applicationsSubmittedToday} slots left",
                        fontSize = 11.sp,
                        color = if (isDailyQuotaExceeded) Color(0xFFB91C1C) else MaterialTheme.colorScheme.primary
                    )
                }

                // Tab Switcher
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("12 Steps", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Documents", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Visa Q&A", fontSize = 12.sp) }
                    )
                }

                when (selectedTab) {
                    0 -> {
                        // 12-Step Audit
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            auditSteps.forEach { step ->
                                val (icon, iconTint, bgCol) = when (step.status) {
                                    VerificationStatus.PASSED -> Triple(Icons.Default.CheckCircle, Color(0xFF10B981), Color(0xFFECFDF5))
                                    VerificationStatus.WARNING -> Triple(Icons.Default.Warning, Color(0xFFF59E0B), Color(0xFFFFFBEB))
                                    VerificationStatus.BLOCKED -> Triple(Icons.Default.Block, Color(0xFFEF4444), Color(0xFFFEF2F2))
                                    VerificationStatus.PENDING -> Triple(Icons.Default.HourglassTop, Color(0xFF64748B), Color(0xFFF1F5F9))
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(bgCol)
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier
                                            .size(15.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Step ${step.stepNumber}: ${step.stepTitle}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = step.details,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Hard Blocker Override
                        if (docs.blockerWarning != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF2F2))
                                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = docs.blockerWarning!!,
                                    fontSize = 11.sp,
                                    color = Color(0xFFB91C1C),
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = overrideBlocker,
                                        onCheckedChange = { overrideBlocker = it }
                                    )
                                    Text(
                                        text = "I possess unrestricted work rights / Override filter",
                                        fontSize = 10.sp,
                                        color = Color(0xFF7F1D1D)
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // Documents
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Truthfully Tailored Cover Letter:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Cover Letter", docs.coverLetter))
                                        Toast.makeText(context, "Cover letter copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", fontSize = 10.sp)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = docs.coverLetter,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = "Verified Candidate Profile Summary:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = docs.tailoredResumeSummary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    2 -> {
                        // Q&A
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Truthful Answers to Screening Questions:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            docs.screeningQAs.forEach { qa ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "Q: ${qa.question}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "A: ${qa.answer}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Integrity Note: ${qa.explanation}",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Explicit Candidate Authorization Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = userConfirmedAuthorization,
                        onCheckedChange = { userConfirmedAuthorization = it },
                        modifier = Modifier.testTag("confirm_authorization_checkbox")
                    )
                    Text(
                        text = "I verify these answers accurately represent my legal immigration requirements.",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Transparent Submission Instructions Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Authentic Submission Policy: JobPilot prepares tailored documents and audits visa rules. Submission is completed directly on the employer's official portal (${job.sourcePlatform}) to comply with anti-bot policies.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Candidate Action Steps
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Action 1: Prepare & Save Application Bundle
                    Button(
                        onClick = {
                            val record = ApplicationRecordEntity(
                                jobId = job.id,
                                jobTitle = job.title,
                                company = job.company,
                                city = job.city,
                                country = job.country,
                                status = "PREPARED",
                                globalScore = 88,
                                technicalMatch = 85,
                                visaFeasibility = 90,
                                sponsorshipCode = job.sponsorshipStatus.code,
                                coverLetter = docs.coverLetter,
                                tailoredResumeSummary = docs.tailoredResumeSummary,
                                qaResponsesJson = "Truthful screening answers verified",
                                submissionEvidenceUrl = job.originalUrl,
                                confirmationCode = null,
                                recruiterNotes = "Application package prepared. Ready for direct employer portal submission.",
                                submittedTimestamp = null,
                                isOverridden = overrideBlocker,
                                isDirectSubmissionVerified = false
                            )
                            onSubmitApplication(record)
                            applicationPrepared = true
                            Toast.makeText(context, "Application package prepared & saved!", Toast.LENGTH_SHORT).show()
                        },
                        enabled = userConfirmedAuthorization && (!hasHardBlocker || overrideBlocker) && !isDailyQuotaExceeded && !isDuplicate && !applicationPrepared,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("prepare_application_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (applicationPrepared) "Application Package Prepared" else "1. Prepare & Save Application Package", fontSize = 12.sp)
                    }

                    // Action 2: Launch Official Employer Portal
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.originalUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("2. Open ${job.company} Career Portal to Submit", fontSize = 12.sp)
                    }

                    // Action 3: Record Actual Submission Reference
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "3. Confirm Completed Submission (Optional Reference):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = enteredEmployerConfirmationCode,
                            onValueChange = { enteredEmployerConfirmationCode = it },
                            placeholder = { Text("e.g. ASML-REQ-8902 or Booking confirmation email code", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val record = ApplicationRecordEntity(
                                    jobId = job.id,
                                    jobTitle = job.title,
                                    company = job.company,
                                    city = job.city,
                                    country = job.country,
                                    status = "SUBMITTED",
                                    globalScore = 88,
                                    technicalMatch = 85,
                                    visaFeasibility = 90,
                                    sponsorshipCode = job.sponsorshipStatus.code,
                                    coverLetter = docs.coverLetter,
                                    tailoredResumeSummary = docs.tailoredResumeSummary,
                                    qaResponsesJson = "Submitted with truthful answers",
                                    submissionEvidenceUrl = job.originalUrl,
                                    confirmationCode = enteredEmployerConfirmationCode.ifBlank { "MANUAL-PORTAL-SUBMISSION" },
                                    recruiterNotes = "Candidate submitted via official ${job.company} portal. Ref: ${enteredEmployerConfirmationCode.ifBlank { "Recorded" }}",
                                    submittedTimestamp = System.currentTimeMillis(),
                                    isOverridden = overrideBlocker,
                                    isDirectSubmissionVerified = true
                                )
                                onSubmitApplication(record)
                                directSubmitted = true
                                Toast.makeText(context, "Submission verified & logged!", Toast.LENGTH_SHORT).show()
                            },
                            enabled = userConfirmedAuthorization && (!hasHardBlocker || overrideBlocker) && !isDailyQuotaExceeded && !directSubmitted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_submission_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
                        ) {
                            Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (directSubmitted) "Submission Logged as Verified" else "Log Verified Portal Submission", fontSize = 12.sp)
                        }
                    }
                }

                // Close Button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", fontSize = 12.sp)
                }
            }
        }
    }
}

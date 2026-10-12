package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "application_records")
data class ApplicationRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jobId: String,
    val jobTitle: String,
    val company: String,
    val city: String,
    val country: String,
    val status: String, // "PREPARED", "SUBMITTED", "UNDER_REVIEW", "INTERVIEW", "OFFER", "REJECTED", "BLOCKED"
    val globalScore: Int,
    val technicalMatch: Int,
    val visaFeasibility: Int,
    val sponsorshipCode: String, // "A", "B", "C", "D", "E"
    val coverLetter: String,
    val tailoredResumeSummary: String,
    val qaResponsesJson: String,
    val submissionEvidenceUrl: String? = null,
    val confirmationCode: String? = null,
    val recruiterNotes: String = "",
    val submittedTimestamp: Long? = null,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val isOverridden: Boolean = false,
    val isDirectSubmissionVerified: Boolean = false
)

package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidate_profile")
data class CandidateProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Alex Chen",
    val email: String = "alex.chen.eng@example.com",
    val phone: String = "+1 (555) 234-8901",
    val residenceCountry: String = "India",
    val citizenshipCountry: String = "India",
    val currentAuthorizations: String = "India (Unrestricted Citizen), No active foreign work permit",
    val requiresSponsorship: Boolean = true,
    val willingToRelocate: Boolean = true,
    val preferredDestinations: String = "Germany, Netherlands, United Kingdom, Ireland, Japan, Australia, Canada",
    val englishProficiency: String = "C1/C2 Fluent (Professional Working Proficiency)",
    val otherLanguages: String = "German (A2 Elementary), Japanese (Basic)",
    val degree: String = "B.Tech in Computer Science & Engineering",
    val graduationDate: String = "2026-06",
    val yearsExperience: Double = 0.5,
    val earliestJoiningDate: String = "2026-08-01",
    val willingToAttendOnsiteInterviews: Boolean = true,
    val keySkills: String = "Kotlin, Python, Java, TypeScript, Docker, SQL, Git, REST APIs, Microservices",
    val githubOrPortfolio: String = "github.com/alexchen-dev",
    val resumeFileName: String = "alex_chen_cv_2026.pdf",
    val resumeContent: String = "Alex Chen - Early Career Software Engineer. B.Tech Computer Science (2026). Skills: Kotlin, Python, Docker, SQL, Git, REST APIs. Projects: High-performance caching proxy, Cloud inventory management system. Location: Bangalore, India. Status: Seeking employer-sponsored work authorization abroad.",
    val dailyApplicationLimit: Int = 5,
    val applicationsSubmittedToday: Int = 1,
    val lastSubmissionDate: String = "2026-10-11"
)

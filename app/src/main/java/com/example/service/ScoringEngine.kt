package com.example.service

import com.example.data.local.CandidateProfileEntity
import com.example.data.model.JobOpportunity
import com.example.data.model.OpportunityScore
import com.example.data.model.SponsorshipStatus

object ScoringEngine {

    fun calculateScore(job: JobOpportunity, profile: CandidateProfileEntity): OpportunityScore {
        val blockers = mutableListOf<String>()
        val unverifiedAspects = mutableListOf<String>()
        val positiveFactors = mutableListOf<String>()

        // 1. Hard Blockers Check
        if (!job.acceptsInternationalApplicants && profile.requiresSponsorship) {
            blockers.add("Employer explicitly excludes candidates requiring sponsorship.")
        }
        if (job.sponsorshipStatus == SponsorshipStatus.UNAVAILABLE && profile.requiresSponsorship) {
            blockers.add("Job states sponsorship unavailable / requires local unrestricted work authorization.")
        }
        if (job.isExpired) {
            blockers.add("Listing is marked expired.")
        }

        // 2. Visa Feasibility (0 - 100)
        var visaScore = when (job.sponsorshipStatus) {
            SponsorshipStatus.CONFIRMED -> 95
            SponsorshipStatus.CONDITIONAL -> 82
            SponsorshipStatus.POTENTIALLY_FRIENDLY -> 65
            SponsorshipStatus.UNCLEAR -> 40
            SponsorshipStatus.UNAVAILABLE -> 10
        }

        // Check relocation
        if (job.relocationAssistance) {
            visaScore = (visaScore + 5).coerceAtMost(100)
            positiveFactors.add("Employer provides active relocation assistance (${job.relocationDetails})")
        }

        // Check salary threshold compliance
        if (job.meetsVisaSalaryThreshold) {
            positiveFactors.add("Salary meets or exceeds national work visa threshold (${job.salaryThresholdDetail})")
        } else {
            visaScore = (visaScore - 15).coerceAtLeast(0)
            unverifiedAspects.add("Salary might be borderline for local statutory immigration thresholds.")
        }

        if (job.sponsorshipStatus == SponsorshipStatus.POTENTIALLY_FRIENDLY ||
            job.sponsorshipStatus == SponsorshipStatus.UNCLEAR
        ) {
            unverifiedAspects.add("Exact visa sponsorship sponsorship for this specific junior role remains unconfirmed by HR.")
        }

        // Check country preference match
        val preferredList = profile.preferredDestinations.split(",").map { it.trim().lowercase() }
        val matchesDestination = preferredList.any { it.contains(job.country.lowercase()) || job.country.lowercase().contains(it) }
        if (matchesDestination) {
            positiveFactors.add("${job.country} is in your preferred destinations list")
        } else if (preferredList.isNotEmpty()) {
            unverifiedAspects.add("${job.country} is outside your current preferred destinations list")
        }

        // 3. Technical Match (0 - 100)
        val candidateSkills = profile.keySkills.split(",").map { it.trim().lowercase() }
        var matchedSkillsCount = 0
        job.requiredSkills.forEach { skill ->
            if (candidateSkills.any { it.contains(skill.lowercase()) || skill.lowercase().contains(it) }) {
                matchedSkillsCount++
            }
        }

        val skillRatio = if (job.requiredSkills.isNotEmpty()) {
            matchedSkillsCount.toFloat() / job.requiredSkills.size.toFloat()
        } else {
            0.8f
        }

        var techScore = (skillRatio * 70f + 20f).toInt().coerceIn(20, 98)

        // Experience match check
        if (profile.yearsExperience >= job.minExperienceYears && profile.yearsExperience <= job.maxExperienceYears + 1) {
            techScore = (techScore + 10).coerceAtMost(100)
            positiveFactors.add("Your experience level (${profile.yearsExperience} yrs) matches early-career range (${job.minExperienceYears}-${job.maxExperienceYears} yrs)")
        } else if (profile.yearsExperience < job.minExperienceYears) {
            techScore = (techScore - 10).coerceAtLeast(10)
            unverifiedAspects.add("Job asks for ${job.minExperienceYears}+ yrs experience.")
        }

        // Education check
        if (profile.degree.isNotBlank()) {
            positiveFactors.add("Degree matches qualifying STEM requirement: ${job.educationRequirement}")
        }

        // 4. Combined Global Opportunity Score (0 - 100)
        // Weighted: 45% Visa Feasibility, 45% Technical Match, 10% Early Career Suitability
        val earlyCareerBonus = if (job.isGraduateProgram || job.jobLevel.contains("Graduate", ignoreCase = true) || job.jobLevel.contains("Junior", ignoreCase = true)) 10 else 5
        var globalScore = ((visaScore * 0.45f) + (techScore * 0.45f) + earlyCareerBonus).toInt().coerceIn(0, 100)

        if (blockers.isNotEmpty()) {
            // Cap score if hard blockers exist so it doesn't give a false green light
            globalScore = globalScore.coerceAtMost(35)
        }

        // Recommendation summary
        val recommendationReason = buildString {
            if (blockers.isNotEmpty()) {
                append("Immigration Caution: ${blockers.first()}")
            } else {
                append("Strong match: ")
                if (positiveFactors.isNotEmpty()) {
                    append(positiveFactors.take(2).joinToString("; "))
                    append(".")
                } else {
                    append("Early-career position with potential international mobility.")
                }
            }
        }

        return OpportunityScore(
            technicalMatch = techScore,
            visaFeasibility = visaScore,
            globalScore = globalScore,
            recommendationReason = recommendationReason,
            unverifiedAspects = unverifiedAspects,
            blockers = blockers
        )
    }
}

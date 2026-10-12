package com.example.data.model

enum class SponsorshipStatus(
    val code: String,
    val title: String,
    val shortDescription: String
) {
    CONFIRMED(
        "A",
        "Sponsorship Confirmed",
        "Employer explicitly confirms visa sponsorship/relocation for this role or hiring program."
    ),
    CONDITIONAL(
        "B",
        "Sponsorship with Conditions",
        "Sponsorship offered subject to salary threshold, degree discipline, or eligibility criteria."
    ),
    POTENTIALLY_FRIENDLY(
        "C",
        "Potentially Sponsor-Friendly",
        "Credible employer international hiring record, but specific role sponsorship is unverified."
    ),
    UNCLEAR(
        "D",
        "Sponsorship Unclear",
        "Insufficient public evidence available on visa support."
    ),
    UNAVAILABLE(
        "E",
        "Sponsorship Unavailable",
        "Employer explicitly states no visa sponsorship or requires unrestricted local work rights."
    )
}

data class SponsorshipEvidence(
    val evidenceUrl: String,
    val exactStatement: String,
    val sourceDate: String,
    val verificationDate: String,
    val confidenceLevel: Float, // 0.0 to 1.0
    val conditions: List<String> = emptyList()
)

data class JobOpportunity(
    val id: String,
    val title: String,
    val company: String,
    val city: String,
    val country: String,
    val region: String, // Europe, Asia-Pacific, North America
    val workModel: String, // Hybrid, On-site, Remote
    val jobLevel: String, // Graduate / Entry-Level, Junior (0-2 yrs), Associate, Early Career, Traineeship
    val isGraduateProgram: Boolean = false,
    val intakeSeason: String = "Immediate", // e.g. "Immediate", "Fall 2026", "Spring 2027"
    val salaryRange: String,
    val meetsVisaSalaryThreshold: Boolean = true,
    val salaryThresholdDetail: String = "",
    val originalUrl: String,
    val sourcePlatform: String, // e.g. "Careers Page", "Greenhouse ATS", "Lever ATS", "Make it in Germany", "LinkedIn Jobs"
    val sourceType: String = "Direct Employer",
    val sponsorshipStatus: SponsorshipStatus,
    val sponsorshipEvidence: SponsorshipEvidence,
    val visaSupportType: String, // e.g. "Visa Sponsorship & Relocation", "Visa Sponsorship Only"
    val relocationAssistance: Boolean = false,
    val relocationDetails: String = "",
    val acceptsInternationalApplicants: Boolean = true,
    val postedDate: String,
    val deadline: String = "Rolling Intake",
    val isExpired: Boolean = false,
    val requiredLanguages: List<String> = listOf("English (Fluent)"),
    val description: String,
    val requiredSkills: List<String>,
    val educationRequirement: String = "Bachelor's / Master's degree in STEM or Computer Science",
    val minExperienceYears: Int = 0,
    val maxExperienceYears: Int = 2
)

data class OpportunityScore(
    val technicalMatch: Int, // 0 - 100
    val visaFeasibility: Int, // 0 - 100
    val globalScore: Int, // 0 - 100
    val recommendationReason: String,
    val unverifiedAspects: List<String> = emptyList(),
    val blockers: List<String> = emptyList()
)

data class CountryVisaInfo(
    val countryName: String,
    val flagEmoji: String,
    val region: String,
    val primaryWorkVisa: String,
    val generalSalaryThreshold: String,
    val stemOrEarlyCareerThreshold: String,
    val generalQualifications: String,
    val permitProcessSummary: String,
    val officialGovUrl: String,
    val officialPortalName: String,
    val verifiedDate: String,
    val graduateStayBackRoute: String,
    val languageExpectation: String
)

data class ApplicationFilter(
    val query: String = "",
    val selectedCountries: Set<String> = emptySet(),
    val selectedSponsorshipStatuses: Set<SponsorshipStatus> = setOf(
        SponsorshipStatus.CONFIRMED,
        SponsorshipStatus.CONDITIONAL,
        SponsorshipStatus.POTENTIALLY_FRIENDLY
    ),
    val selectedJobLevels: Set<String> = emptySet(),
    val selectedWorkModels: Set<String> = emptySet(),
    val graduateProgramsOnly: Boolean = false,
    val relocationOnly: Boolean = false,
    val minGlobalScore: Int = 0
)

data class DailyDigest(
    val date: String,
    val newVacanciesCount: Int,
    val confirmedSponsorshipsCount: Int,
    val featuredCompanies: List<String>,
    val topDestinations: List<String>,
    val highlights: String
)

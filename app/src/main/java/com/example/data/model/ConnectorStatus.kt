package com.example.data.model

data class JobSourceConnector(
    val id: String,
    val name: String,
    val category: String, // "Direct Employer", "Public Government Portal", "Public ATS Feed", "Aggregator API"
    val isOperational: Boolean,
    val authStatus: String, // "Operational (Permitted Direct URL)", "Unprovisioned / OAuth 2.0 Credentials Required", "Restricted Access"
    val officialPolicyNote: String,
    val supportedCountries: List<String>
)

object SourceConnectorsData {
    val connectors: List<JobSourceConnector> = listOf(
        JobSourceConnector(
            id = "direct-employer",
            name = "Direct Employer Career Portals",
            category = "Direct Employer",
            isOperational = true,
            authStatus = "Operational (Permitted Direct Links)",
            officialPolicyNote = "Direct links to employer job vacancies (ASML, Booking.com, SAP, Spotify, Rakuten, Canva, Stripe, Tesco, Siemens). Applications are prepared locally and submitted through official employer pages.",
            supportedCountries = listOf("Germany", "Netherlands", "United Kingdom", "Ireland", "Japan", "Australia", "Canada", "Sweden")
        ),
        JobSourceConnector(
            id = "gov-portal-germany",
            name = "Make it in Germany (Federal Portal)",
            category = "Public Government Portal",
            isOperational = true,
            authStatus = "Operational (Permitted Public Data)",
            officialPolicyNote = "German Federal Government portal for international skilled workers and EU Blue Card opportunities. Free, legitimate public listings.",
            supportedCountries = listOf("Germany")
        ),
        JobSourceConnector(
            id = "ats-greenhouse-lever",
            name = "Public ATS Boards (Greenhouse & Lever)",
            category = "Public ATS Feed",
            isOperational = true,
            authStatus = "Operational (Permitted Public Vacancy URLs)",
            officialPolicyNote = "Direct, unauthenticated public job listings hosted on employer Greenhouse and Lever portals. No restricted API scraping.",
            supportedCountries = listOf("Worldwide", "Netherlands", "United Kingdom", "Ireland", "Australia", "Canada")
        ),
        JobSourceConnector(
            id = "linkedin-jobs",
            name = "LinkedIn Jobs Automated API",
            category = "Aggregator API",
            isOperational = false,
            authStatus = "Unavailable / OAuth 2.0 Partner API Required",
            officialPolicyNote = "Automated application submission and unauthorized scraping are strictly prohibited under LinkedIn User Agreement §8.2. Official automated integration requires LinkedIn Talent Solutions Partner API approval and enterprise OAuth 2.0 credentials (unprovisioned). JobPilot AI safely routes users to verified direct company links instead.",
            supportedCountries = listOf("Worldwide")
        ),
        JobSourceConnector(
            id = "indeed-jobs",
            name = "Indeed Publisher & Apply API",
            category = "Aggregator API",
            isOperational = false,
            authStatus = "Unavailable / Publisher API Secret Required",
            officialPolicyNote = "Indeed programmatic job search and direct apply require Indeed Publisher / Employer API Client credentials (unprovisioned). JobPilot AI uses direct employer career portals to avoid third-party aggregator restrictions.",
            supportedCountries = listOf("Worldwide")
        )
    )
}

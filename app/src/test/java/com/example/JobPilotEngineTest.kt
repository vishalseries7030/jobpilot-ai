package com.example

import com.example.data.local.CandidateProfileEntity
import com.example.data.model.CountryData
import com.example.data.model.SampleJobsData
import com.example.data.model.SourceConnectorsData
import com.example.data.model.SponsorshipStatus
import com.example.service.ApplicationEngine
import com.example.service.ScoringEngine
import com.example.service.VerificationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JobPilotEngineTest {

    private val testCandidate = CandidateProfileEntity(
        fullName = "Alex Chen",
        residenceCountry = "India",
        citizenshipCountry = "India",
        requiresSponsorship = true,
        degree = "B.Tech in Computer Science",
        keySkills = "Python, Kotlin, Docker, Git, SQL",
        yearsExperience = 1.0,
        dailyApplicationLimit = 5,
        applicationsSubmittedToday = 1,
        resumeFileName = "alex_chen_cv_2026.pdf",
        resumeContent = "B.Tech Computer Science graduate with Python & Kotlin experience."
    )

    @Test
    fun testConfirmedSponsorshipScoring() {
        val asmlJob = SampleJobsData.initialJobs.first { it.company == "ASML" }
        val candidateWithEmbedded = testCandidate.copy(keySkills = "C++, Python, Linux, Algorithms, Docker")
        val score = ScoringEngine.calculateScore(asmlJob, candidateWithEmbedded)

        assertEquals(SponsorshipStatus.CONFIRMED, asmlJob.sponsorshipStatus)
        assertTrue("Visa feasibility should be very high for confirmed sponsorship", score.visaFeasibility >= 90)
        assertTrue("Global score should be >= 75 for matching skills", score.globalScore >= 75)
        assertTrue("Should not have hard blockers", score.blockers.isEmpty())
    }

    @Test
    fun testUnavailableSponsorshipBlockerDetection() {
        val defenseJob = SampleJobsData.initialJobs.first { it.company == "AeroDefense Dynamics" }
        val score = ScoringEngine.calculateScore(defenseJob, testCandidate)

        assertEquals(SponsorshipStatus.UNAVAILABLE, defenseJob.sponsorshipStatus)
        assertTrue("Should detect hard immigration blocker", score.blockers.isNotEmpty())
        assertTrue("Global score should be capped due to blocker", score.globalScore <= 35)
    }

    @Test
    fun test12StepApplicationAudit() {
        val sapJob = SampleJobsData.initialJobs.first { it.company == "SAP SE" }
        val steps = ApplicationEngine.execute12StepAudit(sapJob, testCandidate, overrideBlocker = false)

        assertEquals(12, steps.size)
        // Step 1: Vacancy verified
        assertEquals(VerificationStatus.PASSED, steps[0].status)
        // Step 4: Sponsorship audit passed
        assertEquals(VerificationStatus.PASSED, steps[3].status)
        // Step 8: Blocker scan passed
        assertEquals(VerificationStatus.PASSED, steps[7].status)
        // Step 11: Quota check passed (1/5 used)
        assertEquals(VerificationStatus.PASSED, steps[10].status)
    }

    @Test
    fun testDailyApplicationLimitEnforcement() {
        val sapJob = SampleJobsData.initialJobs.first { it.company == "SAP SE" }
        val maxedCandidate = testCandidate.copy(applicationsSubmittedToday = 5, dailyApplicationLimit = 5)
        val steps = ApplicationEngine.execute12StepAudit(sapJob, maxedCandidate, overrideBlocker = false)

        // Step 11 should be BLOCKED because daily quota (5/5) is reached
        assertEquals(VerificationStatus.BLOCKED, steps[10].status)
        assertTrue(steps[10].details.contains("0 remaining today"))
    }

    @Test
    fun testTruthfulDocumentGeneration() {
        val bookingJob = SampleJobsData.initialJobs.first { it.company == "Booking.com" }
        val bundle = ApplicationEngine.generateDocuments(bookingJob, testCandidate)

        // Check truthfulness: never claims local authorization
        assertTrue("Cover letter must mention sponsorship need", bundle.coverLetter.contains("require employer-sponsored work authorization"))
        assertTrue("Cover letter must mention actual citizenship", bundle.coverLetter.contains("India"))

        // Check screening Q&A
        val sponsorshipQ = bundle.screeningQAs.first { it.question.contains("employer sponsorship") }
        assertEquals("Yes", sponsorshipQ.answer)

        val legalAuthQ = bundle.screeningQAs.first { it.question.contains("legally authorized") }
        assertTrue("Must truthfully answer requiring sponsorship", legalAuthQ.answer.contains("Require work visa sponsorship"))
    }

    @Test
    fun testCountryDataIntegrity() {
        val germany = CountryData.getCountryInfo("Germany")
        assertNotNull(germany)
        assertTrue(germany!!.primaryWorkVisa.contains("EU Blue Card"))
        assertTrue(germany.stemOrEarlyCareerThreshold.contains("41,041"))
        assertNotNull(germany.officialGovUrl)

        val netherlands = CountryData.getCountryInfo("Netherlands")
        assertNotNull(netherlands)
        assertTrue(netherlands!!.primaryWorkVisa.contains("Kennismigrant"))

        val japan = CountryData.getCountryInfo("Japan")
        assertNotNull(japan)
        assertTrue(japan!!.primaryWorkVisa.contains("Engineer"))

        val uk = CountryData.getCountryInfo("United Kingdom")
        assertNotNull(uk)
        assertTrue(uk!!.primaryWorkVisa.contains("Skilled Worker"))
        assertTrue(uk.stemOrEarlyCareerThreshold.contains("30,960"))

        val australia = CountryData.getCountryInfo("Australia")
        assertNotNull(australia)
        assertTrue(australia!!.generalSalaryThreshold.contains("73,150"))

        val usa = CountryData.getCountryInfo("United States")
        assertNotNull(usa)
        assertTrue(usa!!.primaryWorkVisa.contains("H-1B"))
    }

    @Test
    fun testInternationalSearchAcrossTargetCountries() {
        val targetCountries = listOf("Germany", "Japan", "Australia", "United Kingdom", "United States")
        val allJobs = SampleJobsData.initialJobs

        targetCountries.forEach { country ->
            val jobsInCountry = allJobs.filter { it.country.equals(country, ignoreCase = true) }
            assertTrue("Should have real verified vacancies in $country", jobsInCountry.isNotEmpty())
            val job = jobsInCountry.first()
            assertNotNull(job.title)
            assertNotNull(job.company)
            assertNotNull(job.salaryRange)
            assertNotNull(job.originalUrl)
            assertNotNull(job.sponsorshipEvidence)
        }
    }

    @Test
    fun testConnectorsAuditStatuses() {
        val connectors = SourceConnectorsData.connectors
        val linkedIn = connectors.first { it.id == "linkedin-jobs" }
        assertFalse("LinkedIn API must be marked unoperational/limited without credentials", linkedIn.isOperational)
        assertTrue(linkedIn.authStatus.contains("OAuth 2.0 Partner API Required"))

        val indeed = connectors.first { it.id == "indeed-jobs" }
        assertFalse("Indeed API must be marked unoperational/limited without credentials", indeed.isOperational)

        val directEmployer = connectors.first { it.id == "direct-employer" }
        assertTrue("Direct employer portals must be operational", directEmployer.isOperational)
    }
}

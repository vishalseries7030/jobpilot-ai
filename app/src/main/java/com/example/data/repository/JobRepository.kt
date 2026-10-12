package com.example.data.repository

import com.example.data.local.ApplicationRecordEntity
import com.example.data.local.CandidateProfileEntity
import com.example.data.local.JobPilotDao
import com.example.data.local.SavedJobEntity
import com.example.data.model.CountryData
import com.example.data.model.CountryVisaInfo
import com.example.data.model.DailyDigest
import com.example.data.model.JobOpportunity
import com.example.data.model.SampleJobsData
import com.example.data.model.SponsorshipStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JobRepository(private val dao: JobPilotDao) {

    private val allJobsList = SampleJobsData.initialJobs.toMutableList()

    fun getAllJobs(): List<JobOpportunity> = allJobsList

    fun getJobById(id: String): JobOpportunity? = allJobsList.firstOrNull { it.id == id }

    val candidateProfile: Flow<CandidateProfileEntity> = dao.getCandidateProfile().map { profile ->
        profile ?: CandidateProfileEntity().also { defaultProfile ->
            dao.saveCandidateProfile(defaultProfile)
        }
    }

    suspend fun saveProfile(profile: CandidateProfileEntity) {
        dao.saveCandidateProfile(profile)
    }

    val allApplications: Flow<List<ApplicationRecordEntity>> = dao.getAllApplications()

    suspend fun getApplicationByJobId(jobId: String): ApplicationRecordEntity? {
        return dao.getApplicationByJobId(jobId)
    }

    fun getApplicationFlowByJobId(jobId: String): Flow<ApplicationRecordEntity?> {
        return dao.getApplicationFlowByJobId(jobId)
    }

    suspend fun recordApplication(record: ApplicationRecordEntity): Long {
        return dao.insertApplication(record)
    }

    suspend fun updateApplication(record: ApplicationRecordEntity) {
        dao.updateApplication(record)
    }

    suspend fun deleteApplication(id: Long) {
        dao.deleteApplicationById(id)
    }

    val savedJobIds: Flow<Set<String>> = dao.getAllSavedJobs().map { list ->
        list.map { it.jobId }.toSet()
    }

    suspend fun toggleSaveJob(jobId: String, currentlySaved: Boolean) {
        if (currentlySaved) {
            dao.deleteSavedJob(jobId)
        } else {
            dao.saveJob(SavedJobEntity(jobId = jobId))
        }
    }

    fun getSupportedCountries(): List<CountryVisaInfo> = CountryData.supportedCountries

    fun getCountryVisaInfo(name: String): CountryVisaInfo? = CountryData.getCountryInfo(name)

    fun getDailyDigest(): DailyDigest {
        val confirmedCount = allJobsList.count { it.sponsorshipStatus == SponsorshipStatus.CONFIRMED }
        val featured = allJobsList.take(4).map { it.company }
        val destinations = listOf("Germany", "Netherlands", "United Kingdom", "Japan", "Australia")
        return DailyDigest(
            date = "Today's Global Digest",
            newVacanciesCount = allJobsList.size,
            confirmedSponsorshipsCount = confirmedCount,
            featuredCompanies = featured,
            topDestinations = destinations,
            highlights = "Discovered $confirmedCount confirmed sponsorship early-career roles across Europe and Asia-Pacific. ASML, Booking.com, and Rakuten are actively accepting international university graduates for 2026/2027 intakes."
        )
    }
}

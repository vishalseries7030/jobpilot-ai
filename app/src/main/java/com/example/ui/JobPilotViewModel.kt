package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ApplicationRecordEntity
import com.example.data.local.CandidateProfileEntity
import com.example.data.local.JobPilotDatabase
import com.example.data.model.ApplicationFilter
import com.example.data.model.CountryVisaInfo
import com.example.data.model.DailyDigest
import com.example.data.model.JobOpportunity
import com.example.data.model.OpportunityScore
import com.example.data.model.SponsorshipStatus
import com.example.data.repository.JobRepository
import com.example.service.ScoringEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScoredJob(
    val job: JobOpportunity,
    val score: OpportunityScore
)

class JobPilotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JobRepository

    init {
        val db = JobPilotDatabase.getInstance(application)
        repository = JobRepository(db.jobPilotDao())
    }

    val candidateProfile: StateFlow<CandidateProfileEntity> = repository.candidateProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CandidateProfileEntity()
        )

    val savedJobIds: StateFlow<Set<String>> = repository.savedJobIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val allApplications: StateFlow<List<ApplicationRecordEntity>> = repository.allApplications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _filterState = MutableStateFlow(ApplicationFilter())
    val filterState: StateFlow<ApplicationFilter> = _filterState.asStateFlow()

    private val _activePreset = MutableStateFlow("All")
    val activePreset: StateFlow<String> = _activePreset.asStateFlow()

    // Dialog navigation states
    private val _selectedJobForEvidence = MutableStateFlow<JobOpportunity?>(null)
    val selectedJobForEvidence: StateFlow<JobOpportunity?> = _selectedJobForEvidence.asStateFlow()

    private val _selectedCountryForInfo = MutableStateFlow<CountryVisaInfo?>(null)
    val selectedCountryForInfo: StateFlow<CountryVisaInfo?> = _selectedCountryForInfo.asStateFlow()

    private val _selectedJobForApplication = MutableStateFlow<JobOpportunity?>(null)
    val selectedJobForApplication: StateFlow<JobOpportunity?> = _selectedJobForApplication.asStateFlow()

    val dailyDigest: DailyDigest = repository.getDailyDigest()
    val supportedCountries: List<CountryVisaInfo> = repository.getSupportedCountries()

    // Scored and filtered jobs
    val displayedJobs: StateFlow<List<ScoredJob>> = combine(
        candidateProfile,
        _filterState
    ) { profile, filter ->
        val rawJobs = repository.getAllJobs()

        rawJobs.map { job ->
            val score = ScoringEngine.calculateScore(job, profile)
            ScoredJob(job, score)
        }.filter { scored ->
            val job = scored.job
            val score = scored.score

            // Query match
            val matchesQuery = filter.query.isBlank() ||
                    job.title.contains(filter.query, ignoreCase = true) ||
                    job.company.contains(filter.query, ignoreCase = true) ||
                    job.city.contains(filter.query, ignoreCase = true) ||
                    job.country.contains(filter.query, ignoreCase = true) ||
                    job.requiredSkills.any { it.contains(filter.query, ignoreCase = true) }

            // Country filter
            val matchesCountry = filter.selectedCountries.isEmpty() ||
                    filter.selectedCountries.contains(job.country)

            // Sponsorship status filter
            val matchesSponsorship = filter.selectedSponsorshipStatuses.isEmpty() ||
                    filter.selectedSponsorshipStatuses.contains(job.sponsorshipStatus)

            // Graduate programs toggle
            val matchesGraduate = !filter.graduateProgramsOnly || job.isGraduateProgram

            // Relocation assistance toggle
            val matchesRelocation = !filter.relocationOnly || job.relocationAssistance

            // Min score filter
            val matchesScore = score.globalScore >= filter.minGlobalScore

            matchesQuery && matchesCountry && matchesSponsorship && matchesGraduate && matchesRelocation && matchesScore
        }.sortedByDescending { it.score.globalScore }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun applyPreset(presetName: String) {
        _activePreset.value = presetName
        when (presetName) {
            "Worldwide" -> {
                _filterState.value = ApplicationFilter()
            }
            "Sponsorship Verified" -> {
                _filterState.value = ApplicationFilter(
                    selectedSponsorshipStatuses = setOf(SponsorshipStatus.CONFIRMED, SponsorshipStatus.CONDITIONAL)
                )
            }
            "Europe" -> {
                val europeanCountries = setOf("Germany", "Netherlands", "United Kingdom", "Ireland", "Sweden", "Switzerland", "France")
                _filterState.value = ApplicationFilter(selectedCountries = europeanCountries)
            }
            "Germany" -> {
                _filterState.value = ApplicationFilter(selectedCountries = setOf("Germany"))
            }
            "Japan" -> {
                _filterState.value = ApplicationFilter(selectedCountries = setOf("Japan"))
            }
            "Australia" -> {
                _filterState.value = ApplicationFilter(selectedCountries = setOf("Australia"))
            }
            "UK & Ireland" -> {
                _filterState.value = ApplicationFilter(selectedCountries = setOf("United Kingdom", "Ireland"))
            }
            "USA & Canada" -> {
                _filterState.value = ApplicationFilter(selectedCountries = setOf("United States", "Canada"))
            }
            "Graduate Programs" -> {
                _filterState.value = ApplicationFilter(graduateProgramsOnly = true)
            }
            "Relocation Opportunities" -> {
                _filterState.value = ApplicationFilter(relocationOnly = true)
            }
            else -> {
                _filterState.value = ApplicationFilter()
            }
        }
    }

    fun toggleCountryFilter(country: String) {
        val current = _filterState.value.selectedCountries.toMutableSet()
        if (current.contains(country)) current.remove(country) else current.add(country)
        _filterState.value = _filterState.value.copy(selectedCountries = current)
    }

    fun toggleSponsorshipFilter(status: SponsorshipStatus) {
        val current = _filterState.value.selectedSponsorshipStatuses.toMutableSet()
        if (current.contains(status)) current.remove(status) else current.add(status)
        _filterState.value = _filterState.value.copy(selectedSponsorshipStatuses = current)
    }

    fun toggleGraduateProgramsOnly(enabled: Boolean) {
        _filterState.value = _filterState.value.copy(graduateProgramsOnly = enabled)
    }

    fun toggleRelocationOnly(enabled: Boolean) {
        _filterState.value = _filterState.value.copy(relocationOnly = enabled)
    }

    fun resetFilters() {
        _filterState.value = ApplicationFilter()
        _activePreset.value = "Worldwide"
    }

    // Toggle saved job
    fun toggleSaveJob(jobId: String) {
        viewModelScope.launch {
            val isSaved = savedJobIds.value.contains(jobId)
            repository.toggleSaveJob(jobId, isSaved)
        }
    }

    // Dialog handlers
    fun openEvidenceDialog(job: JobOpportunity) {
        _selectedJobForEvidence.value = job
    }

    fun closeEvidenceDialog() {
        _selectedJobForEvidence.value = null
    }

    fun openCountryDialog(countryName: String) {
        _selectedCountryForInfo.value = repository.getCountryVisaInfo(countryName)
    }

    fun openCountryDialog(info: CountryVisaInfo) {
        _selectedCountryForInfo.value = info
    }

    fun closeCountryDialog() {
        _selectedCountryForInfo.value = null
    }

    fun openApplicationWorkflow(job: JobOpportunity) {
        _selectedJobForApplication.value = job
    }

    fun closeApplicationWorkflow() {
        _selectedJobForApplication.value = null
    }

    // Submit / Record application
    fun submitApplication(record: ApplicationRecordEntity) {
        viewModelScope.launch {
            repository.recordApplication(record)
            // Increment today's count in candidate profile
            val currentProfile = candidateProfile.value
            val updated = currentProfile.copy(
                applicationsSubmittedToday = currentProfile.applicationsSubmittedToday + 1
            )
            repository.saveProfile(updated)
        }
    }

    fun updateApplicationStatus(record: ApplicationRecordEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateApplication(record.copy(status = newStatus))
        }
    }

    fun updateApplicationNotes(record: ApplicationRecordEntity, newNotes: String) {
        viewModelScope.launch {
            repository.updateApplication(record.copy(recruiterNotes = newNotes))
        }
    }

    fun deleteApplication(id: Long) {
        viewModelScope.launch {
            repository.deleteApplication(id)
        }
    }

    fun updateProfile(profile: CandidateProfileEntity) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }
}

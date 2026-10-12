package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobPilotDao {
    // Candidate Profile
    @Query("SELECT * FROM candidate_profile WHERE id = 1 LIMIT 1")
    fun getCandidateProfile(): Flow<CandidateProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCandidateProfile(profile: CandidateProfileEntity)

    // Application Records
    @Query("SELECT * FROM application_records ORDER BY createdTimestamp DESC")
    fun getAllApplications(): Flow<List<ApplicationRecordEntity>>

    @Query("SELECT * FROM application_records WHERE jobId = :jobId LIMIT 1")
    suspend fun getApplicationByJobId(jobId: String): ApplicationRecordEntity?

    @Query("SELECT * FROM application_records WHERE jobId = :jobId LIMIT 1")
    fun getApplicationFlowByJobId(jobId: String): Flow<ApplicationRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(record: ApplicationRecordEntity): Long

    @Update
    suspend fun updateApplication(record: ApplicationRecordEntity)

    @Query("DELETE FROM application_records WHERE id = :id")
    suspend fun deleteApplicationById(id: Long)

    // Saved Jobs
    @Query("SELECT * FROM saved_jobs")
    fun getAllSavedJobs(): Flow<List<SavedJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveJob(savedJob: SavedJobEntity)

    @Query("DELETE FROM saved_jobs WHERE jobId = :jobId")
    suspend fun deleteSavedJob(jobId: String)
}
